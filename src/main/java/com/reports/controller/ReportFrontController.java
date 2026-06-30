package com.reports.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.comment.model.CommentService;
import com.comment.model.CommentVO;
import com.reports.model.ReportService;
import com.reports.model.ReportVO;

import jakarta.servlet.http.HttpSession;

/**
 * ReportFrontController - 前台檢舉控制器
 * 
 * 職責：
 * 1. 接收前台使用者提交的評論檢舉
 * 2. 進行權限或驗證檢查
 * 3. 儲存檢舉至資料庫
 */
@Controller
@RequestMapping("/front/report")
public class ReportFrontController {

    private final ReportService reportService;
    private final CommentService commentService;

    public ReportFrontController(ReportService reportService, CommentService commentService) {
        this.reportService = reportService;
        this.commentService = commentService;
    }

    /**
     * 提交檢舉
     * 路由：POST /front/report/add
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addReport(
            @RequestParam("commentId") Integer commentId,
            @RequestParam("reportContent") String reportContent,
            HttpSession session) {

        try {
            // 1. 從 Session 取得登入會員
            com.cust.model.CustVO loginCust = (com.cust.model.CustVO) session.getAttribute("loginCust");
            
            // 2. 嚴格限制必須登入，不再預設為會員 1
            if (loginCust == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "請先登入會員"));
            }
            Integer custId = loginCust.getCustId();

            // 3. 檢查評論是否存在
            CommentVO comment = commentService.getOneComment(commentId);
            if (comment == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "被檢舉的評論不存在"));
            }

            // 4. 建立檢舉案件
            ReportVO report = new ReportVO();
            report.setCommentVO(comment);
            report.setCustId(custId);
            report.setReportContent(reportContent);
            report.setReportStatus("0"); // 0 代表待審核 (未處理)
            report.setReportTime(LocalDateTime.now());

            // 5. 儲存檢舉
            reportService.addReport(report);

            return ResponseEntity.ok(Map.of("success", true, "message", "檢舉提交成功，我們將盡快審查該評論！"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "提交失敗：" + e.getMessage()));
        }
    }
}
