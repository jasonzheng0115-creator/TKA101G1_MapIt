package com.region.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.region.model.RegionService;
import com.region.model.RegionVO;

import jakarta.validation.Valid;

/**
 * RegionController - 地區控制層
 * 
 * 職責：
 * 1. 接收前端 HTTP 請求
 * 2. 呼叫 RegionService 取得資料
 * 3. 將資料傳遞給 Thymeleaf 模板進行渲染
 * 
 * 路由前綴：/region
 */
@Controller
@RequestMapping("/region")
public class RegionController {
    
    // ========== 依賴注入 ==========
    private RegionService regionService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }
    
    // ========== 地區列表查詢 ==========
    
    /**
     * 顯示所有地區列表
     * 路由：GET /region/listAll
     * 視圖：templates/region/listAll.html
     */
    @GetMapping("/listAll")
    public String listAll(ModelMap model) {
        List<RegionVO> regionList = regionService.getAll();
        model.addAttribute("regionList", regionList);
        return "back-end/region/listAll";
    }
    
    // ========== 地區新增 ==========
    
    /**
     * 顯示新增地區表單
     * 路由：GET /region/add
     * 視圖：templates/region/add.html
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        // 建立空白的 RegionVO 物件供表單綁定
        RegionVO regionVO = new RegionVO();
        model.addAttribute("regionVO", regionVO);
        return "back-end/region/add";
    }
    
    /**
     * 處理新增地區表單提交
     * 路由：POST /region/add
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("regionVO") RegionVO regionVO,
                      BindingResult result,
                      ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/region/add";
        }
        
        // 儲存地區資料
        regionService.addRegion(regionVO);
        
        // 重導向到列表頁（使用 redirect 避免重複提交）
        return "redirect:/region/listAll";
    }
    
    // ========== 地區編輯 ==========
    
    /**
     * 顯示編輯地區表單
     * 路由：GET /region/update?regionId=1
     * 視圖：templates/region/update.html
     */
    @GetMapping("/update")
    public String updateForm(@RequestParam("regionId") Integer regionId, ModelMap model) {
        // 根據 ID 查詢地區資料
        RegionVO regionVO = regionService.getOneRegion(regionId);
        
        // 若地區不存在，重導向到列表頁
        if (regionVO == null) {
            return "redirect:/region/listAll";
        }
        
        model.addAttribute("regionVO", regionVO);
        return "back-end/region/update";
    }
    
    /**
     * 處理編輯地區表單提交
     * 路由：POST /region/update
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("regionVO") RegionVO regionVO,
                         BindingResult result,
                         ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/region/update";
        }
        
        // 更新地區資料
        regionService.updateRegion(regionVO);
        
        // 重導向到列表頁
        return "redirect:/region/listAll";
    }
    
    // ========== 地區刪除 ==========
    
    /**
     * 刪除地區
     * 路由：GET /region/delete?regionId=1
     * 刪除後重導向到列表頁
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("regionId") Integer regionId) {
        regionService.deleteRegion(regionId);
        return "redirect:/region/listAll";
    }
}
