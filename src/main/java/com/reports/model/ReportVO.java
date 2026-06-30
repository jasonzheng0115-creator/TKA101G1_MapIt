package com.reports.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.comment.model.CommentVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * ReportVO - 檢舉清單實體類別
 * 對應資料表：REPORTS
 * 主鍵類型：手動設定 (Manual PK)
 */
@Entity
@Table(name = "REPORTS")
public class ReportVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer reportId;
    private CommentVO commentVO;
    private Integer custId;  // 暫時使用 Integer，等組員建立 Customer 表後再改為物件關聯
    private Integer empId;   // 暫時使用 Integer，等組員建立 Employee 表後再改為物件關聯
    private String reportContent;
    private String reportStatus;
    private LocalDateTime reportTime;
    
    // ========== 建構子 ==========
    public ReportVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @Column(name = "REPORT_ID")
    public Integer getReportId() {
        return reportId;
    }
    
    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
    
    @NotNull(message = "評論: 請選擇評論")
    @ManyToOne
    @JoinColumn(name = "COMMENT_ID", referencedColumnName = "COMMENT_ID")
    public CommentVO getCommentVO() {
        return commentVO;
    }
    
    public void setCommentVO(CommentVO commentVO) {
        this.commentVO = commentVO;
    }
    
    @NotNull(message = "檢舉人編號: 請勿空白")
    @Column(name = "CUST_ID", nullable = false)
    public Integer getCustId() {
        return custId;
    }
    
    public void setCustId(Integer custId) {
        this.custId = custId;
    }
    
    @Column(name = "EMP_ID", nullable = true)
    public Integer getEmpId() {
        return empId;
    }
    
    public void setEmpId(Integer empId) {
        this.empId = empId;
    }
    
    @NotEmpty(message = "檢舉內容: 請勿空白")
    @Column(name = "REPORT_CONTENT", length = 300, nullable = false)
    public String getReportContent() {
        return reportContent;
    }
    
    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }
    
    @Column(name = "REPORT_STATUS", length = 10, nullable = false)
    public String getReportStatus() {
        return reportStatus;
    }
    
    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }
    
    @Column(name = "REPORT_TIME")
    public LocalDateTime getReportTime() {
        return reportTime;
    }
    
    public void setReportTime(LocalDateTime reportTime) {
        this.reportTime = reportTime;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "ReportVO [reportId=" + reportId + ", custId=" + custId 
                + ", empId=" + empId + ", reportStatus=" + reportStatus 
                + ", reportTime=" + reportTime + "]";
    }
}
