package com.attr.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * AttrRepository - 景點資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 * 繼承 JpaSpecificationExecutor 提供動態查詢功能
 */
public interface AttrRepository extends JpaRepository<AttrVO, Integer>, JpaSpecificationExecutor<AttrVO> {
    
    /**
     * 根據地區 ID 查詢景點
     * JPA 會自動根據方法名稱生成查詢語句
     * @param regionId 地區 ID
     * @return 該地區的所有景點
     */
    List<AttrVO> findByRegionVO_RegionId(Integer regionId);
    
    /**
     * 根據地區 ID 查詢景點（支援分頁）
     * @param regionId 地區 ID
     * @param pageable 分頁參數
     * @return 該地區的景點分頁資料
     */
    Page<AttrVO> findByRegionVO_RegionId(Integer regionId, Pageable pageable);
    
    /**
     * 根據類別 ID 查詢景點
     * JPA 會自動根據方法名稱生成查詢語句
     * @param categoryId 類別 ID
     * @return 該類別的所有景點
     */
    List<AttrVO> findByCategoryVO_CategoryId(Integer categoryId);
    
    /**
     * 根據類別 ID 查詢景點（支援分頁）
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 該類別的景點分頁資料
     */
    Page<AttrVO> findByCategoryVO_CategoryId(Integer categoryId, Pageable pageable);
    
    /**
     * 根據地區 ID 和類別 ID 同時查詢景點（支援分頁）
     * @param regionId 地區 ID
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    Page<AttrVO> findByRegionVO_RegionIdAndCategoryVO_CategoryId(Integer regionId, Integer categoryId, Pageable pageable);
    
    /**
     * 根據景點名稱查詢景點（用於防重複匯入）
     * @param attrName 景點名稱
     * @return 符合名稱的景點（如果存在）
     */
    Optional<AttrVO> findByAttrName(String attrName);
    
    // ========== 關鍵字搜尋方法（支援分頁） ==========
    
    /**
     * 根據景點名稱關鍵字搜尋（支援分頁）
     * @param keyword 關鍵字
     * @param pageable 分頁參數
     * @return 符合關鍵字的景點分頁資料
     */
    Page<AttrVO> findByAttrNameContaining(String keyword, Pageable pageable);
    
    /**
     * 根據關鍵字 + 地區 ID 搜尋（支援分頁）
     * @param keyword 關鍵字
     * @param regionId 地區 ID
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    Page<AttrVO> findByAttrNameContainingAndRegionVO_RegionId(String keyword, Integer regionId, Pageable pageable);
    
    /**
     * 根據關鍵字 + 類別 ID 搜尋（支援分頁）
     * @param keyword 關鍵字
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    Page<AttrVO> findByAttrNameContainingAndCategoryVO_CategoryId(String keyword, Integer categoryId, Pageable pageable);
    
    /**
     * 根據關鍵字 + 地區 ID + 類別 ID 搜尋（支援分頁）
     * 使用 @Query 自定義 JPQL 查詢以簡化方法名稱
     * @param keyword 關鍵字
     * @param regionId 地區 ID
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    @Query("SELECT a FROM AttrVO a WHERE a.attrName LIKE %:keyword% " +
           "AND a.regionVO.regionId = :regionId " +
           "AND a.categoryVO.categoryId = :categoryId")
    Page<AttrVO> findByKeywordAndRegionAndCategory(
        @Param("keyword") String keyword,
        @Param("regionId") Integer regionId,
        @Param("categoryId") Integer categoryId,
        Pageable pageable
    );
}
