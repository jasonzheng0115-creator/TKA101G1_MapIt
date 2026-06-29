package com.attr.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.category.model.CategoryRepository;
import com.category.model.CategoryVO;
import com.region.model.RegionRepository;
import com.region.model.RegionVO;

/**
 * AttrService - 景點業務邏輯層
 * 
 * 職責：
 * 1. 封裝所有與景點相關的業務邏輯
 * 2. 呼叫 AttrRepository 進行資料存取
 * 3. 提供給 Controller 層使用
 */
@Service
@Transactional
public class AttrService {
    
    // ========== 依賴注入 ==========
    private AttrRepository attrRepository;
    private RegionRepository regionRepository;
    private CategoryRepository categoryRepository;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public AttrService(AttrRepository attrRepository, 
                       RegionRepository regionRepository,
                       CategoryRepository categoryRepository) {
        this.attrRepository = attrRepository;
        this.regionRepository = regionRepository;
        this.categoryRepository = categoryRepository;
    }
    
    // ========== 景點查詢方法 ==========
    
    /**
     * 查詢所有景點
     * @return 所有景點列表
     */
    public List<AttrVO> findAll() {
        return attrRepository.findAll();
    }
    
    /**
     * 分頁查詢所有景點
     * @param pageable 分頁參數
     * @return 景點分頁資料
     */
    public Page<AttrVO> findAll(Pageable pageable) {
        return attrRepository.findAll(pageable);
    }
    
    /**
     * 根據地區 ID 查詢景點
     * @param regionId 地區 ID
     * @return 該地區的所有景點
     */
    public List<AttrVO> findByRegionId(Integer regionId) {
        // 使用 JPA 方法命名規則自動生成查詢
        // 需要在 AttrRepository 中新增此方法
        return attrRepository.findByRegionVO_RegionId(regionId);
    }
    
    /**
     * 根據地區 ID 查詢景點（支援分頁）
     * @param regionId 地區 ID
     * @param pageable 分頁參數
     * @return 該地區的景點分頁資料
     */
    public Page<AttrVO> findByRegionId(Integer regionId, Pageable pageable) {
        return attrRepository.findByRegionVO_RegionId(regionId, pageable);
    }
    
    /**
     * 根據類別 ID 查詢景點
     * @param categoryId 類別 ID
     * @return 該類別的所有景點
     */
    public List<AttrVO> findByCategoryId(Integer categoryId) {
        // 使用 JPA 方法命名規則自動生成查詢
        // 需要在 AttrRepository 中新增此方法
        return attrRepository.findByCategoryVO_CategoryId(categoryId);
    }
    
    /**
     * 根據類別 ID 查詢景點（支援分頁）
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 該類別的景點分頁資料
     */
    public Page<AttrVO> findByCategoryId(Integer categoryId, Pageable pageable) {
        return attrRepository.findByCategoryVO_CategoryId(categoryId, pageable);
    }
    
