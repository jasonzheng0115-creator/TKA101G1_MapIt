package com.emp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dept.model.DeptService;
import com.dept.model.DeptVO;
import com.emp.model.EmpService;
import com.emp.model.EmpVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/back-end/emp") // 員工管理控制器門牌
public class EmpController {

    @Autowired
    private EmpService empService;

    @Autowired
    private DeptService deptService;

    // 登入頁面路由 (直接對應 /back-end/login)
    @GetMapping("/login")
    public String loginPage() {
        return "back-end/login"; // templates/back-end/login.html
    }

    // 登入驗證
    @PostMapping("/loginCheck")
    public String loginCheck(
            @RequestParam("empAcc") String empAcc,
            @RequestParam("empPwd") String empPwd,
            HttpSession session,
            ModelMap model) {

        // 1. 空白防呆
        if (empAcc == null || empAcc.trim().isEmpty() || empPwd == null || empPwd.trim().isEmpty()) {
            model.addAttribute("errorMsg", "帳號或密碼請勿空白");
            return "back-end/login";
        }

        // 2. 登入檢查
        EmpVO empVO = empService.login(empAcc, empPwd);

        // 3. 登入失敗 (帳密錯誤)
        if (empVO == null) {
            model.addAttribute("errorMsg", "帳號或密碼錯誤");
            return "back-end/login";
        }

        // 4. 帳號狀態防呆 (若被停權則不允許登入)
        if (!empVO.getEmpStatus()) {
            model.addAttribute("errorMsg", "該帳號已被停權，請聯絡系統管理員");
            return "back-end/login";
        }

        // 5. 登入成功，存入 session 供 Filter 檢查與頁面右上角顯示
        session.setAttribute("loginEmp", empVO);

        // 6. 重導向至後台首頁 (儀表板)
        return "redirect:/manage";
    }

    // 登出功能
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除 Session 所有內容
        return "redirect:/back-end/emp/login"; // 重導向至登入頁
    }

    // 進入員工管理首頁 (提供複合查詢與管理入口)
    @GetMapping("/select_page")
    public String selectPage(ModelMap model) {
        // 提供部門選單供查詢下拉使用
        List<DeptVO> deptList = deptService.getAll();
        model.addAttribute("deptList", deptList);
        return "back-end/emp/select_page"; // templates/back-end/emp/select_page.html
    }

    // 查詢所有員工
    @GetMapping("/listAllEmp")
    public String listAllEmp(ModelMap model) {
        List<EmpVO> list = empService.listAllEmp();
        model.addAttribute("empList", list);
        return "back-end/emp/listAllEmp"; // templates/back-end/emp/listAllEmp.html
    }

    // 開啟新增員工頁面
    @GetMapping("/addEmp")
    public String addEmpPage(ModelMap model) {
        EmpVO empVO = new EmpVO();
        empVO.setEmpStatus(true); // 預設狀態為啟用
        model.addAttribute("empVO", empVO);

        // 帶入部門清單供下拉選單使用
        List<DeptVO> deptList = deptService.getAll();
        model.addAttribute("deptList", deptList);

        return "back-end/emp/addEmp"; // templates/back-end/emp/addEmp.html
    }

    // 執行新增員工
    @PostMapping("/insert")
    public String insert(
            @Valid @ModelAttribute("empVO") EmpVO empVO,
            BindingResult result,
            ModelMap model) {

        // 1. 若資料驗證有錯誤，回傳原本頁面顯示錯誤訊息
        if (result.hasErrors()) {
            List<DeptVO> deptList = deptService.getAll();
            model.addAttribute("deptList", deptList);
            return "back-end/emp/addEmp";
        }

        // 2. 呼叫服務層進行新增 (內含帳號唯一性防呆)
        try {
            empService.addEmp(empVO);
            return "redirect:/back-end/emp/listAllEmp"; // 新增成功重導向至列表
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            List<DeptVO> deptList = deptService.getAll();
            model.addAttribute("deptList", deptList);
            return "back-end/emp/addEmp";
        }
    }

    // 開啟編輯員工頁面 (依 ID 查詢)
    @PostMapping("/getOne_For_Update")
    public String getOneForUpdate(
            @RequestParam("empId") Integer empId,
            ModelMap model) {

        EmpVO empVO = empService.getOneEmp(empId);
        model.addAttribute("empVO", empVO);

        // 帶入部門清單供下拉選單使用
        List<DeptVO> deptList = deptService.getAll();
        model.addAttribute("deptList", deptList);

        return "back-end/emp/update_emp_input"; // templates/back-end/emp/update_emp_input.html
    }

    // 執行修改員工資料
    @PostMapping("/update")
    public String update(
            @Valid @ModelAttribute("empVO") EmpVO empVO,
            BindingResult result,
            ModelMap model) {

        // 1. 資料驗證有錯誤
        if (result.hasErrors()) {
            List<DeptVO> deptList = deptService.getAll();
            model.addAttribute("deptList", deptList);
            return "back-end/emp/update_emp_input";
        }

        // 2. 執行更新
        empService.updateEmp(empVO);
        return "redirect:/back-end/emp/listAllEmp";
    }

    // 員工複合查詢
    @PostMapping("/listEmps_ByCompositeQuery")
    public String listEmpsByCompositeQuery(
            HttpServletRequest req,
            ModelMap model) {

        // 1. 取得前端傳過來的所有條件 Map
        Map<String, String[]> map = req.getParameterMap();

        // 2. 執行複合查詢
        List<EmpVO> list = empService.listAllEmp(map);

        // 3. 若查無結果，給予錯誤提示
        if (list.isEmpty()) {
            model.addAttribute("errorMsg", "查無符合條件的員工資料");
        }

        // 4. 將查詢結果傳回前端
        model.addAttribute("empList", list);

        // 5. 為了讓複合查詢的頁面也能有部門下拉選單，需帶入部門清單
        List<DeptVO> deptList = deptService.getAll();
        model.addAttribute("deptList", deptList);

        return "back-end/emp/select_page";
    }
}
