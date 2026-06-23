package com.orders.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERS")
@IdClass(OrderItemId.class) // 告訴JPA這張表格的有兩個pk,規格寫在OrderItemId.class裡
public class OrderItemVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// 這兩個主鍵屬性的名字與型態，必須跟 OrderItemId 裡寫的變數一樣
	@Id
	@Column(name = "ORDER_ID")
	private Integer orderId;
	
	@Id
	@Column(name = "PRODUCT_ID")
	private Integer productId;
	
	@Column(name = "ITEM_QTY")
	private Integer itemQty;
	
	@Column(name = "ITEM_PRICE")
	private Integer itemPrice;
	
	public OrderItemVO() {
	}
}
