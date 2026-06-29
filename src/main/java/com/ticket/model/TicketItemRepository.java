package com.ticket.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketItemRepository extends JpaRepository<TicketItemVO, TicketItemId> {

}
