package com.prod.model;

import java.io.Serializable;

import com.splr.model.SplrVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "PRODUCT")
public class ProdVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRODUCT_ID", updatable = false)
	private Integer productId;
	
	@NotEmpty(message = "商品名稱: 請勿空白")
	@Size(max = 20, message = "商品名稱不可超過20字")
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	@NotEmpty(message = "商品描述: 請勿空白")
	@Size(max = 50, message = "商品描述不可超過50字")
	@Column(name = "PRODUCT_DESC")
	private String productDesc;
	
	@NotNull(message = "商品數量: 請勿空白")  // 注意：數字不能用 @NotEmpty
	@Min(value = 0, message = "商品數量不可小於0")
	@Column(name = "PRODUCT_QTY")
	private Integer productQty;
	
	@Column(name = "PURCHASED_QTY")
	private Integer purchasedQty = 0; // 預設已購買為 0
	
	@NotNull(message = "商品金額: 請勿空白")
	@Min(value = 0, message = "商品金額不可小於0")
	@Column(name = "PRODUCT_PRICE")
	private Integer productPrice;  
	
	@NotNull(message = "請選擇商品狀態")
	@Column(name = "PRODUCT_STATUS")
	private Boolean productStatus = true; // 預設上架
	
	@NotNull(message = "商品使用期限: 請勿空白")
	@Min(value = 0, message = "商品使用期限不可小於0(單位:月數)")
	@Column(name = "USE_PERIOD")
	private Integer usePeriod;
	
	@Column(name = "PRODUCT_IMG")
	private String productImg;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLIER_ID", referencedColumnName = "SUPPLIER_ID")
	private SplrVO splrVO;
	
	public ProdVO() {
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

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public Integer getProductQty() {
		return productQty;
	}

	public void setProductQty(Integer productQty) {
		this.productQty = productQty;
	}

	public Integer getPurchasedQty() {
		return purchasedQty;
	}

	public void setPurchasedQty(Integer purchasedQty) {
		this.purchasedQty = purchasedQty;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Boolean getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Boolean productStatus) {
		this.productStatus = productStatus;
	}

	public Integer getUsePeriod() {
		return usePeriod;
	}

	public void setUsePeriod(Integer usePeriod) {
		this.usePeriod = usePeriod;
	}
	
	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	public SplrVO getSplrVO() {
		return splrVO;
	}

	public void setSplrVO(SplrVO splrVO) {
		this.splrVO = splrVO;
	}

	@Override
	public String toString() {
		return "ProdVO [productId=" + productId + ", productName=" + productName + ", productDesc=" + productDesc
				+ ", productQty=" + productQty + ", purchasedQty=" + purchasedQty + ", productPrice=" + productPrice
				+ ", productStatus=" + productStatus + ", usePeriod=" + usePeriod + ", productImg=" + productImg + "]";
	}

}
