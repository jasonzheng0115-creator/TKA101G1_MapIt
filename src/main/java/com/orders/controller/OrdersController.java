package com.orders.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cust.model.CustVO;
import com.orders.model.OrdersRepository;
import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;
import com.prod.model.CartVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrdersController {
	
	@Autowired
	private OrdersService ordersSvc;
	
	// 讓 OrdersController 也能去 Redis 撈購物車資料
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private ProdService prodSvc;
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	
	// 核心 API：前端送出購物車資料結帳
	// 網址：POST http://localhost:8080/orders/checkout
	// @RequestBody ➔ 強迫 Spring Boot 將前端傳過來的 JSON 字串，自動翻譯成 OrdersVO 物件
	// @Valid ➔ 啟動我們之前在 OrdersVO 身上貼的 @NotNull、@NotEmpty 表單驗證擋板
	// ResponseEntity 作為控制器的回傳格式，因為它能同時控制「HTTP 狀態碼（Status Code）」與「回應內容（Body）」
	
	@PostMapping("/checkout")
	@ResponseBody  //確保方法可以吐出JSON資料
	public ResponseEntity<?> checkout(
			@Valid @RequestBody OrdersVO ordersVO,
			HttpSession session) {
		
		try {
			
			// 將前端打包好的訂單與明細，直接送進 Service 大腦執行（扣庫存、增銷量、級聯存檔）
			OrdersVO savedOrder = ordersSvc.insertOrder(ordersVO);
			
			// 結帳成功，清除 Redis 購物車
	        com.cust.model.CustVO loginCust = (com.cust.model.CustVO) session.getAttribute("loginCust");
	        if (loginCust != null) {
	            String key = "cart:member:" + loginCust.getCustId();
	            
	            // 刪除這個會員的 Redis 購物車
	            redisTemplate.delete(key);
	            
	            // 保留：如果以後有做「勾選部分商品結帳」，就要用指定的 field 刪除：
	            // redisTemplate.opsForHash().delete(key, 商品ID);
	        }
			
			// 結帳成功：回傳 HTTP 200 狀態碼，並把存完檔、帶有最新 ORDER_ID 的訂單物件當成 JSON 吐回前端
			return ResponseEntity.ok(savedOrder);
			
		} catch (IllegalArgumentException | IllegalStateException e) {
			
			// 失敗（例如：庫存不足、購物車空了）：回傳 HTTP 400 錯誤，並附上失敗原因
			return ResponseEntity.badRequest().body(e.getMessage());
			
		} catch (Exception e) {
			
			// 系統崩潰（例如：資料庫斷線）：回傳 HTTP 500 伺服器錯誤
			return ResponseEntity.status(500).body("系統結帳異常，請稍後再試！錯誤資訊：" + e.getMessage());
		}
	}
	
	// 擴充 API：後端或會員取消訂單
	// 網址：POST http://localhost:8080/orders/cancel
	// 封裝一個簡單的請求容器來接前端傳過來的引數
	public static class CancelRequest {
		private Integer orderId;
		private String cancelReason;
		
		//  提供標準的 getter/setter 讓 Jackson 轉換 JSON
		public Integer getOrderId() {
			return orderId;
		}
		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}
		public String getCancelReason() {
			return cancelReason;
		}
		public void setCancelReason(String cancelReason) {
			this.cancelReason = cancelReason;
		}	
	}
	
	@PostMapping("/cancel")
	@ResponseBody //確保方法可以吐出JSON資料
	public ResponseEntity<?> cancel(@RequestBody CancelRequest request) {
		try {
			
			// 1. 呼叫 Service 逆向工程：退庫存、扣銷量、改狀態
			OrdersVO updatedOrder = ordersSvc.cancelOrder(request.getOrderId(), request.getCancelReason());
			
			// 2. 取消成功：回傳 HTTP 200，並附帶更新後的訂單狀態
			return ResponseEntity.ok(updatedOrder);
			
		} catch (IllegalArgumentException | IllegalStateException e) {	
			
			// 3. 攔截業務異常（例如：重複取消、找不到訂單）：回傳 HTTP 400
			return ResponseEntity.badRequest().body(e.getMessage());
			
		} catch (Exception e) {
			
			// 4. 系統級異常：回傳 HTTP 500
			return ResponseEntity.status(500).body("取消訂單異常，原因：" + e.getMessage());
		}
	}
	
	
	// 1. 去 Redis 撈出商品 2. 計算總金額 3. 帶去checkout.html
	// HttpSession session 會員登入
	@GetMapping("/showCheckout")
	public String showCheckout(ModelMap model, HttpSession session) {
		
		// 從 Session 拿出登入成功的會員物件
	    CustVO loginCust = (CustVO) session.getAttribute("loginCust");
	    
	    // 如果發現根本沒登入，強迫導向登入頁面
	    if (loginCust == null) {
	        return "redirect:/customer/login";
	    }
	    
	    // 動態組合 Redis Key  拿當前會員的真實 ID 來當成 Key
	    String key = "cart:member:" + loginCust.getCustId();
		
		// 去 Redis 把購物車裡的所有 {商品ID: 數量} 倒出來
		Map<Object, Object> redisCart = redisTemplate.opsForHash().entries(key);
		
		// 轉換資料（同購物車）
		List<CartVO> cartList = redisCart.entrySet().stream()
				.map(entry -> {
					Integer productId = Integer.parseInt((String) entry.getKey());
					Integer qty = Integer.parseInt((String) entry.getValue());
					ProdVO product = prodSvc.getOneProd(productId);
					return (product != null) ? new CartVO(product, qty) : null;
				})
				.filter(Objects::nonNull) // 過濾失效或被下架的商品
				.collect(Collectors.toList());
		
		// 計算總金額
		int totalAmount = cartList.stream()
				.mapToInt(com.prod.model.CartVO::getSubtotal)
				.sum();
		
		// 讓 checkout.html 的導覽列抓到名字
	    model.addAttribute("userName", loginCust.getCustName());
	    
	    // 把整筆會員資料傳給前端，讓 JavaScript 可以動態讀取姓名與電話
	    model.addAttribute("loginCust", loginCust);
		
		// 把資料裝進箱子，送去網頁
		model.addAttribute("cartList", cartList);
		model.addAttribute("totalAmount", totalAmount);
		
		// 導向網頁
		return "front-end/cart/checkout";
	}
	
	// 導向「訂單成立與成功收據」頁面
	// 網址：GET http://localhost:8080/orders/success?orderId=10025
	@GetMapping("/success")
	public String showSuccess(
			@RequestParam("orderId") Integer orderId, 
			ModelMap model) {
		
		// 用ID去資料庫拿出訂單主檔
		OrdersVO order = ordersSvc.getOneOrders(orderId); // 對齊 OrdersService 撈單筆的方法名稱
		
		// 把整筆訂單裝箱送去成功頁面
		model.addAttribute("order", order);
		
		// 導向成功的頁面
		return "front-end/cart/success";
	}
	
	// 前台會員歷史訂單
	// 網址：GET http://localhost:8080/orders/my-orders
	@GetMapping("/my-orders")
	public String showMyOrders(HttpSession session, ModelMap model) {
		//從 session 取得真正登入的會員
		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		if (loginCust == null) {
			return "redirect:/customer/login"; //如果沒登入，踢回登入頁
		}
		//將名字塞進 Model，這樣導覽列上角顯示OOO 你好
		model.addAttribute("userName", loginCust.getCustName());
		//用會員ID去查歷史訂單
		Integer currentCustId = loginCust.getCustId(); 
		List<OrdersVO> list = ordersSvc.getOrdersByCustId(currentCustId);
		// 訂單裝給 Thymeleaf 渲染
		model.addAttribute("ordersList", list);
		// 導向前台列表
		return "front-end/cart/order_list";
	}
	
	
	// 後台訂單列表
	@GetMapping("/backend-list")
	public String showBackendOrders(ModelMap model) {
		List<OrdersVO> list = ordersRepository.findAll();
		model.addAttribute("allOrdersList", list);
		return "back-end/orders/order_management"; // 導向後台html
	}
	
	@PostMapping("/backend-cancel")
	@ResponseBody
	public ResponseEntity<?> backendCancelOrder(
			@RequestParam("orderId") Integer orderId,
			@RequestParam("cancelReason") String cancelReason) {
		try {
			// Service的取消訂單逆向邏輯
			OrdersVO updatedOrder = ordersSvc.cancelOrder(orderId, cancelReason);
			return ResponseEntity.ok(updatedOrder);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("取消失敗" + e.getMessage());
		}
		
	}
	
	
}




