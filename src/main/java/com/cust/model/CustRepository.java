package com.cust.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface CustRepository extends JpaRepository<CustVO,Integer>{


@Query //查詢會員帳號密碼
(value = "select * from customer where  CUST_ACCOUNT= ?1 and CUST_PASSWORD = ?2", nativeQuery = true)
CustVO loginCheck(String account, String password);

@Query
(value = "select CUST_ID from customer where CUST_ACCOUNT = ?1", nativeQuery = true)
CustVO findByAccount(Integer id);
}