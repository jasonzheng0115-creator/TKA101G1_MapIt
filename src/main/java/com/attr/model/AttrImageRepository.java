package com.attr.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * AttrImageRepository - 景點圖片資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 * 
 * 更新說明 (2026/06/22)：
 * - 從 com.image.model.ImageRepository 重構為 com.attr.model.AttrImageRepository
 * - 符合 CLAUDE.md 專案規範：使用 com.attr 模組
 */
public interface AttrImageRepository extends JpaRepository<AttrImageVO, Integer> {
    
    /**
     * 根據景點 ID 查詢所有圖片
     * @param attrId 景點 ID
     * @return 圖片清單
     */
    @Query("FROM AttrImageVO WHERE attrVO.attrId = :attrId ORDER BY isMain DESC, uploadTime ASC")
    List<AttrImageVO> findByAttrId(@Param("attrId") Integer attrId);
    
    /**
     * 根據景點 ID 查詢主圖
     * @param attrId 景點 ID
     * @return 主圖 (可能為 null)
     */
    @Query("FROM AttrImageVO WHERE attrVO.attrId = :attrId AND isMain = true")
    AttrImageVO findMainImageByAttrId(@Param("attrId") Integer attrId);
    
    /**
     * 將指定景點的所有圖片設為非主圖
     * @param attrId 景點 ID
     */
    @Modifying
    @Query("UPDATE AttrImageVO SET isMain = false WHERE attrVO.attrId = :attrId")
    void unsetAllMainImages(@Param("attrId") Integer attrId);
    
    /**
     * 刪除指定景點的所有圖片
     * @param attrId 景點 ID
     */
    @Modifying
    @Query("DELETE FROM AttrImageVO WHERE attrVO.attrId = :attrId")
    void deleteByAttrId(@Param("attrId") Integer attrId);
}
