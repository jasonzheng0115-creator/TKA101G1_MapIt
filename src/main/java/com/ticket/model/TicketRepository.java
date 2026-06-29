package com.ticket.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<TicketVO, Integer> {

	
	@Query // 查找單一商品的所有票券庫存
	(value = "select * from ticket where PRODUCT_ID = ?1", nativeQuery = true)
	//因為會回傳多個資料，所以用list
	List<TicketVO> findByProductId(Integer prodId);
	
	@Query //查找單一商品所有票券的銷售狀態
	(value = "select * from ticket where PRODUCT_ID = ?1 AND TKT_SALE = ?2" , nativeQuery = true)
	List<TicketVO> findByProdIdAndTktSale(Integer prodId, Integer tkt_sale);

	@Query //查找單一商品未售出票券的指定數量
	(value = "select * from ticket where PRODUCT_ID = ?1 AND TKT_SALE = 0 LIMIT ?2" , nativeQuery = true)
	List<TicketVO> findUnsoldTickets(Integer ProdId, Integer limit);
}
