package com.ticket.model;

//票券詳細資訊功能，把VO拿來的資料，自訂一個專屬的json格式
public class TicketDTO {
	private Integer tktId;
	private Integer productId;
	private String productName;
	private String saleStatus;
	
	//getter setter方法
	public Integer getTktId() {
		return tktId;
	}
	public void setTktId(Integer tktId) {
		this.tktId = tktId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getSaleStatus() {
		return saleStatus;
	}
	public void setSaleStatus(String saleStatus) {
		this.saleStatus = saleStatus;
	}
	
	

}
