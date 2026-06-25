package com.prod.controller;

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

import com.prod.model.CartVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private ProdService prodSvc; // 用來查 MySQL 商品資訊
	
	@Autowired
	private StringRedisTemplate redisTemplate; // Spring Boot 內建操作 Redis 的工具
	
	// 輔助方法：定義 Redis 的專屬號碼箱（Key）
	private String getCartKey() {
		return "cart:member:1"; // 驗收 Demo 先寫死 1 號會員
	}
	
	// 加入購物車 (數量自動累加)
	@PostMapping("/add")
	public String addToCart(
			@RequestParam("productId") Integer productId, 
			@RequestParam("quantity") Integer quantity) {
		
		String key = getCartKey();                // 指定一個會員的購物車
		String field = String.valueOf(productId); // 此會員購物車裡的某一個商品
		
		// 官方標準寫法：去這個 key 的 Hash 結構裡，把這個商品欄位數量累加
		// 舉例:("cart:member:1", "101", quantity) -> 括號內參數解釋為:(1號會員的購物車,商品編號101,數量增加)
		redisTemplate.opsForHash().increment(key, field, quantity);
		
		return "redirect:/cart/show";
	}
	
	// 顯示購物車 ( Redis + Function 資料轉換 Stream)
	@GetMapping("/show")
	public String showCart(ModelMap model) {
		String key = getCartKey();
		
		// 官方標準寫法：直接把這個 key 的所有 {商品ID: 數量} 倒出來
		Map<Object, Object> redisCart = redisTemplate.opsForHash().entries(key);
		
		// Stream 資料轉換 (將 Redis 的字串轉為 CartVO 物件)
		// redisCart是Map不可直接stream()，需透過entrySet()
		List<CartVO> cartList = redisCart.entrySet().stream()
				.map(entry -> {
					Integer productId = Integer.parseInt((String) entry.getKey());
					Integer qty = Integer.parseInt((String) entry.getValue());
					
					// 去 MySQL 撈出最新的商品詳情
					ProdVO product = prodSvc.getOneProd(productId);
					
					// 【Function 轉換】：如果商品存在，轉化為前端畫面的 CartVO 物件
					return (product != null) ? new CartVO(product, qty) : null;
				})
				.filter(Objects::nonNull) // 過濾失效或被下架的商品
				.collect(Collectors.toList());
		
		// 計算總金額
		int totalAmount = cartList.stream()
				.mapToInt(CartVO::getSubtotal)
				.sum();
		
		// 把所有旅遊商品從資料庫撈出來，裝進 frontProdList 
		// 讓推薦商品區塊顯示
		List<ProdVO> frontProdList = prodSvc.getAll();
		model.addAttribute("frontProdList", frontProdList);
		
		model.addAttribute("cartList", cartList);
		model.addAttribute("totalAmount", totalAmount);
		return "front-end/cart/cart_list";
	}
	
	// 修改數量
	@PostMapping("/update")
	public String updateCart(
			@RequestParam("productId") Integer productId, 
			@RequestParam("quantity") Integer quantity) {
		
		String key = getCartKey();
		String field = String.valueOf(productId);
		
		if (quantity <= 0) {
			redisTemplate.opsForHash().delete(key, field); // 數量變 0 直接移除
		} else {
			// 直接用新數量覆蓋
			redisTemplate.opsForHash().put(key, field, String.valueOf(quantity));
		}
		return "redirect:/cart/show";
	}
	
	// 手動刪除
	@PostMapping("/delete")
	public String deleteFormCart(@RequestParam("productId") Integer productId) {
		String key = getCartKey();
		redisTemplate.opsForHash().delete(key, String.valueOf(productId));
		return "redirect:/cart/show";
	}
	
	// 一鍵清空購物車
	@PostMapping("/clear")
	public String clearCart() {
		String key = getCartKey();      // 取得cart:member:1
		redisTemplate.delete(key);      // 核心:直接把這個會員的 Redis 號碼箱整箱刪除
		return "redirect:/cart/show";   // 刪完後重整購物車畫面
	}
	
	
	
}
