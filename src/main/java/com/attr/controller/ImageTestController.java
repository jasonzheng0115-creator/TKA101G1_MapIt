package com.attr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.attr.model.ImageDownloaderService;

/**
 * ImageTestController - 圖片下載測試控制器
 * 提供測試端點來觸發圖片下載任務
 * 
 * 建立日期：2026/06/22
 * 符合 CLAUDE.md 規範：
 * - 使用 com.attr.controller 模組
 * - 無 Lombok
 * - 使用 @ResponseBody 回傳純文字
 */
@Controller
public class ImageTestController {
    
    @Autowired
    private ImageDownloaderService imageDownloaderService;
    
    /**
     * 測試端點：下載所有圖片
     * 路徑：/test/download-all
     * @return 下載完成訊息
     */
    @GetMapping("/test/download-all")
    @ResponseBody
    public String downloadAllImages() {
        try {
            imageDownloaderService.downloadImagesToLocal();
            return "下載任務已完成！請查看 Console 輸出以了解詳細結果。";
        } catch (Exception e) {
            e.printStackTrace();
            return "下載任務執行時發生錯誤: " + e.getMessage();
        }
    }
}
