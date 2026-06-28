package com.ticket.model;

//票券統整功能，把VO拿來的資料，自訂一個專屬的json格式
public class TicketTotalDTO {
	private Integer productId;
	private String productName;
	private Integer unsoldCount;
	private Integer soldCount;
	private Integer cancelCount;
	
	//getter setter方法
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
	public Integer getUnsoldCount() {
		return unsoldCount;
	}
	public void setUnsoldCount(Integer unsoldCount) {
		this.unsoldCount = unsoldCount;
	}
	public Integer getSoldCount() {
		return soldCount;
	}
	public void setSoldCount(Integer soldCount) {
		this.soldCount = soldCount;
	}
	public Integer getCancelCount() {
		return cancelCount;
	}
	public void setCancelCount(Integer cancelCount) {
		this.cancelCount = cancelCount;
	}
	
	
	
}
