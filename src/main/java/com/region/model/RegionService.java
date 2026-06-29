package com.region.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegionService - 地區業務邏輯層
 * 
 * 職責：
 * 1. 封裝所有與地區相關的業務邏輯
 * 2. 呼叫 RegionRepository 進行資料存取
 * 3. 提供給 Controller 層使用
 */
@Service
@Transactional
public class RegionService {
    
    // ========== 依賴注入 ==========
    private RegionRepository regionRepository;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
    
    // ========== 地區新增方法 ==========
    
    /**
     * 新增地區
     * @param regionVO 地區物件
     * @return 儲存後的地區物件
     */
    public RegionVO addRegion(RegionVO regionVO) {
        return regionRepository.save(regionVO);
    }
    
    // ========== 地區更新方法 ==========
    
    /**
     * 更新地區
     * @param regionVO 地區物件（必須包含 regionId）
     * @return 更新後的地區物件
     */
    public RegionVO updateRegion(RegionVO regionVO) {
        return regionRepository.save(regionVO);
    }
    
    // ========== 地區刪除方法 ==========
    
    /**
     * 刪除地區
     * @param regionId 地區 ID
     */
    public void deleteRegion(Integer regionId) {
        regionRepository.deleteById(regionId);
    }
    
    // ========== 地區查詢方法 ==========
    
    /**
     * 根據地區 ID 查詢單一地區
     * @param regionId 地區 ID
     * @return 地區物件，若不存在則回傳 null
     */
    public RegionVO getOneRegion(Integer regionId) {
        Optional<RegionVO> optional = regionRepository.findById(regionId);
        return optional.orElse(null);
    }
    
    /**
     * 查詢所有地區
     * @return 所有地區列表
     */
    public List<RegionVO> getAll() {
        return regionRepository.findAll();
    }
}
