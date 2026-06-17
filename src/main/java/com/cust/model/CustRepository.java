package com.cust.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface CustRepository extends JpaRepository<CustVO,Integer>{


@Query //登入檢查(密碼大小寫必須完全一致，要在SQL語法裡加上：BINARY(二進位比對)。加上後MySQL就會大小寫比對)
(value = "select * from customer where CUST_ACCOUNT= ?1 and BINARY CUST_PASSWORD = ?2", nativeQuery = true)
CustVO loginCheck(String account, String password);

@Query
(value = "select CUST_ID from customer where CUST_ACCOUNT = ?1", nativeQuery = true)
CustVO findByAccount(Integer id);

}

