package com.prod.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cust.model.CustVO;
import com.prod.model.CartVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private ProdService prodSvc;
	
	@Autowired
	private StringRedisTemplate redisTemplate; // Spring Boot 內建 Redis
	
	// 定義會員跟旅客的key
	private String getCartKey(HttpSession session) {
		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		
		if (loginCust != null) {
	        return "cart:member:" + loginCust.getCustId();
	    } else {
	        return "cart:guest:" + session.getId();
	    }
	}
	
	// 加入購物車 (數量自動累加)
	@PostMapping("/add")
	public String addToCart(
		@RequestParam("productId") Integer productId, 
		@RequestParam("quantity") Integer quantity,
		HttpSession session) {
		
		// 防止被繞過前端的數量限制
		if (productId == null || quantity == null || quantity <= 0) {
		    return "redirect:/cart/show";
		}
		
		String key = getCartKey(session);  // key代表購物車的ID
		String field = String.valueOf(productId); // field代表這個購物車裡的東西
		ProdVO product = prodSvc.getOneProd(productId); // product代表商品所有資料=商品物件
		
		if (product != null) {  // product != null表示該商品有資料存在，非下架狀態或不存在的狀態
			// 取出"key"的購物車資料
			String currentQtyStr = (String) redisTemplate.opsForHash().get(key, field);
			int currentQty = (currentQtyStr != null) ? Integer.parseInt(currentQtyStr) : 0;
			
			// 庫存數量=總數量-已購買數量
			int purchased = (product.getPurchasedQty() == null) ? 0 : product.getPurchasedQty();
			int remainingStock = product.getProductQty() - purchased;
			
			// 新的總數 = 當前已加數量 + 這次要加數量，但不能超過剩餘庫存
			int newQty = Math.min(currentQty + quantity, remainingStock);
			
			// 寫入 Redis
			redisTemplate.opsForHash().put(key, field, String.valueOf(newQty));
			
			// 購物車過期:30天未登入清空
			redisTemplate.expire(key, Duration.ofDays(30));
		}
		
		return "redirect:/cart/show";
	}
	
	// 顯示購物車
	@GetMapping("/show")
	public String showCart(ModelMap model, HttpSession session) {
		
		String key = getCartKey(session);
		
		// .entries()回傳型別Map<Object, Object>是固定寫法
		Map<Object, Object> redisCart = redisTemplate.opsForHash().entries(key);
		
		// Stream 資料轉換 (將 Redis 的字串轉為 CartVO 物件)
		// redisCart是Map不可直接stream()，需透過entrySet()
		List<CartVO> cartList = redisCart.entrySet().stream()
			.map(entry -> {
				Integer productId = Integer.parseInt((String) entry.getKey());
				Integer qty = Integer.parseInt((String) entry.getValue());
				ProdVO product = prodSvc.getOneProd(productId);
				return (product != null) ? new CartVO(product, qty) : null;  // 商品存在就return給前端，轉為CartVO
				})
				.filter(Objects::nonNull) // 過濾下架商品
				.collect(Collectors.toList()); // 打包成新的資料
		
		// 計算總金額
		int totalAmount = cartList.stream().mapToInt(CartVO::getSubtotal).sum();
		
		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		
		if (loginCust != null) {
		    model.addAttribute("userName", loginCust.getCustName());  // 登入，傳會員名字
	    } else {
	        model.addAttribute("userName", null); // 未登入，不傳，前端的 th:unless="${userName}" 會成立，右上角顯示「註冊 / 登入」
	    }
		
		// 補充:推薦商品區塊
		List<ProdVO> frontProdList = prodSvc.getRandomProducts(4);
		model.addAttribute("frontProdList", frontProdList);
		model.addAttribute("cartList", cartList);
		model.addAttribute("totalAmount", totalAmount);
		
		return "front-end/cart/cart_list";
	}
	
	// 修改數量
	@PostMapping("/update")
	public String updateCart(
		@RequestParam("productId") Integer productId, 
		@RequestParam("quantity") Integer quantity,
		HttpSession session) {
		
		// 防止被繞過前端的數量限制
		if (productId == null || quantity == null || quantity <= 0) {
		    return "redirect:/cart/show";
		}
		
		String key = getCartKey(session);
		String field = String.valueOf(productId);
		
		if (quantity <= 0) {
			redisTemplate.opsForHash().delete(key, field); // 數量變 0 直接移除
		} else {

		 	// 商品目前在購物車的已加數量
			ProdVO product = prodSvc.getOneProd(productId);
			int finalQty = quantity;
			
			if (product != null) {
				int purchased = (product.getPurchasedQty() == null) ? 0 : product.getPurchasedQty();
				int remainingStock = product.getProductQty() - purchased;
				
				if (quantity > remainingStock) {
					finalQty = Math.max(0, remainingStock); // 預防出現負數 & 限制在庫存最大值
				}
			}
			
			redisTemplate.opsForHash().put(key, field, String.valueOf(finalQty));
			
			redisTemplate.expire(key, Duration.ofDays(30));
		}

		return "redirect:/cart/show";
	}
	
	// 刪除
	@PostMapping("/delete")
	public String deleteFormCart(
		@RequestParam("productId") Integer productId,
		HttpSession session) {
		
		String key = getCartKey(session);
	    
		redisTemplate.opsForHash().delete(key, String.valueOf(productId));
		return "redirect:/cart/show";
	}
	
	// 清空購物車
	@PostMapping("/clear")
	public String clearCart(HttpSession session) {
		String key = getCartKey(session);     
		redisTemplate.delete(key);      // 刪除此會員的 Redis 
		return "redirect:/cart/show"; 
	}
	
}
