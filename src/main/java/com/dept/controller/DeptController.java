package com.dept.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dept.model.DeptService;
import com.dept.model.DeptVO;

@Controller
@RequestMapping("/back-end/dept") // 設定部門控制器對外的門牌
public class DeptController {

    @Autowired
    private DeptService deptService;

    // 查詢所有部門列表，並帶往部門管理頁面
    @GetMapping("/listAllDept")
    public String listAllDept(ModelMap model) {
        // 1. 呼叫服務層，拿到所有部門的 List 清單
        List<DeptVO> list = deptService.getAll();

        // 2. 將部門清單塞進 ModelMap 中，讓 Thymeleaf 前端網頁可以透過 key 鍵 "deptList" 取用
        model.addAttribute("deptList", list);

        // 3. 回傳 Thymeleaf 的路徑名稱 (對應到 templates/back-end/dept/listAllDept.html)
        return "back-end/dept/listAllDept";
    }
}
