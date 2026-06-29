package com.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {

    // 使用 BINARY 進行密碼大小寫的精準比對
    @Query(value = "select * from employee where EMP_ACC = ?1 and BINARY EMP_PWD = ?2", nativeQuery = true)
    EmpVO loginCheck(String account, String password);

    // 確認此帳號是否已被註冊過
    @Query(value = "select * from employee where EMP_ACC = ?1", nativeQuery = true)
    EmpVO findByAccount(String account);
}
