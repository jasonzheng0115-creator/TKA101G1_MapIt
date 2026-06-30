package com.reports.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * ReportRepository - 檢舉清單資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 */
public interface ReportRepository extends JpaRepository<ReportVO, Integer> {
    
    @Query("SELECT MAX(r.reportId) FROM ReportVO r")
    Integer findMaxId();
}
