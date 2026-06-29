package com.fav.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav.model.FavoriteVO;
import com.fav.service.FavService;

import jakarta.servlet.http.HttpSession;

/**
 * FavController - 收藏功能控制器
 * 處理收藏相關的前端請求
 */
@Controller
public class FavController {
    
    @Autowired
    private FavService favService;

    // ==========================================
    // 功能 1：處理 Ajax「加入收藏」的背景請求
    // ==========================================
    @PostMapping("/front/favorite/add")
    @ResponseBody
    public Map<String, Object> addToFavorite(
            @RequestParam("attrId") Integer attrId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: 從 Session 取得登入會員的 ID
            // 目前暫時使用測試用的會員 ID = 1
            // 等會員登入功能完成後，改為：
            // Integer memberId = (Integer) session.getAttribute("memberId");
            Integer memberId = 1;
            
            if (memberId == null) {
                response.put("success", false);
                response.put("message", "請先登入會員");
                return response;
            }
            
            // 呼叫 Service 新增收藏
            FavoriteVO favorite = favService.addFavorite(memberId, attrId);
            
            System.out.println("✅ 成功加入收藏！景點 ID：" + attrId + "，會員 ID：" + memberId);
            
            response.put("success", true);
            response.put("message", "加入收藏成功");
            response.put("collectId", favorite.getCollectId());
            
        } catch (Exception e) {
            System.err.println("❌ 加入收藏失敗：" + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "加入收藏失敗：" + e.getMessage());
        }
        
        return response;
    }
    
    // ==========================================
    // 功能 2：處理 Ajax「移除收藏」的背景請求
    // ==========================================
    @PostMapping("/front/favorite/remove")
    @ResponseBody
    public Map<String, Object> removeFavorite(
            @RequestParam("attrId") Integer attrId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: 從 Session 取得登入會員的 ID
            Integer memberId = 1; // 測試用
            
            if (memberId == null) {
                response.put("success", false);
                response.put("message", "請先登入會員");
                return response;
            }
            
            // 呼叫 Service 移除收藏
            favService.removeFavorite(memberId, attrId);
            
            System.out.println("✅ 成功移除收藏！景點 ID：" + attrId + "，會員 ID：" + memberId);
            
            response.put("success", true);
            response.put("message", "移除收藏成功");
            
        } catch (Exception e) {
            System.err.println("❌ 移除收藏失敗：" + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "移除收藏失敗：" + e.getMessage());
        }
        
        return response;
    }

    // ==========================================
    // 功能 3：處理點擊導覽列「我的收藏」開啟列表網頁
    // ==========================================
    @GetMapping("/front/favorite/list")
    public String listFavorites(ModelMap model, HttpSession session) {
        
        try {
            // TODO: 從 Session 取得登入會員的 ID
            Integer memberId = 1; // 測試用
            
            if (memberId == null) {
                // 如果未登入，導向登入頁面
                return "redirect:/login";
            }
            
            // 呼叫 Service 查詢收藏清單
            List<FavoriteVO> favoriteList = favService.getFavoritesByMemberId(memberId);
            
            System.out.println("👉 查詢到 " + favoriteList.size() + " 筆收藏記錄");
            
            // 將收藏清單傳給前端頁面
            model.addAttribute("favoriteList", favoriteList);
            
        } catch (Exception e) {
            System.err.println("❌ 查詢收藏清單失敗：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "查詢收藏清單失敗");
        }
        
        // 回傳 Thymeleaf 網頁檔案名稱
        return "front-end/fav/favorite"; 
    }
}
