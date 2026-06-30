package com.attr.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.attr.model.AttrImageRepository;
import com.attr.model.AttrImageVO;
import com.attr.model.AttrRepository;
import com.attr.model.AttrService;
import com.attr.model.AttrSpecification;
import com.attr.model.AttrVO;
import com.category.model.CategoryVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.region.model.RegionVO;
import com.comment.model.CommentService;
import com.comment.model.CommentVO;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AttrFrontController - 前台景點瀏覽控制層
 * 
 * 職責：
 * 1. 提供前台使用者瀏覽景點的功能
 * 2. 整合 Google Maps API 顯示景點地圖
 * 3. 與後台 AttrController 分離，專注於前台展示
 * 
 * 路由前綴：/front/attr
 */
@Controller
@RequestMapping("/front/attr")
public class AttrFrontController {
    
    // ========== 依賴注入 ==========
    private AttrService attrService;
    private AttrRepository attrRepository;
    private AttrImageRepository attrImageRepository;
    private CommentService commentService;
    
    // 從 application.properties 讀取 Google Maps API Key
    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public AttrFrontController(AttrService attrService, AttrRepository attrRepository, 
                               AttrImageRepository attrImageRepository, CommentService commentService) {
        this.attrService = attrService;
        this.attrRepository = attrRepository;
        this.attrImageRepository = attrImageRepository;
        this.commentService = commentService;
    }
    
    // ========== 前台景點列表（地圖模式） ==========
    
