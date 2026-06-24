package com.orders.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cust.model.CustVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ORDERS")
public class OrdersVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ORDER_ID", updatable = false)
	private Integer orderId;
	
	@Column(name = "ORDER_TIMESTAMP", insertable = false, updatable = false)
	private LocalDateTime orderTimestamp;
	
	@NotNull(message = "訂單金額: 請勿空白")
	@Column(name = "ORDER_PRICE")
	private Integer orderPrice;
	
	@NotEmpty(message = "訂單狀態: 請勿空白")
	@Column(name = "ORDER_STATUS")
	private String orderStatus;
	
	@Column(name = "ORDER_CANCEL")
	private String orderCancel;
	
	@Column(name = "CANCEL_DATE")
	private LocalDateTime cancelDate;
	
	@NotEmpty(message = "付款方式: 請勿空白")
	@Column(name = "PAYMENT_METHOD")
	private String paymentMethod;
	
	@NotNull(message = "付款狀態: 請勿空白")
	@Column(name = "PAYMENT_STATUS")
	private Boolean paymentStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUST_ID", referencedColumnName = "CUST_ID")
	private CustVO custVO;
	

	// 一對多訂單明細（雙向關聯與生命週期連動）
	// mappedBy = "ordersVO" ➔ 指向明細端（OrderItemVO）中建立關聯的屬性欄位名稱
	// cascade = CascadeType.ALL ➔ 設定級聯操作：當主檔進行新增、修改、刪除時，其所屬的明細將同步觸發對應的操作
	// orphanRemoval = true ➔ 孤兒移除機制：未來若從 Java 的 orderItems 清單中移除某筆明細物件，JPA 將自動於資料庫中刪除該筆對應的明細資料
	@OneToMany(mappedBy = "ordersVO", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderItemVO> orderItems = new ArrayList<>();
	
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getOrderTimestamp() {
		return orderTimestamp;
	}

	public void setOrderTimestamp(LocalDateTime orderTimestamp) {
		this.orderTimestamp = orderTimestamp;
	}

	public Integer getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderCancel() {
		return orderCancel;
	}

	public void setOrderCancel(String orderCancel) {
		this.orderCancel = orderCancel;
	}

	public LocalDateTime getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(LocalDateTime cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Boolean getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public CustVO getCustVO() {
		return custVO;
	}

	public void setCustVO(CustVO custVO) {
		this.custVO = custVO;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public List<OrderItemVO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}

	@Override
	public String toString() {
		return "OrdersVO [orderId=" + orderId + ", orderTimestamp=" + orderTimestamp + ", orderPrice=" + orderPrice
				+ ", orderStatus=" + orderStatus + ", orderCancel=" + orderCancel + ", cancelDate=" + cancelDate
				+ ", paymentMethod=" + paymentMethod + ", paymentStatus=" + paymentStatus + "]";
	}

}
