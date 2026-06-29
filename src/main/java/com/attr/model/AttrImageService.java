package com.attr.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.attr.util.FileUploadUtil;

/**
 * AttrImageService - 景點圖片業務邏輯層
 * 負責處理圖片上傳、刪除、主圖設定等業務邏輯
 * 
 * 更新說明 (2026/06/22)：
 * - 從 com.image.model.ImageService 重構為 com.attr.model.AttrImageService
 * - 符合 CLAUDE.md 專案規範：使用 com.attr 模組
 * - 方法命名遵循老師規範：addXxx, updateXxx, deleteXxx, getOneXxx, getAll
 */
@Service
public class AttrImageService {
    
    @Autowired
    private AttrImageRepository attrImageRepository;
    
    @Autowired
    private AttrRepository attrRepository;
    
    /**
     * 上傳圖片 (存實體檔 + 寫入 DB)
     * @param attrId 景點 ID
     * @param file 上傳的圖片檔案
     * @param isMain 是否設為主圖
     * @return 儲存後的 AttrImageVO
     * @throws Exception 上傳失敗時拋出
     */
    @Transactional
    public AttrImageVO addImage(Integer attrId, MultipartFile file, Boolean isMain) throws Exception {
        // 1. 驗證景點是否存在
        Optional<AttrVO> attrOpt = attrRepository.findById(attrId);
        if (!attrOpt.isPresent()) {
            throw new IllegalArgumentException("景點不存在: attrId = " + attrId);
        }
        
        // 2. 驗證檔案類型
        if (!FileUploadUtil.isImageFile(file)) {
            throw new IllegalArgumentException("檔案類型錯誤，僅支援圖片格式");
        }
        
        // 3. 如果要設為主圖，先將該景點的所有圖片設為非主圖
        if (isMain != null && isMain) {
            attrImageRepository.unsetAllMainImages(attrId);
        }
        
        // 4. 上傳檔案到本機
        String filename = FileUploadUtil.uploadFile(file);
        
        // 5. 建立 AttrImageVO 並儲存到資料庫
        AttrImageVO imageVO = new AttrImageVO();
        imageVO.setAttrVO(attrOpt.get());
        imageVO.setImagePath(filename);
        imageVO.setIsMain(isMain != null ? isMain : false);
        imageVO.setUploadTime(LocalDateTime.now());
        
        return attrImageRepository.save(imageVO);
    }
    
    /**
     * 批次上傳多張圖片
     * @param attrId 景點 ID
     * @param files 上傳的圖片檔案陣列
     * @return 儲存後的 AttrImageVO 清單
     * @throws Exception 上傳失敗時拋出
     */
    @Transactional
    public List<AttrImageVO> addMultipleImages(Integer attrId, MultipartFile[] files) throws Exception {
        // 驗證景點是否存在
        Optional<AttrVO> attrOpt = attrRepository.findById(attrId);
        if (!attrOpt.isPresent()) {
            throw new IllegalArgumentException("景點不存在: attrId = " + attrId);
        }
        
        // 檢查該景點是否已有主圖
        AttrImageVO mainImage = attrImageRepository.findMainImageByAttrId(attrId);
        boolean hasMainImage = (mainImage != null);
        
        // 逐一上傳檔案
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            
            // 第一張圖片且沒有主圖時，設為主圖
            boolean isMain = (i == 0 && !hasMainImage);
            
            addImage(attrId, file, isMain);
        }
        