    /**
     * 顯示前台景點列表（地圖模式）
     * 路由：GET /front/attr/list
     * 視圖：templates/front/listAttr.html
     * 
     * 功能說明：
     * 1. 取得所有景點資料（包含經緯度）
     * 2. 將 VO 物件轉換為簡化的 Map 結構（避免循環參考）
     * 3. 使用 Jackson 將資料序列化為 JSON 字串
     * 4. 前端使用 JSON.parse() 解析資料並在地圖上標記景點
     */
    @GetMapping("/list")
    public String list(ModelMap model) {
        // 取得所有景點資料
        List<AttrVO> attrList = attrService.findAll();
        
        // 將景點列表傳遞給前端（用於統計顯示）
        model.addAttribute("attrList", attrList);
        
        // 將 Google Maps API Key 傳遞給前端
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        
        // 將 VO 轉換為簡化的 Map 結構（避免循環參考問題）
        List<Map<String, Object>> simplifiedAttrList = new ArrayList<>();
        
        for (AttrVO attr : attrList) {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("id", attr.getAttrId());
            attrMap.put("name", attr.getAttrName());
            attrMap.put("lat", attr.getLat());
            attrMap.put("lng", attr.getLng());
            attrMap.put("address", attr.getAttrAddress());
            attrMap.put("region", attr.getRegionVO() != null ? attr.getRegionVO().getRegionName() : "");
            attrMap.put("category", attr.getCategoryVO() != null ? attr.getCategoryVO().getCategoryName() : "");
            attrMap.put("tel", attr.getAttrTel());
            attrMap.put("openTime", attr.getOpenTime());
            attrMap.put("isOpen", attr.getIsOpen());
            attrMap.put("avgStars", attr.getAvgStars());
            
            simplifiedAttrList.add(attrMap);
        }
        
        // 使用 Jackson ObjectMapper 將 List<Map> 轉為 JSON 字串
        ObjectMapper objectMapper = new ObjectMapper();
        String attrListJson = "[]";
        
        try {
            attrListJson = objectMapper.writeValueAsString(simplifiedAttrList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 若轉換失敗，使用空陣列
            attrListJson = "[]";
        }
        
        // 將 JSON 字串傳遞給前端
        model.addAttribute("attrListJson", attrListJson);
        
        // 返回前台景點列表頁面
        return "front-end/attr/listAttr";
    }
    
    // ========== 前台景點詳情頁 ==========
    
    /**
     * 顯示景點詳情頁
     * 路由：GET /front/attr/detail/{attrId}
     * 視圖：templates/front/detailAttr.html
     * 
     * 功能說明：
     * 1. 根據景點 ID 查詢景點資料
     * 2. 查詢該景點的所有圖片（主圖優先）
     * 3. 傳遞資料到前端進行展示
     * 
     * @param attrId 景點 ID
     * @param model Spring MVC Model
     * @return 視圖名稱
     */
    @GetMapping("/detail/{attrId}")
    public String getDetail(@PathVariable Integer attrId, ModelMap model) {
        // 1. 查詢景點資料
        AttrVO attr = attrService.findById(attrId);
        
        // 2. 若景點不存在，導向錯誤頁面或列表頁
        if (attr == null) {
            model.addAttribute("errorMessage", "找不到指定的景點（ID: " + attrId + "）");
            return "redirect:/front-end/attr/list";
        }
        
        // 3. 查詢該景點的所有圖片（主圖優先，依上傳時間排序）
        List<AttrImageVO> images = attrImageRepository.findByAttrId(attrId);
        
        // 4. 將資料傳遞到前端
        model.addAttribute("attr", attr);
        model.addAttribute("images", images);
        
        // 查詢該景點已審核通過的評論並放入 model
        List<CommentVO> commentList = commentService.getApprovedComments(attrId);
        model.addAttribute("commentList", commentList);
        
        // 5. 將 Google Maps API Key 傳遞給前端
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        
        // 6. 返回詳情頁視圖
        return "front-end/attr/detailAttr";
    }
    
    /**
     * 關於我們頁面
     * 路由：GET /front/attr/about
     * 視圖：templates/front-end/about.html
     */
    @GetMapping("/about")
    public String about() {
        return "front-end/about"; 
    }
    
    // ========== 前台景點搜尋（支援複合條件與分頁） ==========
    
    /**
     * 景點搜尋功能（支援關鍵字、地區、類別的複合搜尋與分頁）
     * 路由：GET /front/attr/search
     * 視圖：templates/front/searchAttr.html
     * 
     * 功能說明：
     * 1. 接收搜尋參數（keyword, regionId, categoryId）
     * 2. 使用 JPA Specification 進行動態查詢
     * 3. 支援分頁與排序
     * 4. 將搜尋結果與篩選條件傳遞到前端
     * 
     * @param keyword 景點名稱關鍵字（可選）
     * @param regionId 地區 ID（可選）
     * @param categoryId 類別 ID（可選）
     * @param page 頁碼（從 0 開始，預設 0）
     * @param size 每頁筆數（預設 12）
     * @param model Spring MVC Model
     * @return 視圖名稱
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            ModelMap model) {
        
        // 1. 建立分頁參數（依景點名稱排序）
        Pageable pageable = PageRequest.of(page, size, Sort.by("attrName").ascending());
        
        // 解析大區域參數對應的複數地區 ID
        List<Integer> regionIds = null;
        if (area != null && !area.trim().isEmpty()) {
            regionIds = new ArrayList<>();
            switch (area.trim()) {
                case "北部地區":
                    regionIds.addAll(List.of(1, 2, 3, 4, 5, 6, 17));
                    break;
                case "中部地區":
                    regionIds.addAll(List.of(7, 8, 9, 10, 11));
                    break;
                case "南部地區":
                    regionIds.addAll(List.of(12, 13, 14, 15, 16));
                    break;
                case "東部地區":
                    regionIds.addAll(List.of(18, 19));
                    break;
                case "離島地區":
                    regionIds.addAll(List.of(20, 21, 22));
                    break;
                default:
                    break;
            }
        } else if (regionId != null) {
            regionIds = new ArrayList<>();
            regionIds.add(regionId);
        }
        
        // 2. 使用 Specification 建立動態查詢條件
        Specification<AttrVO> spec = AttrSpecification.buildSpecification(keyword, regionIds, categoryId);
        
        // 3. 執行查詢
        Page<AttrVO> attrPage = attrRepository.findAll(spec, pageable);
        
        // 4. 為每個景點建立包含主圖的 Map（與首頁一致的資料結構）
        List<Map<String, Object>> attrWithImages = attrPage.getContent().stream()
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
                .collect(java.util.stream.Collectors.toList());
        
        // 5. 取得地區與類別列表（供下拉選單使用）
        List<RegionVO> regions = attrService.getAllRegions();
        List<CategoryVO> categories = attrService.getAllCategories();
        
        // 6. 將資料傳遞到前端
        model.addAttribute("attrPage", attrPage);
        model.addAttribute("attrWithImages", attrWithImages);
        model.addAttribute("regions", regions);
        model.addAttribute("categories", categories);
        
        // 7. 保留搜尋條件（用於前端顯示與分頁連結）
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("regionId", regionId);
        model.addAttribute("area", area != null ? area : "");
        model.addAttribute("categoryId", categoryId);
        
        // 8. 返回搜尋結果頁面
        return "front-end/attr/searchAttr";
    }
    
    // ========== 收藏功能 API ==========
    
    /**
     * 加入收藏
     * 路由：POST /front/attr/collect
     * 
     * 功能說明：
     * 1. 接收景點 ID 和顧客 ID（目前暫時使用固定值或 Session）
     * 2. 檢查是否已收藏，避免重複
     * 3. 儲存收藏記錄到資料庫
     * 
     * @param attrId 景點 ID
     * @return JSON 回應（成功或失敗訊息）
     */
    @org.springframework.web.bind.annotation.PostMapping("/collect")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> addToCollect(
            @org.springframework.web.bind.annotation.RequestParam Integer attrId) {
        
        try {
            // TODO: 從 Session 或 JWT 取得當前登入的顧客 ID
            // 目前暫時使用固定值 1 作為測試
            Integer custId = 1;
            
            // 檢查景點是否存在
            AttrVO attr = attrService.findById(attrId);
            if (attr == null) {
                return org.springframework.http.ResponseEntity
                        .badRequest()
                        .body(Map.of("success", false, "message", "景點不存在"));
            }
            
            // TODO: 檢查是否已收藏（需要在 FavoriteRepository 中新增查詢方法）
            // 目前暫時跳過重複檢查
            
            // 建立收藏記錄
            com.fav.model.FavoriteVO favorite = new com.fav.model.FavoriteVO();
            favorite.setAttrVO(attr);
            favorite.setCustId(custId);
            favorite.setCollectTime(java.time.LocalDateTime.now());
            
            // 儲存收藏記錄（需要注入 FavoriteRepository）
            // favoriteRepository.save(favorite);
            
            // 暫時返回成功訊息（實際儲存功能待 FavoriteService 建立後實作）
            return org.springframework.http.ResponseEntity.ok(
                    Map.of("success", true, "message", "收藏成功！（功能開發中）")
            );
            
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "收藏失敗：" + e.getMessage()));
        }
    }
}
