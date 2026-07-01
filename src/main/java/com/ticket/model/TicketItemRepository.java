package com.ticket.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketItemRepository extends JpaRepository<TicketItemVO, TicketItemId> {

	@Query("SELECT ticket FROM TicketItemVO ticket WHERE ticket.custId.custId = ?1")
	List<TicketItemVO> findTicketsByCustId(Integer custId);

	// 根據tktId找出明細
	@Query("SELECT ticket FROM TicketItemVO ticket WHERE ticket.ticketId.tktId = ?1")
	TicketItemVO findByTktId(Integer tktId);

}
