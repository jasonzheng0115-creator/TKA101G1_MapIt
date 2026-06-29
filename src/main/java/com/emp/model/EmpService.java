package com.emp.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

@Service
public class EmpService {

    @Autowired
    private EmpRepository repository;

    @Autowired
    private EntityManager entityManager;

    // 員工登入
    public EmpVO login(String account, String password) {
        // 呼叫 Repository 的 loginCheck 方法比對帳號與密碼 (大小寫完全一致)
        return repository.loginCheck(account, password);
    }

    // 新增員工 (含帳號防呆)
    public void addEmp(EmpVO empVO) {
        // 1. 先用帳號去資料庫查詢，看是否有人用過了
        EmpVO existEmp = repository.findByAccount(empVO.getEmpAcc());

        // 2. 如果查出來不是 null，代表這個帳號已經被搶先註冊了，拋出例外！
        if (existEmp != null) {
            throw new RuntimeException("該帳號已存在，請更換帳號");
        }

        // 3. 防呆通過，儲存進資料庫
        repository.save(empVO);
    }

    // 修改員工資料
    public void updateEmp(EmpVO empVO) {
        // 呼叫 save 時，若 empVO 內部帶有 empId (PK)，JPA 會自動執行 UPDATE 語法
        repository.save(empVO);
    }

    // 查詢單一員工資料
    public EmpVO getOneEmp(Integer empId) {
        Optional<EmpVO> optional = repository.findById(empId);
        return optional.orElse(null);
    }

    // 查詢所有員工列表
    public List<EmpVO> listAllEmp() {
        return repository.findAll();
    }

    // 複合條件查詢 (依條件篩選員工)
    public List<EmpVO> listAllEmp(Map<String, String[]> map) {
        // 呼叫我們剛剛在步驟 3-A 寫好的複合查詢工具，把前端 Map 與實體管理器傳過去
        return EmpCompositeQuery.getAllC(map, entityManager);
    }
}