        // 回傳該景點的所有圖片
        return attrImageRepository.findByAttrId(attrId);
    }
    
    /**
     * 設定主圖 (使用 @Transactional 確保先清空舊主圖再設定新主圖)
     * @param imageId 要設為主圖的圖片 ID
     * @throws Exception 設定失敗時拋出
     */
    @Transactional
    public void updateMainImage(Integer imageId) throws Exception {
        // 1. 查詢圖片是否存在
        Optional<AttrImageVO> imageOpt = attrImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            throw new IllegalArgumentException("圖片不存在: imageId = " + imageId);
        }
        
        AttrImageVO imageVO = imageOpt.get();
        Integer attrId = imageVO.getAttrVO().getAttrId();
        
        // 2. 先將該景點的所有圖片設為非主圖
        attrImageRepository.unsetAllMainImages(attrId);
        
        // 3. 設定指定圖片為主圖
        imageVO.setIsMain(true);
        attrImageRepository.save(imageVO);
    }
    
    /**
     * 刪除圖片 (刪除 DB 記錄 + 刪除實體檔案)
     * @param imageId 圖片 ID
     * @throws Exception 刪除失敗時拋出
     */
    @Transactional
    public void deleteImage(Integer imageId) throws Exception {
        // 1. 查詢圖片是否存在
        Optional<AttrImageVO> imageOpt = attrImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            throw new IllegalArgumentException("圖片不存在: imageId = " + imageId);
        }
        
        AttrImageVO imageVO = imageOpt.get();
        String filename = imageVO.getImagePath();
        
        // 2. 刪除資料庫記錄
        attrImageRepository.deleteById(imageId);
        
        // 3. 刪除實體檔案
        if (filename != null && !filename.isEmpty()) {
            FileUploadUtil.deleteFile(filename);
        }
    }
    
    /**
     * 刪除指定景點的所有圖片
     * @param attrId 景點 ID
     */
    @Transactional
    public void deleteAllImagesByAttrId(Integer attrId) {
        // 1. 查詢該景點的所有圖片
        List<AttrImageVO> images = attrImageRepository.findByAttrId(attrId);
        
        // 2. 逐一刪除實體檔案
        for (AttrImageVO image : images) {
            if (image.getImagePath() != null && !image.getImagePath().isEmpty()) {
                FileUploadUtil.deleteFile(image.getImagePath());
            }
        }
        
        // 3. 刪除資料庫記錄
        attrImageRepository.deleteByAttrId(attrId);
    }
    
    /**
     * 查詢指定景點的所有圖片
     * @param attrId 景點 ID
     * @return 圖片清單 (主圖優先，再依上傳時間排序)
     */
    public List<AttrImageVO> getImagesByAttrId(Integer attrId) {
        return attrImageRepository.findByAttrId(attrId);
    }
    
    /**
     * 查詢指定景點的主圖
     * @param attrId 景點 ID
     * @return 主圖 (可能為 null)
     */
    public AttrImageVO getMainImageByAttrId(Integer attrId) {
        return attrImageRepository.findMainImageByAttrId(attrId);
    }
    
    /**
     * 根據圖片 ID 查詢圖片
     * @param imageId 圖片 ID
     * @return AttrImageVO (可能為 null)
     */
    public AttrImageVO getOneImage(Integer imageId) {
        return attrImageRepository.findById(imageId).orElse(null);
    }
    
    /**
     * 透過 URL 新增圖片 (不下載到本地，直接儲存 URL)
     * @param attrId 景點 ID
     * @param imageUrl 圖片 URL
     * @param isMain 是否設為主圖
     * @return 儲存後的 AttrImageVO
     * @throws Exception 新增失敗時拋出
     */
    @Transactional
    public AttrImageVO addImageByUrl(Integer attrId, String imageUrl, Boolean isMain) throws Exception {
        // 1. 驗證景點是否存在
        Optional<AttrVO> attrOpt = attrRepository.findById(attrId);
        if (!attrOpt.isPresent()) {
            throw new IllegalArgumentException("景點不存在: attrId = " + attrId);
        }
        
        // 2. 驗證 URL 是否為空
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("圖片 URL 不可為空");
        }
        
        // 3. 如果要設為主圖，先將該景點的所有圖片設為非主圖
        if (isMain != null && isMain) {
            attrImageRepository.unsetAllMainImages(attrId);
        }
        
        // 4. 建立 AttrImageVO 並儲存到資料庫 (僅儲存 URL，不儲存實體檔案)
        AttrImageVO imageVO = new AttrImageVO();
        imageVO.setAttrVO(attrOpt.get());
        imageVO.setImageUrl(imageUrl);  // 使用 imageUrl 欄位
        imageVO.setIsMain(isMain != null ? isMain : false);
        imageVO.setUploadTime(LocalDateTime.now());
        
        return attrImageRepository.save(imageVO);
    }
}
