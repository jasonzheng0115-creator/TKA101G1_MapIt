package com.fav.service;

import java.util.List;

import com.fav.model.FavoriteVO;

/**
 * FavService - 收藏功能服務介面
 * 定義收藏相關的業務邏輯方法
 */
public interface FavService {
    
    /**
     * 新增收藏
     * @param memberId 會員編號 (CUST_ID)
     * @param attrId 景點編號
     * @return 新增成功的收藏物件
     */
    FavoriteVO addFavorite(Integer memberId, Integer attrId);
    
    /**
     * 移除收藏
     * @param memberId 會員編號
     * @param attrId 景點編號
     */
    void removeFavorite(Integer memberId, Integer attrId);
    
    /**
     * 查詢會員的所有收藏
     * @param memberId 會員編號
     * @return 收藏清單
     */
    List<FavoriteVO> getFavoritesByMemberId(Integer memberId);
    
    /**
     * 檢查是否已收藏
     * @param memberId 會員編號
     * @param attrId 景點編號
     * @return true 表示已收藏，false 表示未收藏
     */
    boolean isFavorite(Integer memberId, Integer attrId);
}
