package com.category.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.category.model.CategoryService;
import com.category.model.CategoryVO;

import jakarta.validation.Valid;

/**
 * CategoryController - 類別控制層
 * 
 * 職責：
 * 1. 接收前端 HTTP 請求
 * 2. 呼叫 CategoryService 取得資料
 * 3. 將資料傳遞給 Thymeleaf 模板進行渲染
 * 
 * 路由前綴：/category
 */
@Controller
@RequestMapping("/category")
public class CategoryController {
    
    // ========== 依賴注入 ==========
    private CategoryService categoryService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    // ========== 類別列表查詢 ==========
    
    /**
     * 顯示所有類別列表
     * 路由：GET /category/listAll
     * 視圖：templates/category/listAll.html
     */
    @GetMapping("/listAll")
    public String listAll(ModelMap model) {
        List<CategoryVO> categoryList = categoryService.getAll();
        model.addAttribute("categoryList", categoryList);
        return "back-end/category/listAll";
    }
    
    // ========== 類別新增 ==========
    
    /**
     * 顯示新增類別表單
     * 路由：GET /category/add
     * 視圖：templates/category/add.html
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        // 建立空白的 CategoryVO 物件供表單綁定
        CategoryVO categoryVO = new CategoryVO();
        model.addAttribute("categoryVO", categoryVO);
        return "back-end/category/add";
    }
    
    /**
     * 處理新增類別表單提交
     * 路由：POST /category/add
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("categoryVO") CategoryVO categoryVO,
                      BindingResult result,
                      ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/category/add";
        }
        
        // 儲存類別資料
        categoryService.addCategory(categoryVO);
        
        // 重導向到列表頁（使用 redirect 避免重複提交）
        return "redirect:/category/listAll";
    }
    
    // ========== 類別編輯 ==========
    
    /**
     * 顯示編輯類別表單
     * 路由：GET /category/update?categoryId=1
     * 視圖：templates/category/update.html
     */
    @GetMapping("/update")
    public String updateForm(@RequestParam("categoryId") Integer categoryId, ModelMap model) {
        // 根據 ID 查詢類別資料
        CategoryVO categoryVO = categoryService.getOneCategory(categoryId);
        
        // 若類別不存在，重導向到列表頁
        if (categoryVO == null) {
            return "redirect:/category/listAll";
        }
        
        model.addAttribute("categoryVO", categoryVO);
        return "back-end/category/update";
    }
    
    /**
     * 處理編輯類別表單提交
     * 路由：POST /category/update
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("categoryVO") CategoryVO categoryVO,
                         BindingResult result,
                         ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/category/update";
        }
        
        // 更新類別資料
        categoryService.updateCategory(categoryVO);
        
        // 重導向到列表頁
        return "redirect:/category/listAll";
    }
    
    // ========== 類別刪除 ==========
    
    /**
     * 刪除類別
     * 路由：GET /category/delete?categoryId=1
     * 刪除後重導向到列表頁
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("categoryId") Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/category/listAll";
    }
}
