package com.ticket.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prod.model.ProdVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket")
public class TicketVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "TKT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY) //資料庫自動給值
	private Integer tktId;
	
	//因為有多筆資料，不想要一開始就全部載入降低效能，所以用lazy先放個假資料，真的有被呼叫再去資料庫拿資料
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="PRODUCT_ID")
	//因為用了lazy又在controller要json格式輸出給前端的@ResponseBody
	//所以用⬇︎忽略lazy的假資料，讓controller不要轉json(hibernateLazyInitializer、handler)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","apirVO"})
	private ProdVO productVO;
	
	@Column(name = "TKT_SALE")
	private Integer tktSale;
	
	
	
	public TicketVO() {} //給一個無參數建構子


	public Integer getTktId() {
		return tktId;
	}


	public void setTktId(Integer tktId) {
		this.tktId = tktId;
	}


	public ProdVO getProductVO() {
		return productVO;
	}


	public void setProductVO(ProdVO productVO) {
		this.productVO = productVO;
	}


	public Integer getTktSale() {
		return tktSale;
	}


	public void setTktSale(Integer tktSale) {
		this.tktSale = tktSale;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


		
}