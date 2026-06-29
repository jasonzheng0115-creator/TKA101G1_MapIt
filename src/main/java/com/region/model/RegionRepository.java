package com.region.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RegionRepository - 地區資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 */
public interface RegionRepository extends JpaRepository<RegionVO, Integer> {
    
    /**
     * 根據地區名稱查詢地區
     * @param regionName 地區名稱
     * @return 地區物件
     */
    Optional<RegionVO> findByRegionName(String regionName);
}
