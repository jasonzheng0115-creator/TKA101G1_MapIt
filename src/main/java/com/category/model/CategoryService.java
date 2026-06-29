package com.category.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CategoryService - 類別業務邏輯層
 * 
 * 職責：
 * 1. 封裝所有與類別相關的業務邏輯
 * 2. 呼叫 CategoryRepository 進行資料存取
 * 3. 提供給 Controller 層使用
 */
@Service
@Transactional
public class CategoryService {
    
    // ========== 依賴注入 ==========
    private CategoryRepository categoryRepository;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    // ========== 類別新增方法 ==========
    
    /**
     * 新增類別
     * @param categoryVO 類別物件
     * @return 儲存後的類別物件（含自動生成的 ID）
     */
    public CategoryVO addCategory(CategoryVO categoryVO) {
        return categoryRepository.save(categoryVO);
    }
    
    // ========== 類別更新方法 ==========
    
    /**
     * 更新類別
     * @param categoryVO 類別物件（必須包含 categoryId）
     * @return 更新後的類別物件
     */
    public CategoryVO updateCategory(CategoryVO categoryVO) {
        return categoryRepository.save(categoryVO);
    }
    
    // ========== 類別刪除方法 ==========
    
    /**
     * 刪除類別
     * @param categoryId 類別 ID
     */
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }
    
    // ========== 類別查詢方法 ==========
    
    /**
     * 根據類別 ID 查詢單一類別
     * @param categoryId 類別 ID
     * @return 類別物件，若不存在則回傳 null
     */
    public CategoryVO getOneCategory(Integer categoryId) {
        Optional<CategoryVO> optional = categoryRepository.findById(categoryId);
        return optional.orElse(null);
    }
    
    /**
     * 查詢所有類別
     * @return 所有類別列表
     */
    public List<CategoryVO> getAll() {
        return categoryRepository.findAll();
    }
}
