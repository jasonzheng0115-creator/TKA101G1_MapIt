package com.attr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.attr.model.AttrImageService;
import com.attr.model.AttrImageVO;

/**
 * AttrImageController - 景點圖片控制器
 * 負責處理圖片上傳、查詢、設定主圖、刪除等操作
 * 
 * 更新說明 (2026/06/22)：
 * - 從 com.image.controller.ImageController 重構為 com.attr.controller.AttrImageController
 * - 符合 CLAUDE.md 專案規範：使用 com.attr 模組
 * - URL 路徑從 /image 改為 /attr/image
 */
@Controller
@RequestMapping("/attr/image")
public class AttrImageController {
    
    @Autowired
    private AttrImageService attrImageService;
    
    /**
     * 查詢指定景點的圖片清單
     * @param attrId 景點 ID
     * @param model Model 物件
     * @return 圖片列表頁面
     */
    @GetMapping("/list/{attrId}")
    public String listImages(@PathVariable("attrId") Integer attrId, ModelMap model) {
        try {
            // 查詢該景點的所有圖片 (主圖優先，再依上傳時間排序)
            List<AttrImageVO> images = attrImageService.getImagesByAttrId(attrId);
            
            // 傳遞資料到前端
            model.addAttribute("images", images);
            model.addAttribute("attrId", attrId);
            
            return "image/list"; // 對應 templates/image/list.html
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "查詢圖片失敗: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 上傳多張圖片
     * @param attrId 景點 ID
     * @param files 上傳的圖片檔案陣列
     * @param redirectAttributes 重導向屬性
     * @return 重導向到圖片列表頁
     */
    @PostMapping("/upload")
    public String uploadImages(
            @RequestParam("attrId") Integer attrId,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes) {
        try {
            // 檢查是否有上傳檔案
            if (files == null || files.length == 0 || files[0].isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "請選擇要上傳的圖片");
                return "redirect:/attr/image/list/" + attrId;
            }
            
            // 批次上傳圖片
            attrImageService.addMultipleImages(attrId, files);
            
            // 成功訊息
            redirectAttributes.addFlashAttribute("successMessage", 
                    "成功上傳 " + files.length + " 張圖片");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "上傳失敗: " + e.getMessage());
        }
        
        return "redirect:/attr/image/list/" + attrId;
    }
    
    /**
     * 設定主圖
     * @param imageId 圖片 ID
     * @param attrId 景點 ID (用於重導向)
     * @param redirectAttributes 重導向屬性
     * @return 重導向到圖片列表頁
     */
    @GetMapping("/setMain/{imageId}")
    public String setMainImage(
            @PathVariable("imageId") Integer imageId,
            @RequestParam("attrId") Integer attrId,
            RedirectAttributes redirectAttributes) {
        try {
            // 設定主圖
            attrImageService.updateMainImage(imageId);
            
            // 成功訊息
            redirectAttributes.addFlashAttribute("successMessage", "主圖設定成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "設定主圖失敗: " + e.getMessage());
        }
        
        return "redirect:/attr/image/list/" + attrId;
    }
    
    /**
     * 刪除圖片
     * @param imageId 圖片 ID
     * @param attrId 景點 ID (用於重導向)
     * @param redirectAttributes 重導向屬性
     * @return 重導向到圖片列表頁
     */
    @GetMapping("/delete/{imageId}")
    public String deleteImage(
            @PathVariable("imageId") Integer imageId,
            @RequestParam("attrId") Integer attrId,
            RedirectAttributes redirectAttributes) {
        try {
            // 刪除圖片 (DB + 實體檔案)
            attrImageService.deleteImage(imageId);
            
            // 成功訊息
            redirectAttributes.addFlashAttribute("successMessage", "圖片刪除成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "刪除失敗: " + e.getMessage());
        }
        
        return "redirect:/attr/image/list/" + attrId;
    }
    
    /**
     * 透過 URL 新增圖片
     * @param attrId 景點 ID
     * @param imageUrl 圖片 URL
     * @param isMain 是否設為主圖
     * @param redirectAttributes 重導向屬性
     * @return 重導向到圖片列表頁
     */
    @PostMapping("/uploadByUrl")
    public String uploadImageByUrl(
            @RequestParam("attrId") Integer attrId,
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam(value = "isMain", required = false, defaultValue = "false") Boolean isMain,
            RedirectAttributes redirectAttributes) {
        try {
            // 檢查 URL 是否為空
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "請輸入圖片 URL");
                return "redirect:/attr/image/list/" + attrId;
            }
            
            // 透過 URL 新增圖片
            attrImageService.addImageByUrl(attrId, imageUrl.trim(), isMain);
            
            // 成功訊息
            redirectAttributes.addFlashAttribute("successMessage", "成功透過 URL 新增圖片");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "新增失敗: " + e.getMessage());
        }
        
        return "redirect:/attr/image/list/" + attrId;
    }
}
