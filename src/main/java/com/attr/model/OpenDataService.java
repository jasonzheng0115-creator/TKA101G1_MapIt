package com.attr.model;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attr.dto.OpenDataDto;
import com.category.model.CategoryRepository;
import com.category.model.CategoryVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.region.model.RegionRepository;
import com.region.model.RegionVO;

/**
 * OpenDataService - 負責從本地 JSON 檔案匯入景點資料
 * 使用 ClassPathResource 讀取本地檔案並將資料轉換為 AttrVO 儲存
 */
@Service
@Transactional
public class OpenDataService {
    
    @Autowired
    private AttrRepository attrRepository;
    
    @Autowired
    private AttrImageRepository attrImageRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 本地 JSON 檔案路徑
    private static final String JSON_FILE_PATH = "static/scenic_spots.json";
    
    /**
     * 從本地 JSON 檔案匯入景點資料
     * @return 成功匯入的景點數量
     */
    public int importAttractions() {
        try {
            // 1. 使用 ClassPathResource 讀取本地 JSON 檔案
            ClassPathResource resource = new ClassPathResource(JSON_FILE_PATH);
            InputStream inputStream = resource.getInputStream();
            
            // 2. 使用 ObjectMapper 將 JSON 轉換為 DTO 陣列
            ObjectMapper objectMapper = new ObjectMapper();
            OpenDataDto[] dtoArray = objectMapper.readValue(inputStream, OpenDataDto[].class);
            
            if (dtoArray == null || dtoArray.length == 0) {
                System.out.println("未取得任何資料");
                return 0;
            }
            
            // 3. 取得預設的 Region 和 Category（防呆處理）
            RegionVO defaultRegion = getOrCreateDefaultRegion();
            CategoryVO defaultCategory = getOrCreateDefaultCategory();
            
            // 4. 遍歷 DTO 陣列，轉換並儲存資料
            int count = 0;
            int skippedCount = 0;
            for (OpenDataDto dto : dtoArray) {
                try {
                    // 取得景點名稱
                    String attrName = dto.getScenicSpotName() != null ? dto.getScenicSpotName() : "未命名景點";
                    
                    // 檢查資料庫中是否已存在相同名稱的景點（防重複匯入）
                    Optional<AttrVO> existingAttr = attrRepository.findByAttrName(attrName);
                    if (existingAttr.isPresent()) {
                        System.out.println("景點已存在，跳過匯入: " + attrName);
                        skippedCount++;
                        continue; // 跳過此筆資料，不進行新增
                    }
                    
                    // 建立 AttrVO
                    AttrVO attrVO = new AttrVO();
                    
                    // 設定基本資料
                    attrVO.setAttrName(attrName);
                    attrVO.setAttrAddress(dto.getAddress() != null ? dto.getAddress() : "地址未提供");
                    
                    // 設定預設的 Region 和 Category
                    attrVO.setRegionVO(defaultRegion);
                    attrVO.setCategoryVO(defaultCategory);
                    
                    // 設定預設的經緯度（台灣中心點附近）
                    attrVO.setLat(new BigDecimal("23.5000000"));
                    attrVO.setLng(new BigDecimal("121.0000000"));
                    
                    // 設定其他預設值
                    attrVO.setIsOpen("Y");
                    attrVO.setAttrVotes(0);
                    attrVO.setAttrStars(0);
                    attrVO.setAvgStars(new BigDecimal("0.0"));
                    
                    // 儲存景點
                    AttrVO savedAttr = attrRepository.save(attrVO);
                    
                    // 5. 處理圖片（如果有 PictureUrl1）- 直接存 URL，不下載
                    if (dto.getPicture() != null && dto.getPicture().containsKey("PictureUrl1")) {
                        String pictureUrl = dto.getPicture().get("PictureUrl1");
                        if (pictureUrl != null && !pictureUrl.trim().isEmpty()) {
                            AttrImageVO imageVO = new AttrImageVO();
                            imageVO.setAttrVO(savedAttr);
                            imageVO.setImageUrl(pictureUrl);  // 直接存 URL 到 IMAGE_URL 欄位
                            imageVO.setIsMain(true);
                            imageVO.setUploadTime(LocalDateTime.now());
                            attrImageRepository.save(imageVO);
                        }
                    }
                    
                    count++;
                    System.out.println("成功匯入景點: " + dto.getScenicSpotName());
                    
                } catch (Exception e) {
                    System.err.println("匯入景點失敗: " + dto.getScenicSpotName() + ", 錯誤: " + e.getMessage());
                    // 繼續處理下一筆資料
                }
            }
            
            System.out.println("總共成功匯入 " + count + " 筆景點資料，跳過 " + skippedCount + " 筆重複資料");
            return count;
            
        } catch (IOException e) {
            System.err.println("讀取 JSON 檔案失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("讀取 JSON 檔案失敗", e);
        } catch (Exception e) {
            System.err.println("匯入 Open Data 失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("匯入 Open Data 失敗", e);
        }
    }
    
    /**
     * 取得或建立預設的 Region (ID=1)
     */
    private RegionVO getOrCreateDefaultRegion() {
        Optional<RegionVO> regionOpt = regionRepository.findById(1);
        if (regionOpt.isPresent()) {
            return regionOpt.get();
        } else {
            // 如果不存在，建立一筆預設的 Region
            RegionVO defaultRegion = new RegionVO();
            defaultRegion.setRegionId(1);
            defaultRegion.setRegionName("未分類地區");
            return regionRepository.save(defaultRegion);
        }
    }
    
    /**
     * 取得或建立預設的 Category (ID=1)
     */
    private CategoryVO getOrCreateDefaultCategory() {
        Optional<CategoryVO> categoryOpt = categoryRepository.findById(1);
        if (categoryOpt.isPresent()) {
            return categoryOpt.get();
        } else {
            // 如果不存在，建立一筆預設的 Category
            CategoryVO defaultCategory = new CategoryVO();
            defaultCategory.setCategoryName("未分類類別");
            return categoryRepository.save(defaultCategory);
        }
    }
}
