package com.splr.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "SUPPLIER")
public class SplrVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SUPPLIER_ID", updatable = false)
	private Integer supplierId;
	
	@NotEmpty(message = "廠商名稱: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_\\s\\(\\) （）)]{2,50}$", message="廠商名稱: 只能是中、英文字母、數字、括號和空格 , 且長度必需在2到50之間")

	@Column(name = "SUPPLIER_NAME")
	private String supplierName;
	
	@NotEmpty(message = "聯絡人: 請勿空白")
	@Column(name = "SUPPLIER_CONTACT")
	private String supplierContact;
	
	@NotEmpty(message = "廠商電話: 請勿空白")
	@Pattern(regexp = "^(0\\d{1,2}-\\d{7,8}|09\\d{8}|0\\d{8,9})$", message = "廠商電話: 格式不正確 (格式範例:02-12345678 或 0212345678 或 10碼手機號碼)")
	@Column(name = "SUPPLIER_TEL")
	private String supplierTel;
	
	@NotEmpty(message = "Email請勿空白")
	@Email(message = "信箱格式不正確")
	@Column(name = "SUPPLIER_EMAIL")
	private String supplierEmail;
	
	@NotEmpty(message = "地址請勿空白")
	@Column(name = "SUPPLIER_ADDRESS")
	private String supplierAddress;
	
	@Column(name = "SUPPLIER_STATUS", insertable = false)
	private Boolean supplierStatus = true; // 預設啟用中 (true)
	
	public SplrVO() {
	}
	
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierContact() {
		return supplierContact;
	}

	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	public String getSupplierTel() {
		return supplierTel;
	}

	public void setSupplierTel(String supplierTel) {
		this.supplierTel = supplierTel;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}
	
	public Boolean getSupplierStatus() {
		return supplierStatus;
	}

	public void setSupplierStatus(Boolean supplierStatus) {
		this.supplierStatus = supplierStatus;
	}

	@Override
	public String toString() {
		return "SplrVO [supplierId=" + supplierId + ", supplierName=" + supplierName + ", supplierContact="
				+ supplierContact + ", supplierTel=" + supplierTel + ", supplierEmail=" + supplierEmail
				+ ", supplierAddress=" + supplierAddress + ", supplierStatus=" + supplierStatus + "]";
	}
	
}
