package com.cust.model;

import java.sql.Date;

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
public class CustVO implements java.io.Serializable{
	//讓CustVO實作，有一個唯一的序號
	private static final long serialVersionUID = 1L;
	
	@Id //代表Entity的唯一值，對應到table的PK
	@Column(name = "CUST_ID") //對應到資料庫Table的哪一個欄位
	@GeneratedValue(strategy = GenerationType.IDENTITY) //和@Id搭配使用，讓資料庫在新增資料時自動生成唯一ID，總共有4種
	private Integer cust_id;
	
	@Column(name = "CUST_ACCOUNT", unique=true)
	@NotEmpty(message="會員帳號，請勿空白")
	@Pattern(regexp = "^[(a-zA-Z0-9_.)]{3,32}$",
	message = "帳號長度必須在 3 到 32 碼之間，請使用大小寫英文字母或_.")
	private String cust_account;
	
	@Column(name = "CUST_PASSWORD", unique=true)
	@NotEmpty(message="會員密碼，請勿空白")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])[A-Za-z0-9!@#$%^&*()_+=?><:;\\]\\[]{8,20}$"
	,message = "密碼長度必須在 8 到 20 碼之間，且必須包含大小寫英文字母，不允許空白")
	private String cust_password;
	
	@Column(name = "CUST_RIGHT")
	private String cust_right="正常";
	
	@Column(name = "CUST_USE")
	private String cust_use="啟動";
	
	@NotEmpty(message="會員姓名，請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)]{2,10}$",message = "請填寫正確姓名")
	@Column(name = "CUST_NAME")
	private String cust_name;
	
	@NotEmpty(message="會員性別，請勿空白")
	@Column(name = "CUST_SEX")
	private String cust_sex;
	
	@NotEmpty(message="會員手機號碼，請勿空白")
	@Pattern(regexp = "^[0][0-9][0-9]{8}$",message = "手機號碼格式錯誤")
	@Column(name = "CUST_TEL")
	private String cust_tel;
	
	@NotEmpty(message="電子郵件，請勿空白")
	@Column(name = "CUST_EMAIL")
	@Email(message = "電子郵件格式輸入錯誤")
	private String cust_email;
	
	@Column(name = "CUST_IMG")
	private String cust_img;
	
	@NotEmpty(message="國民身分證，請勿空白")
	@Column(name = "CUST_ID_CARD")
	@Pattern(regexp = "^[A-Z][1-2][0-9]{8}$", message = "國民身分證格式輸入錯誤")
	private String cust_id_card;
	
	@NotNull(message="會員生日，請勿空白")
	@Column(name = "CUST_BIRTHDAY")
	private Date cust_birthday;
	
	@Column(name = "CUST_CARD")
	private String cust_card;
	
	@Column(name = "CUST_ADDRESS")
	private String cust_address;
	
	//必須有一個不傳參數的建構子
	public CustVO() {}

	
	public Integer getCust_id() {
		return cust_id;
	}

	public void setCust_id(Integer cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_account() {
		return cust_account;
	}

	public void setCust_account(String cust_account) {
		this.cust_account = cust_account;
	}

	public String getCust_password() {
		return cust_password;
	}

	public void setCust_password(String cust_password) {
		this.cust_password = cust_password;
	}

	public String getCust_right() {
		return cust_right;
	}

	public void setCust_right(String cust_right) {
		this.cust_right = cust_right;
	}

	public String getCust_use() {
		return cust_use;
	}

	public void setCust_use(String cust_use) {
		this.cust_use = cust_use;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getCust_sex() {
		return cust_sex;
	}

	public void setCust_sex(String cust_sex) {
		this.cust_sex = cust_sex;
	}

	public String getCust_tel() {
		return cust_tel;
	}

	public void setCust_tel(String cust_tel) {
		this.cust_tel = cust_tel;
	}

	public String getCust_email() {
		return cust_email;
	}

	public void setCust_email(String cust_email) {
		this.cust_email = cust_email;
	}

	public String getCust_img() {
		return cust_img;
	}

	public void setCust_img(String cust_img) {
		this.cust_img = cust_img;
	}

	public String getCust_id_card() {
		return cust_id_card;
	}

	public void setCust_id_card(String cust_id_card) {
		this.cust_id_card = cust_id_card;
	}

	public Date getCust_birthday() {
		return cust_birthday;
	}

	public void setCust_birthday(Date cust_birthday) {
		this.cust_birthday = cust_birthday;
	}

	public String getCust_card() {
		return cust_card;
	}

	public void setCust_card(String cust_card) {
		this.cust_card = cust_card;
	}

	public String getCust_address() {
		return cust_address;
	}

	public void setCust_address(String cust_address) {
		this.cust_address = cust_address;
	}

	
	
}
