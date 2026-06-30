package com.reports.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reports.model.ReportService;
import com.reports.model.ReportVO;
import com.emp.model.EmpVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * ReportController - 檢舉管理控制層
 * 
 * 職責：
 * 1. 接收前端 HTTP 請求
 * 2. 呼叫 ReportService 取得資料
 * 3. 將資料傳遞給 Thymeleaf 模板進行渲染
 * 4. 處理檢舉審核通過與駁回
 * 
 * 路由前綴：/report
 */
@Controller
@RequestMapping("/report")
public class ReportController {
    
    // ========== 依賴注入 ==========
    private ReportService reportService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    // ========== 檢舉列表查詢 ==========
    
    /**
     * 顯示所有檢舉列表
     * 路由：GET /report/listAll
     * 視圖：templates/report/listAll.html
     */
    @GetMapping("/listAll")
    public String listAll(ModelMap model) {
        List<ReportVO> reportList = reportService.getAll();
        model.addAttribute("reportList", reportList);
        return "back-end/report/listAll";
    }
    
    // ========== 檢舉新增 ==========
    
    /**
     * 顯示新增檢舉表單
     * 路由：GET /report/add
     * 視圖：templates/report/add.html
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        // 建立空白的 ReportVO 物件供表單綁定
        ReportVO reportVO = new ReportVO();
        model.addAttribute("reportVO", reportVO);
        return "back-end/report/add";
    }
    
    /**
     * 處理新增檢舉表單提交
     * 路由：POST /report/add
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("reportVO") ReportVO reportVO,
                      BindingResult result,
                      ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/report/add";
        }
        
        // 儲存檢舉資料
        reportService.addReport(reportVO);
        
        // 重導向到列表頁（使用 redirect 避免重複提交）
        return "redirect:/report/listAll";
    }
    
    // ========== 檢舉編輯 ==========
    
    /**
     * 顯示編輯檢舉表單
     * 路由：GET /report/update?reportId=1
     * 視圖：templates/report/update.html
     */
    @GetMapping("/update")
    public String updateForm(@RequestParam("reportId") Integer reportId, ModelMap model) {
        // 根據 ID 查詢檢舉資料
        ReportVO reportVO = reportService.getOneReport(reportId);
        
        // 若檢舉不存在，重導向到列表頁
        if (reportVO == null) {
            return "redirect:/report/listAll";
        }
        
        model.addAttribute("reportVO", reportVO);
        return "back-end/report/update";
    }
    
    /**
     * 處理編輯檢舉表單提交
     * 路由：POST /report/update
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("reportVO") ReportVO reportVO,
                         BindingResult result,
                         ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/report/update";
        }
        
        // 更新檢舉資料
        reportService.updateReport(reportVO);
        
        // 重導向到列表頁
        return "redirect:/report/listAll";
    }
    
    // ========== 檢舉刪除 ==========
    
    /**
     * 刪除檢舉
     * 路由：GET /report/delete?reportId=1
     * 刪除後重導向到列表頁
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("reportId") Integer reportId) {
        reportService.deleteReport(reportId);
        return "redirect:/report/listAll";
    }
    
    // ========== 檢舉審核 ==========
    
    /**
     * 審核通過檢舉
     * 路由：GET /report/approve?reportId=1
     * 
     * 執行步驟：
     * 1. 更新檢舉狀態為已處理 (1)
     * 2. 調用 CommentService 將被檢舉的評論強制下架 (狀態改為 2)
     */
    @GetMapping("/approve")
    public String approve(@RequestParam("reportId") Integer reportId, HttpSession session) {
        // 從 session 中取出當前登入的員工物件
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        Integer empId = (loginEmp != null) ? loginEmp.getEmpId() : 1001; // 防呆預設值為 1001
        
        // 呼叫 Service 層的審核通過方法
        reportService.approveReport(reportId, empId);
        
        // 重導向到列表頁
        return "redirect:/report/listAll";
    }
    
    /**
     * 駁回檢舉
     * 路由：GET /report/reject?reportId=1
     * 
     * 執行步驟：
     * 1. 更新檢舉狀態為已駁回 (2)
     */
    @GetMapping("/reject")
    public String reject(@RequestParam("reportId") Integer reportId, HttpSession session) {
        // 從 session 中取出當前登入的員工物件
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        Integer empId = (loginEmp != null) ? loginEmp.getEmpId() : 1001; // 防呆預設值為 1001
        
        // 呼叫 Service 層的駁回方法
        reportService.rejectReport(reportId, empId);
        
        // 重導向到列表頁
        return "redirect:/report/listAll";
    }
}
