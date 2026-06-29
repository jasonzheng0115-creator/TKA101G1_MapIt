package com.fav.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attr.model.AttrVO;
import com.fav.model.FavoriteRepository;
import com.fav.model.FavoriteVO;

/**
 * FavServiceImpl - 收藏功能服務實作類
 * 實作收藏相關的業務邏輯
 */
@Service
public class FavServiceImpl implements FavService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    /**
     * 新增收藏
     * @param memberId 會員編號 (CUST_ID)
     * @param attrId 景點編號
     * @return 新增成功的收藏物件
     */
    @Override
    @Transactional
    public FavoriteVO addFavorite(Integer memberId, Integer attrId) {
        // 檢查是否已經收藏過
        Optional<FavoriteVO> existing = favoriteRepository.findByCustIdAndAttrId(memberId, attrId);
        if (existing.isPresent()) {
            // 如果已經收藏過，直接返回現有的收藏記錄
            return existing.get();
        }
        
        // 建立新的收藏記錄
        FavoriteVO favorite = new FavoriteVO();
        favorite.setCustId(memberId);
        
        // 建立 AttrVO 物件並設定 ID（只需要 ID 即可建立關聯）
        AttrVO attrVO = new AttrVO();
        attrVO.setAttrId(attrId);
        favorite.setAttrVO(attrVO);
        
        // 設定收藏時間
        favorite.setCollectTime(LocalDateTime.now());
        
        // 儲存到資料庫
        return favoriteRepository.save(favorite);
    }
    
    /**
     * 移除收藏
     * @param memberId 會員編號
     * @param attrId 景點編號
     */
    @Override
    @Transactional
    public void removeFavorite(Integer memberId, Integer attrId) {
        favoriteRepository.deleteByCustIdAndAttrId(memberId, attrId);
    }
    
    /**
     * 查詢會員的所有收藏
     * @param memberId 會員編號
     * @return 收藏清單
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteVO> getFavoritesByMemberId(Integer memberId) {
        return favoriteRepository.findByCustId(memberId);
    }
    
    /**
     * 檢查是否已收藏
     * @param memberId 會員編號
     * @param attrId 景點編號
     * @return true 表示已收藏，false 表示未收藏
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Integer memberId, Integer attrId) {
        return favoriteRepository.findByCustIdAndAttrId(memberId, attrId).isPresent();
    }
}
