package com.ticket.model;

import java.io.Serializable;
import java.util.Objects;

public class TicketItemId implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer ticketId;
	private Integer custId;
	
	public TicketItemId() {}
	
	public TicketItemId(Integer ticketVO, Integer custVO) {
		this.ticketId = ticketId;
		this.custId = custId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(custId, ticketId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketItemId other = (TicketItemId) obj;
		return Objects.equals(custId, other.custId) && Objects.equals(ticketId, other.ticketId);
	}


	
	
}