// "checkout" 跟 "cancel" 部分 改回上方@Controller
//@RestController ＝ @Controller ＋ 所有方法都自動加上 @ResponseBody
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.orders.model.OrdersService;
//import com.orders.model.OrdersVO;
//
//import jakarta.validation.Valid;
//
//
//@RestController
//@RequestMapping("/orders")
//public class OrdersController {
//	
//	@Autowired
//	private OrdersService ordersSvc;
//	
//	// 核心 API：前端送出購物車資料結帳
//	// 網址：POST http://localhost:8080/orders/checkout
//	// @RequestBody ➔ 強迫 Spring Boot 將前端傳過來的 JSON 字串，自動翻譯成 OrdersVO 物件
//	// @Valid ➔ 啟動我們之前在 OrdersVO 身上貼的 @NotNull、@NotEmpty 表單驗證擋板
//	// ResponseEntity 作為控制器的回傳格式，因為它能同時控制「HTTP 狀態碼（Status Code）」與「回應內容（Body）」
//	@PostMapping("/checkout")
//	public ResponseEntity<?> checkout(@Valid @RequestBody OrdersVO ordersVO) {
//		
//		try {
//			
//			// 1. 將前端打包好的訂單與明細，直接送進 Service 大腦執行（扣庫存、增銷量、級聯存檔）
//			OrdersVO savedOrder = ordersSvc.insertOrder(ordersVO);
//			// 2. 結帳成功：回傳 HTTP 200 狀態碼，並把存完檔、帶有最新 ORDER_ID 的訂單物件當成 JSON 吐回前端
//			return ResponseEntity.ok(savedOrder);
//			
//		} catch (IllegalArgumentException | IllegalStateException e) {
//			
//			// 3. 商業邏輯失敗（例如：庫存不足、購物車空了）：回傳 HTTP 400 錯誤，並附上失敗原因
//			return ResponseEntity.badRequest().body(e.getMessage());
//			
//		} catch (Exception e) {
//			
//			// 4. 系統崩潰（例如：資料庫斷線）：回傳 HTTP 500 伺服器錯誤
//			return ResponseEntity.status(500).body("系統結帳異常，請稍後再試！錯誤資訊：" + e.getMessage());
//		}
//	}
//	
//	// 擴充 API：後端或會員取消訂單
//	// 網址：POST http://localhost:8080/orders/cancel
//	// 封裝一個簡單的請求容器來接前端傳過來的引數
//	public static class CancelRequest {
//		private Integer orderId;
//		private String cancelReason;
//		
//		//  提供標準的 getter/setter 讓 Jackson 轉換 JSON
//		public Integer getOrderId() {
//			return orderId;
//		}
//		public void setOrderId(Integer orderId) {
//			this.orderId = orderId;
//		}
//		public String getCancelReason() {
//			return cancelReason;
//		}
//		public void setCancelReason(String cancelReason) {
//			this.cancelReason = cancelReason;
//		}	
//	}
//	
//	@PostMapping("/cancel")
//	public ResponseEntity<?> cancel(@RequestBody CancelRequest request) {
//		try {
//			
//			// 1. 呼叫 Service 逆向工程：退庫存、扣銷量、改狀態
//			OrdersVO updatedOrder = ordersSvc.cancelOrder(request.getOrderId(), request.getCancelReason());
//			
//			// 2. 取消成功：回傳 HTTP 200，並附帶更新後的訂單狀態
//			return ResponseEntity.ok(updatedOrder);
//			
//		} catch (IllegalArgumentException | IllegalStateException e) {	
//			
//			// 3. 攔截業務異常（例如：重複取消、找不到訂單）：回傳 HTTP 400
//			return ResponseEntity.badRequest().body(e.getMessage());
//			
//		} catch (Exception e) {
//			
//			// 4. 系統級異常：回傳 HTTP 500
//			return ResponseEntity.status(500).body("取消訂單異常，原因：" + e.getMessage());
//		}
//	}
//}
