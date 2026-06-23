package com.orders.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {
	private static final long serialVersionUID = 1L;
	// 複合主鍵必須實作 Serializable 介面
	
	private Integer orderId;
	private Integer productId;
	
	public OrderItemId() {
	}
	
	// 未來 new 鑰匙時直接帶入數字的建構子
	public OrderItemId(Integer orderId, Integer productId) {
		this.orderId = orderId;
		this.productId = productId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}


	// override equals，告訴系統只要「訂單編號」和「商品編號」數字相同，這兩把鑰匙就是相等的
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true; // 如果在記憶體裡根本是同一個物件，直接回傳 true
		
		// 確認不是空值，且確認對方的類別跟自己完全一模一樣
		if (obj != null && getClass() == obj.getClass()) {
			
			// 再次檢查對方是不是 OrderItemId 種類（其實上面那行過關，這裡必過）
			if (obj instanceof OrderItemId) {
				// 強制將傳進來的通用 Object 轉換成鑰匙型態
				OrderItemId id = (OrderItemId) obj;
				// 資料型態Integer是物件,非int,所以用.equals
				if (orderId.equals(id.orderId) && productId.equals(id.productId)) {
					return true; // 欄位數字都相同，判定相等
				}
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(orderId, productId);
	}
	
}
