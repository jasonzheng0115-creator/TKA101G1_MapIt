package com.fav.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * FavoriteRepository - 收藏清單資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 */
public interface FavoriteRepository extends JpaRepository<FavoriteVO, Integer> {
    
    /**
     * 根據會員編號查詢所有收藏
     * @param custId 會員編號
     * @return 收藏清單
     */
    List<FavoriteVO> findByCustId(Integer custId);
    
    /**
     * 根據會員編號和景點編號查詢收藏
     * @param custId 會員編號
     * @param attrId 景點編號
     * @return 收藏物件（如果存在）
     */
    @Query("SELECT f FROM FavoriteVO f WHERE f.custId = :custId AND f.attrVO.attrId = :attrId")
    Optional<FavoriteVO> findByCustIdAndAttrId(@Param("custId") Integer custId, @Param("attrId") Integer attrId);
    
    /**
     * 刪除特定會員的特定景點收藏
     * @param custId 會員編號
     * @param attrId 景點編號
     */
    @Modifying
    @Query("DELETE FROM FavoriteVO f WHERE f.custId = :custId AND f.attrVO.attrId = :attrId")
    void deleteByCustIdAndAttrId(@Param("custId") Integer custId, @Param("attrId") Integer attrId);
}
