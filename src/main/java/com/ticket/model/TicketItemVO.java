package com.ticket.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.cust.model.CustVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_item")
@IdClass(TicketItemId.class) //複合組件
public class TicketItemVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@ManyToOne
	@JoinColumn(name="TKT_ID")
	private TicketVO ticketId;
	
	@Id
	@ManyToOne
	@JoinColumn(name="CUST_ID")
	private CustVO custId;
	
	@Column(name = "STARTDATE")
	private LocalDateTime startDate;
	
	@Column(name = "ENDDATE")
	private LocalDateTime endDate;
	
	@Column(name="TKT_STATUS")
	private String ticketStatus;

	public TicketVO getTicketId() {
		return ticketId;
	}

	public void setTicketId(TicketVO ticketId) {
		this.ticketId = ticketId;
	}

	public CustVO getCustId() {
		return custId;
	}

	public void setCustId(CustVO custId) {
		this.custId = custId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}
	

	


}
