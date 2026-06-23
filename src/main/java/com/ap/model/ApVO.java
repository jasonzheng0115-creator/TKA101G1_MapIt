package com.ap.model;

import java.io.Serializable;
import java.time.LocalDate;

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
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "ACCOUNTS_PAYABLE")
public class ApVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AP_ID", updatable = false)
	private Integer apId;
	
	@NotNull(message = "應付金額: 請勿空白")
	@Min(value = 0, message = "金額不可小於0")
	@Column(name = "AMOUNT_PAYABLE")
	private Integer amountPayable;
	
	@NotNull(message = "請選擇付款狀態")
	@Column(name = "AP_STATUS")
	private Boolean apStatus = false;
	
	@NotEmpty(message = "帳單月份: 請勿空白")
	@Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "帳單月份格式不正確，請輸入西元年-月份 (例如: 2026-06)")
	@Column(name = "BILL_MONTH", length = 7)
	private String billMonth;
	
	@Column(name = "PAYMENT_DATE")
	private LocalDate paymentDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLIER_ID", referencedColumnName = "SUPPLIER_ID")
	private SplrVO splrVO;
	
	public ApVO () {
	}

	public Integer getApId() {
		return apId;
	}

	public void setApId(Integer apId) {
		this.apId = apId;
	}

	public Integer getAmountPayable() {
		return amountPayable;
	}

	public void setAmountPayable(Integer amountPayable) {
		this.amountPayable = amountPayable;
	}

	public Boolean getApStatus() {
		return apStatus;
	}

	public void setApStatus(Boolean apStatus) {
		this.apStatus = apStatus;
	}

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public SplrVO getSplrVO() {
		return splrVO;
	}

	public void setSplrVO(SplrVO splrVO) {
		this.splrVO = splrVO;
	}

	@Override
	public String toString() {
		return "ApVO [apId=" + apId + ", amountPayable=" + amountPayable + ", apStatus=" + apStatus 
				+ ", billMonth=" + billMonth + ", paymentDate=" + paymentDate + "]";
	}

}
