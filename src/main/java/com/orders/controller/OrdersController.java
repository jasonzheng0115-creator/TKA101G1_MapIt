package com.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrdersController {
	
	@Autowired
	private OrdersService ordersSvc;
	
	// 核心 API：前端送出購物車資料結帳
	// 網址：POST http://localhost:8080/orders/checkout
	// @RequestBody ➔ 強迫 Spring Boot 將前端傳過來的 JSON 字串，自動翻譯成 OrdersVO 物件
	// @Valid ➔ 啟動我們之前在 OrdersVO 身上貼的 @NotNull、@NotEmpty 表單驗證擋板
	// ResponseEntity 作為控制器的回傳格式，因為它能同時控制「HTTP 狀態碼（Status Code）」與「回應內容（Body）」
	@PostMapping("/checkout")
	public ResponseEntity<?> checkout(@Valid @RequestBody OrdersVO ordersVO) {
		
		try {
			
			// 1. 將前端打包好的訂單與明細，直接送進 Service 大腦執行（扣庫存、增銷量、級聯存檔）
			OrdersVO savedOrder = ordersSvc.insertOrder(ordersVO);
			// 2. 結帳成功：回傳 HTTP 200 狀態碼，並把存完檔、帶有最新 ORDER_ID 的訂單物件當成 JSON 吐回前端
			return ResponseEntity.ok(savedOrder);
			
		} catch (IllegalArgumentException | IllegalStateException e) {
			
			// 3. 商業邏輯失敗（例如：庫存不足、購物車空了）：回傳 HTTP 400 錯誤，並附上失敗原因
			return ResponseEntity.badRequest().body(e.getMessage());
			
		} catch (Exception e) {
			
			// 4. 系統崩潰（例如：資料庫斷線）：回傳 HTTP 500 伺服器錯誤
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

}
