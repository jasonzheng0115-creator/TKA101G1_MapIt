package com.orders.model;

import java.io.Serializable;

import com.prod.model.ProdVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ITEM")
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
	
	// 雙向關聯設定（多對一）
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID", insertable = false, updatable = false)
	// 為什麼要設 insertable/updatable = false？
	// 因為我們在上面已經定義過一個普通的「private Integer orderId;」用來存數字了。
	// JPA 規定：同一個欄位（ORDER_ID）不能有兩個人同時具備寫入權限，否則資料庫會打架。
	// 所以我們把這條「物件水管」設為唯讀，只用來在畫面上「撈資料、顯示訂單內容」。
	private OrdersVO ordersVO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
	private ProdVO prodVO;
	
	public OrderItemVO() {
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

	public Integer getItemQty() {
		return itemQty;
	}

	public void setItemQty(Integer itemQty) {
		this.itemQty = itemQty;
	}

	public Integer getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Integer itemPrice) {
		this.itemPrice = itemPrice;
	}

	public OrdersVO getOrdersVO() {
		return ordersVO;
	}
	
	// 這裡雖然唯讀，但還是提供 Setter 給 Java 內部互串物件使用
	public void setOrdersVO(OrdersVO ordersVO) {
		this.ordersVO = ordersVO;
	}

	public ProdVO getProdVO() {
		return prodVO;
	}

	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}
	
}
