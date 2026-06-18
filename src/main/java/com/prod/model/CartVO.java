package com.prod.model;

import java.io.Serializable;

public class CartVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ProdVO product;    // 商品物件（內含品名、價格、圖片網址等）
	private Integer quantity;  // 購買數量
	private Integer subtotal;  // 該品項的小計 (單價 * 數量)
	
	public CartVO() {
	}
	
	// 建構子：建立物件時，自動幫忙把小計（單價 * 數量）算好
	public CartVO(ProdVO product, Integer quantity) {
		this.product = product;
		this.quantity = quantity;
		this.subtotal = product.getProductPrice() * quantity;
	}
	
	public ProdVO getProduct() {
		return product;
	}
	public void setProduct(ProdVO product) {
		this.product = product;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
		// 當數量被修改時，小計也要跟著重新計算
		this.subtotal = this.product.getProductPrice() * quantity;
	}
	public Integer getSubtotal() {
		return subtotal;
	}
	
}
