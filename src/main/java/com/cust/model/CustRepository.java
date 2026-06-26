package com.cust.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CustRepository extends JpaRepository<CustVO, Integer> {

    @Query // 登入檢查(密碼大小寫必須完全一致，要在SQL語法裡加上：BINARY(二進位比對)。加上後MySQL就會大小寫比對)
    (value = "select * from customer where CUST_ACCOUNT= ?1 and BINARY CUST_PASSWORD = ?2", nativeQuery = true)
    CustVO loginCheck(String account, String password);

    @Query // 會員註冊檢查帳號是否唯一+會員資料更新用
    (value = "select * from customer where CUST_ACCOUNT = ?1", nativeQuery = true)
    CustVO findByAccount(String account);

    @Query //
    (value = "select * from customer where CUST_ID = ?1", nativeQuery = true)
    CustVO findByCustId(Integer id);

    List<CustVO> findByCustAccountContaining(String keyword);

}
