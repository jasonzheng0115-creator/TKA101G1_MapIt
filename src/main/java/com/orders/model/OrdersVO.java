package com.orders.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.cust.model.CustVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@NotNull(message = "訂單成立時間: 請勿空白")
	@Column(name = "ORDER_TIMESTAMP")
	private LocalDate orderTimestamp;
	
	@NotNull(message = "訂單金額: 請勿空白")
	@Column(name = "ORDER_PRICE")
	private Integer orderPrice;
	
	@NotEmpty(message = "訂單狀態: 請勿空白")
	@Column(name = "ORDER_STATUS")
	private String orderStatus;
	
	@Column(name = "ORDER_CANCEL")
	private String orderCancel;
	
	@Column(name = "CANCEL_DATE")
	private LocalDate cancelDate;
	
	@NotEmpty(message = "付款方式: 請勿空白")
	@Column(name = "PAYMENT_METHOD")
	private String paymentMethod;
	
	@NotNull(message = "付款狀態: 請勿空白")
	@Column(name = "PAYMENT_STATUS")
	private Boolean paymentStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUST_ID", referencedColumnName = "CUST_ID")
	private CustVO custVO;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public LocalDate getOrderTimestamp() {
		return orderTimestamp;
	}

	public void setOrderTimestamp(LocalDate orderTimestamp) {
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

	public LocalDate getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(LocalDate cancelDate) {
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

	@Override
	public String toString() {
		return "OrdersVO [orderId=" + orderId + ", orderTimestamp=" + orderTimestamp + ", orderPrice=" + orderPrice
				+ ", orderStatus=" + orderStatus + ", orderCancel=" + orderCancel + ", cancelDate=" + cancelDate
				+ ", paymentMethod=" + paymentMethod + ", paymentStatus=" + paymentStatus + "]";
	}
	
	
}
