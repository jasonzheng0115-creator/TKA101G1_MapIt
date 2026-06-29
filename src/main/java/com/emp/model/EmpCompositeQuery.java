package com.emp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class EmpCompositeQuery {

    // 根據前端傳入的欄位名稱與值，動態生成 JPA 條件 (Predicate)
    public static Predicate getPredicate(CriteriaBuilder builder, Root<EmpVO> root, String columnName, String value) {
        Predicate predicate = null;

        if ("empName".equals(columnName) || "empTel".equals(columnName)) {
            // 姓名與電話：使用模糊比對 (like)，例如：like %0912%
            predicate = builder.like(root.get(columnName), "%" + value + "%");
        } else if ("empId".equals(columnName)) {
            // 員工編號：精準比對 (equal)，型態需轉為 Integer
            predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
        } else if ("deptId".equals(columnName)) {
            // 部門編號：這是外鍵關聯，必須透過關聯物件 deptVO 取得裡面的 deptId
            predicate = builder.equal(root.get("deptVO").get("deptId"), Integer.valueOf(value));
        }

        return predicate;
    }

    // 主要執行複合查詢的方法，讀取 Map 中的條件並進行查詢
    @SuppressWarnings("unchecked")
    public static List<EmpVO> getAllC(Map<String, String[]> map, EntityManager em) {
        List<EmpVO> list = null;

        // 1. 建立 Criteria 查詢的工具箱 (builder) 與查詢骨架 (criteriaQuery)
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EmpVO> criteriaQuery = builder.createQuery(EmpVO.class);

        // 2. 指定我們要查詢的主表為 EMPLOYEE 表 (對應的 Entity 為 EmpVO)
        Root<EmpVO> root = criteriaQuery.from(EmpVO.class);

        // 3. 用來裝載動態拼接完成的 Predicate (條件) 清單
        List<Predicate> predicateList = new ArrayList<>();

        // 4. 遍歷前端送來的所有查詢參數 (Key 為欄位名，Value 為值陣列)
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String value = map.get(key)[0]; // 拿取第一個參數值

            // 防呆：值不可為空白，且必須排除非資料庫欄位的動作標記 (如 action)
            if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
                predicateList.add(getPredicate(builder, root, key, value.trim()));
            }
        }

        // 5. 將所有 Predicate 條件用 AND 連接，放進 Criteria 查詢的 WHERE 區塊中
        criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));

        // 6. 設定查詢結果依「員工編號 (empId)」由小到大 (ASC) 排序
        criteriaQuery.orderBy(builder.asc(root.get("empId")));

        // 7. 透過 EntityManager 執行查詢並回傳名單
        Query query = em.createQuery(criteriaQuery);
        list = query.getResultList();

        return list;
    }
}
