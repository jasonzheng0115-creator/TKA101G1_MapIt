package com.cust.model;

import java.io.Serializable;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity //貼標籤給springBoot，這是JPA的一個Entity類別
@Table(name = "customer") //貼標籤給springBoot，這個class對應到資料庫的哪個table
public class CustVO implements Serializable{
	//讓CustVO實作，有一個唯一的序號
	private static final long serialVersionUID = 1L;
	
	@Id //代表Entity的唯一值，對應到table的PK
	@Column(name = "CUST_ID") //對應到資料庫Table的哪一個欄位
	@GeneratedValue(strategy = GenerationType.IDENTITY) //和@Id搭配使用，讓資料庫在新增資料時自動生成唯一ID，總共有4種
	private Integer custId;
	
	@Column(name = "CUST_ACCOUNT", unique=true)
	@NotEmpty(message="會員帳號，請勿空白")
	@Pattern(regexp = "^[(a-zA-Z0-9_.)]{3,32}$",
	message = "帳號長度必須在 3 到 32 碼之間，請使用大小寫英文字母或_.")
	private String custAccount;
	
	@Column(name = "CUST_PASSWORD", unique=true)
	@NotEmpty(message="會員密碼，請勿空白")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])[A-Za-z0-9!@#$%^&*()_+=?><:;\\]\\[]{8,20}$"
	,message = "密碼長度必須在 8 到 20 碼之間，且必須包含大小寫英文字母，不允許空白")
	private String custPassword;
	
	@Column(name = "CUST_RIGHT")
	private String custRight="正常";
	
	@Column(name = "CUST_USE")
	private String custUse="啟動";
	
	@NotEmpty(message="會員姓名，請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)]{2,10}$",message = "請填寫正確姓名")
	@Column(name = "CUST_NAME")
	private String custName;
	
	@NotEmpty(message="會員性別，請勿空白")
	@Column(name = "CUST_SEX")
	private String custSex;
	
	@NotEmpty(message="會員手機號碼，請勿空白")
	@Pattern(regexp = "^[0][0-9][0-9]{8}$",message = "手機號碼格式錯誤")
	@Column(name = "CUST_TEL")
	private String custTel;
	
	@NotEmpty(message="電子郵件，請勿空白")
	@Column(name = "CUST_EMAIL")
	@Email(message = "電子郵件格式輸入錯誤")
	private String custEmail;
	
	@Column(name = "CUST_IMG")
	private String custImg;
	
	@NotEmpty(message="國民身分證，請勿空白")
	@Column(name = "CUST_ID_CARD")
	@Pattern(regexp = "^[A-Z][1-2][0-9]{8}$", message = "國民身分證格式輸入錯誤")
	private String custIdCard;
	
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	@NotNull(message="會員生日，請勿空白")
	@Column(name = "CUST_BIRTHDAY")
	private Date custBirthday;
	
	@Column(name = "CUST_CARD")
	private String custCard;
	
	@Column(name = "CUST_ADDRESS")
	private String custAddress;
	
	//必須有一個不傳參數的建構子
	public CustVO() {}

	
	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getCustAccount() {
		return custAccount;
	}

	public void setCustAccount(String custAccount) {
		this.custAccount = custAccount;
	}

	public String getCustPassword() {
		return custPassword;
	}

	public void setCustPassword(String custPassword) {
		this.custPassword = custPassword;
	}

	public String getCustRight() {
		return custRight;
	}

	public void setCustRight(String custRight) {
		this.custRight = custRight;
	}

	public String getCustUse() {
		return custUse;
	}

	public void setCustUse(String custUse) {
		this.custUse = custUse;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustSex() {
		return custSex;
	}

	public void setCustSex(String custSex) {
		this.custSex = custSex;
	}

	public String getCustTel() {
		return custTel;
	}

	public void setCustTel(String custTel) {
		this.custTel = custTel;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustImg() {
		return custImg;
	}

	public void setCustImg(String custImg) {
		this.custImg = custImg;
	}

	public String getCustIdCard() {
		return custIdCard;
	}

	public void setCustIdCard(String custIdCard) {
		this.custIdCard = custIdCard;
	}

	public Date getCustBirthday() {
		return custBirthday;
	}

	public void setCustBirthday(Date custBirthday) {
		this.custBirthday = custBirthday;
	}

	public String getCustCard() {
		return custCard;
	}

	public void setCustCard(String custCard) {
		this.custCard = custCard;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}
}
