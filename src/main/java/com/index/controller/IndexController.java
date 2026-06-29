package com.index.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.attr.model.AttrImageRepository;
import com.attr.model.AttrImageVO;
import com.attr.model.AttrService;
import com.attr.model.AttrVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import jakarta.servlet.http.HttpSession;

/**
 * IndexController - 首頁控制器
 * 
 * 用途：處理網站首頁的請求
 * 當使用者訪問 http://localhost:8080/ 時，會導向到 index.html 頁面
 */
@Controller
public class IndexController {

    private final AttrService attrService;
    private final AttrImageRepository attrImageRepository;

    // 從 application.properties 讀取 Google Maps API Key
    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    /**
     * 建構子注入
     */
    public IndexController(AttrService attrService, AttrImageRepository attrImageRepository) {
        this.attrService = attrService;
        this.attrImageRepository = attrImageRepository;
    }

    /**
     * 首頁路由
     * 
     * @return 回傳 Thymeleaf 模板名稱 "index"（對應 templates/index.html）
     */
    @GetMapping("/")
    public String index(ModelMap model, HttpSession session,
            @PageableDefault(size = 300, sort = "attrId") Pageable pageable) {
        // 1. 嘗試從 Session 中取出名為 "loginCust" 的登入會員資料
        com.cust.model.CustVO loginCust = (com.cust.model.CustVO) session.getAttribute("loginCust");
        if (loginCust != null) {
            // 2. 如果已登入，將會員名字存入 Model 傳給前端
            model.addAttribute("userName", loginCust.getCustName());
        }

        // 取得分頁景點資料（一頁 10 筆）

        Page<AttrVO> attrPage = attrService.findAll(pageable);
        List<AttrVO> attrList = attrPage.getContent();

        // 為每個景點建立包含主圖的 Map
        List<Map<String, Object>> attrWithImages = attrList.stream()
                .map(attr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("attr", attr);

                    // 查詢該景點的主圖
                    AttrImageVO mainImage = attrImageRepository.findMainImageByAttrId(attr.getAttrId());
                    map.put("mainImage", mainImage);

                    // 【重要】確保 imageUrl 也被傳遞到前端（用於 fallback 機制）
                    if (mainImage != null) {
                        map.put("imageUrl", mainImage.getImageUrl());
                        map.put("imagePath", mainImage.getImagePath());
                    }

                    return map;
                })
                .collect(Collectors.toList());

        model.addAttribute("attrWithImages", attrWithImages);
        // 將分頁資訊傳遞給前端以產生分頁按鈕
        model.addAttribute("attrPage", attrPage);

        // 將 Google Maps API Key 傳遞給前端
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        return "front-end/index"; // Thymeleaf 會自動找到 templates/index.html
    }
}
