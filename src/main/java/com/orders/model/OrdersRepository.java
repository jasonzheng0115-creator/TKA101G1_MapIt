package com.orders.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersVO, Integer> {

	// 會員中心核心查詢：根據「會員編號 (custId)」撈出該會員的所有訂單，並按照成立時間由新到舊排序
	// JPA 的命名魔術：只要方法名字叫 findBy + 屬性名 + OrderBy + 排序屬性 + Desc
	// Spring Data JPA 就會在幕後自動幫你組裝出對應的 SQL 語法，完全不需要自己手寫 @Query
	List<OrdersVO> findByCustVO_CustIdOrderByOrderTimestampDesc(Integer custId);
}
