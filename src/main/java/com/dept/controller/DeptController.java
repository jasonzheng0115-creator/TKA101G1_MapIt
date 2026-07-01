package com.dept.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dept.model.DeptService;
import com.dept.model.DeptVO;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/dept") // 設定部門控制器對外的門牌
public class DeptController {

    @Autowired
    private DeptService deptService;

    // 查詢所有部門列表，並帶往部門管理頁面
    @GetMapping("/listAllDept")
    public String listAllDept(ModelMap model) {
        // 1. 呼叫服務層，拿到所有部門的 List 清單
        List<DeptVO> list = deptService.getAll();
        model.addAttribute("deptList", list);

        // 2. 為了讓前端的「新增/修改表單」能進行 th:object 綁定，必須塞入一個空的 DeptVO
        if (!model.containsAttribute("deptVO")) {
            model.addAttribute("deptVO", new DeptVO());
        }

        // 3. 回傳 Thymeleaf 的路徑名稱 (對應到 templates/back-end/dept/listAllDept.html)
        return "back-end/dept/listAllDept";
    }

    // 新增部門
    @PostMapping("/insert")
    public String insert(@Valid DeptVO deptVO, BindingResult result, RedirectAttributes redirectAttributes, ModelMap model) {
        if (result.hasErrors() || deptVO.getDeptName() == null || deptVO.getDeptName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "部門名稱請勿空白");
            return "redirect:/dept/listAllDept";
        }

        // 防呆：檢查部門名稱是否重複
        if (deptService.isDeptNameDuplicate(deptVO.getDeptName().trim())) {
            redirectAttributes.addFlashAttribute("errorMsg", "部門名稱「" + deptVO.getDeptName().trim() + "」已存在，請勿重複新增！");
            redirectAttributes.addFlashAttribute("deptVO", deptVO); // 快閃保存輸入的值，使其保持展開狀態且不遺失
            return "redirect:/dept/listAllDept";
        }

        deptService.save(deptVO);
        return "redirect:/dept/listAllDept";
    }

    // 取得單一部門資料以利修改
    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("deptId") Integer deptId, ModelMap model) {
        DeptVO deptVO = deptService.getOneDept(deptId);
        model.addAttribute("deptVO", deptVO); // 塞入要編輯的部門資料，前端會自動切換為「編輯模式」

        List<DeptVO> list = deptService.getAll();
        model.addAttribute("deptList", list);
        return "back-end/dept/listAllDept";
    }

    // 修改部門資料
    @PostMapping("/update")
    public String update(@Valid DeptVO deptVO, BindingResult result, RedirectAttributes redirectAttributes, ModelMap model) {
        if (result.hasErrors() || deptVO.getDeptName() == null || deptVO.getDeptName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "部門名稱請勿空白");
            return "redirect:/dept/listAllDept";
        }

        // 防呆：檢查修改後的部門名稱是否與其他部門重複
        DeptVO existingDept = deptService.findByDeptName(deptVO.getDeptName().trim());
        if (existingDept != null && !existingDept.getDeptId().equals(deptVO.getDeptId())) {
            redirectAttributes.addFlashAttribute("errorMsg", "部門名稱「" + deptVO.getDeptName().trim() + "」已存在，修改失敗！");
            redirectAttributes.addFlashAttribute("deptVO", deptVO); // 快閃保存修改中的資料，保持編輯狀態
            return "redirect:/dept/listAllDept";
        }

        deptService.save(deptVO);
        return "redirect:/dept/listAllDept";
    }
}
