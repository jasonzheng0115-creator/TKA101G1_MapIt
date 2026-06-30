package com.reports.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comment.model.CommentService;

/**
 * ReportService - 檢舉管理業務邏輯層
 * 
 * 職責：
 * 1. 封裝所有與檢舉相關的業務邏輯
 * 2. 呼叫 ReportRepository 進行資料存取
 * 3. 提供檢舉審核通過功能（更新狀態 + 調用 CommentService 強制下架評論）
 * 4. 提供給 Controller 層使用
 */
@Service
@Transactional
public class ReportService {
    
    // ========== 依賴注入 ==========
    private ReportRepository reportRepository;
    private CommentService commentService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public ReportService(ReportRepository reportRepository, CommentService commentService) {
        this.reportRepository = reportRepository;
        this.commentService = commentService;
    }
    
    // ========== 檢舉新增方法 ==========
    
    /**
     * 新增檢舉
     * @param reportVO 檢舉物件
     * @return 儲存後的檢舉物件
     */
    public ReportVO addReport(ReportVO reportVO) {
        // 若沒有手動設定 ID，自動計算下一個最大 ID
        if (reportVO.getReportId() == null) {
            Integer maxId = reportRepository.findMaxId();
            reportVO.setReportId(maxId == null ? 1 : maxId + 1);
        }
        // 設定檢舉時間為當前時間
        if (reportVO.getReportTime() == null) {
            reportVO.setReportTime(LocalDateTime.now());
        }
        // 預設狀態為待處理 (0)
        if (reportVO.getReportStatus() == null) {
            reportVO.setReportStatus("0");
        }
        return reportRepository.save(reportVO);
    }
    
    // ========== 檢舉更新方法 ==========
    
    /**
     * 更新檢舉
     * @param reportVO 檢舉物件（必須包含 reportId）
     * @return 更新後的檢舉物件
     */
    public ReportVO updateReport(ReportVO reportVO) {
        return reportRepository.save(reportVO);
    }
    
    // ========== 檢舉刪除方法 ==========
    
    /**
     * 刪除檢舉
     * @param reportId 檢舉 ID
     */
    public void deleteReport(Integer reportId) {
        reportRepository.deleteById(reportId);
    }
    
    // ========== 檢舉查詢方法 ==========
    
    /**
     * 根據檢舉 ID 查詢單一檢舉
     * @param reportId 檢舉 ID
     * @return 檢舉物件，若不存在則回傳 null
     */
    public ReportVO getOneReport(Integer reportId) {
        Optional<ReportVO> optional = reportRepository.findById(reportId);
        return optional.orElse(null);
    }
    
    /**
     * 查詢所有檢舉
     * @return 所有檢舉列表
     */
    public List<ReportVO> getAll() {
        return reportRepository.findAll();
    }
    
    // ========== 檢舉審核方法 ==========
    
    /**
     * 審核通過檢舉
     * 執行步驟：
     * 1. 更新檢舉狀態為已處理 (1)
     * 2. 調用 CommentService 將被檢舉的評論強制下架 (狀態改為 2)
     * 
     * @param reportId 檢舉 ID
     * @param empId 處理員工 ID
     * @return 是否審核成功
     */
    public boolean approveReport(Integer reportId, Integer empId) {
        ReportVO report = getOneReport(reportId);
        if (report == null) {
            return false;
        }
        
        // 1. 更新檢舉狀態為已處理 (1) 並寫入審查員工 ID
        report.setReportStatus("1");
        report.setEmpId(empId);
        reportRepository.save(report);
        
        // 2. 調用 CommentService 將被檢舉的評論強制下架 (狀態改為 2)
        if (report.getCommentVO() != null && report.getCommentVO().getCommentId() != null) {
            commentService.updateCommentStatus(report.getCommentVO().getCommentId(), "2");
        }
        
        return true;
    }
    
    /**
     * 駁回檢舉
     * 更新檢舉狀態為已駁回 (2)
     * 
     * @param reportId 檢舉 ID
     * @param empId 處理員工 ID
     * @return 是否駁回成功
     */
    public boolean rejectReport(Integer reportId, Integer empId) {
        ReportVO report = getOneReport(reportId);
        if (report == null) {
            return false;
        }
        
        // 更新檢舉狀態為已駁回 (2) 並寫入審查員工 ID
        report.setReportStatus("2");
        report.setEmpId(empId);
        reportRepository.save(report);
        
        return true;
    }
}