    /**
     * 根據地區 ID 和類別 ID 同時查詢景點（支援分頁）
     * @param regionId 地區 ID
     * @param categoryId 類別 ID
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    public Page<AttrVO> findByRegionIdAndCategoryId(Integer regionId, Integer categoryId, Pageable pageable) {
        return attrRepository.findByRegionVO_RegionIdAndCategoryVO_CategoryId(regionId, categoryId, pageable);
    }
    
    /**
     * 根據多重條件篩選景點（關鍵字 + 地區 + 類別）
     * 支援 8 種查詢組合的統一入口方法
     * @param keyword 關鍵字（可為 null 或空字串）
     * @param regionId 地區 ID（可為 null）
     * @param categoryId 類別 ID（可為 null）
     * @param pageable 分頁參數
     * @return 符合條件的景點分頁資料
     */
    public Page<AttrVO> findAttrByFilters(String keyword, Integer regionId, Integer categoryId, Pageable pageable) {
        // 判斷關鍵字是否有效
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasRegion = regionId != null;
        boolean hasCategory = categoryId != null;
        
        // 根據 8 種組合選擇對應的查詢方法
        if (hasKeyword && hasRegion && hasCategory) {
            // 1. 關鍵字 + 地區 + 類別
            return attrRepository.findByKeywordAndRegionAndCategory(keyword.trim(), regionId, categoryId, pageable);
        } else if (hasKeyword && hasRegion) {
            // 2. 關鍵字 + 地區
            return attrRepository.findByAttrNameContainingAndRegionVO_RegionId(keyword.trim(), regionId, pageable);
        } else if (hasKeyword && hasCategory) {
            // 3. 關鍵字 + 類別
            return attrRepository.findByAttrNameContainingAndCategoryVO_CategoryId(keyword.trim(), categoryId, pageable);
        } else if (hasKeyword) {
            // 4. 只有關鍵字
            return attrRepository.findByAttrNameContaining(keyword.trim(), pageable);
        } else if (hasRegion && hasCategory) {
            // 5. 地區 + 類別
            return attrRepository.findByRegionVO_RegionIdAndCategoryVO_CategoryId(regionId, categoryId, pageable);
        } else if (hasRegion) {
            // 6. 只有地區
            return attrRepository.findByRegionVO_RegionId(regionId, pageable);
        } else if (hasCategory) {
            // 7. 只有類別
            return attrRepository.findByCategoryVO_CategoryId(categoryId, pageable);
        } else {
            // 8. 無任何條件，查詢全部
            return attrRepository.findAll(pageable);
        }
    }
    
    /**
     * 根據景點 ID 查詢單一景點
     * @param attrId 景點 ID
     * @return 景點物件，若不存在則回傳 null
     */
    public AttrVO findById(Integer attrId) {
        Optional<AttrVO> optional = attrRepository.findById(attrId);
        return optional.orElse(null);
    }
    
    // ========== 景點新增/更新/刪除方法 ==========
    
    /**
     * 新增景點
     * @param attrVO 景點物件
     * @return 儲存後的景點物件（含自動生成的 ID）
     */
    public AttrVO save(AttrVO attrVO) {
        return attrRepository.save(attrVO);
    }
    
    /**
     * 更新景點
     * @param attrVO 景點物件（必須包含 attrId）
     * @return 更新後的景點物件
     */
    public AttrVO update(AttrVO attrVO) {
        // JPA 的 save 方法會自動判斷是新增還是更新
        // 若 ID 存在則執行 UPDATE，否則執行 INSERT
        return attrRepository.save(attrVO);
    }
    
    /**
     * 刪除景點
     * @param attrId 景點 ID
     */
    public void deleteById(Integer attrId) {
        attrRepository.deleteById(attrId);
    }
    
    // ========== 下拉選單資料查詢方法 ==========
    
    /**
     * 取得所有地區（供下拉選單使用）
     * @return 所有地區列表
     */
    public List<RegionVO> getAllRegions() {
        return regionRepository.findAll();
    }
    
    /**
     * 取得所有類別（供下拉選單使用）
     * @return 所有類別列表
     */
    public List<CategoryVO> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // ========== 預留：Open Data 批次匯入方法（Phase 6 實作） ==========
    
    /**
     * 🔮 預留方法：從 Open Data 批次匯入景點資料
     * 
     * 實作重點：
     * 1. 使用 RestTemplate 或 WebClient 撈取 JSON 資料
     * 2. 使用 Gson 或 Jackson 解析 JSON
     * 3. 清洗資料（去除重複、驗證必填欄位）
     * 4. 使用 saveAll() 批次儲存，避免逐筆 insert
     * 5. 使用 @Transactional 確保原子性
     * 
     * @param jsonUrl Open Data JSON 資料來源 URL
     */
    // public void importFromOpenData(String jsonUrl) {
    //     // Phase 6 實作
    // }
}
