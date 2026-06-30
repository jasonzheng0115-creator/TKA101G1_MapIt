package com.attr.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * AttrSpecification - 景點動態查詢規格類別
 * 
 * 職責：
 * 1. 提供動態組合查詢條件的功能
 * 2. 支援關鍵字、地區、類別的複合搜尋
 * 3. 使用 JPA Criteria API 建立查詢條件
 * 
 * 使用方式：
 * <pre>
 * Specification<AttrVO> spec = AttrSpecification.buildSpecification("台北", 1, 2);
 * Page<AttrVO> result = attrRepository.findAll(spec, pageable);
 * </pre>
 */
public class AttrSpecification {
    
    /**
     * 建立動態查詢規格（支援多地區 ID）
     * 
     * @param keyword 景點名稱關鍵字（可為 null 或空字串）
     * @param regionIds 地區 ID 列表（可為 null 或空列表）
     * @param categoryId 類別 ID（可為 null）
     * @return Specification 查詢規格
     */
    public static Specification<AttrVO> buildSpecification(String keyword, List<Integer> regionIds, Integer categoryId) {
        return new Specification<AttrVO>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<AttrVO> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // 建立條件列表
                List<Predicate> predicates = new ArrayList<>();
                
                // 1. 關鍵字條件（同時模糊搜尋：景點名稱、景點地址、地區名稱或類別名稱）
                if (keyword != null && !keyword.trim().isEmpty()) {
                    // 🟢 方案 B：統一將「臺」替換為資料庫使用的「台」，解決繁簡/俗寫不一致問題
                    String cleanKeyword = keyword.trim().replace("臺", "台");
                    String pattern = "%" + cleanKeyword + "%";
                    
                    Predicate namePredicate = criteriaBuilder.like(root.get("attrName"), pattern);
                    Predicate addressPredicate = criteriaBuilder.like(root.get("attrAddress"), pattern);
                    Predicate regionNamePredicate = criteriaBuilder.like(root.get("regionVO").get("regionName"), pattern);
                    Predicate categoryNamePredicate = criteriaBuilder.like(root.get("categoryVO").get("categoryName"), pattern);
                    
                    Predicate keywordPredicate = criteriaBuilder.or(
                        namePredicate, 
                        addressPredicate, 
                        regionNamePredicate, 
                        categoryNamePredicate
                    );
                    predicates.add(keywordPredicate);
                }
                
                // 2. 地區條件（支援 IN 查詢多地區）
                if (regionIds != null && !regionIds.isEmpty()) {
                    Predicate regionPredicate = root.get("regionVO").get("regionId").in(regionIds);
                    predicates.add(regionPredicate);
                }
                
                // 3. 類別條件
                if (categoryId != null) {
                    Predicate categoryPredicate = criteriaBuilder.equal(
                        root.get("categoryVO").get("categoryId"), 
                        categoryId
                    );
                    predicates.add(categoryPredicate);
                }
                
                // 4. 組合所有條件（使用 AND 邏輯）
                if (predicates.isEmpty()) {
                    // 若無任何條件，返回 null（查詢全部）
                    return criteriaBuilder.conjunction();
                } else {
                    // 將所有條件用 AND 連接
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            }
        };
    }

    /**
     * 建立動態查詢規格（單一地區 ID，相容舊版）
     */
    public static Specification<AttrVO> buildSpecification(String keyword, Integer regionId, Integer categoryId) {
        List<Integer> regionIds = null;
        if (regionId != null) {
            regionIds = new ArrayList<>();
            regionIds.add(regionId);
        }
        return buildSpecification(keyword, regionIds, categoryId);
    }
    
    /**
     * 建立只有關鍵字的查詢規格
     * 
     * @param keyword 景點名稱關鍵字
     * @return Specification 查詢規格
     */
    public static Specification<AttrVO> byKeyword(String keyword) {
        return buildSpecification(keyword, (List<Integer>) null, null);
    }
    
    /**
     * 建立只有地區的查詢規格
     * 
     * @param regionId 地區 ID
     * @return Specification 查詢規格
     */
    public static Specification<AttrVO> byRegion(Integer regionId) {
        return buildSpecification(null, regionId, null);
    }
    
    /**
     * 建立只有類別的查詢規格
     * 
     * @param categoryId 類別 ID
     * @return Specification 查詢規格
     */
    public static Specification<AttrVO> byCategory(Integer categoryId) {
        return buildSpecification(null, (List<Integer>) null, categoryId);
    }
}
