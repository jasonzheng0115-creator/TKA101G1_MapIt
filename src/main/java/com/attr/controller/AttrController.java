package com.attr.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.attr.model.AttrService;
import com.attr.model.AttrVO;
import com.attr.model.OpenDataService;
import com.category.model.CategoryVO;
import com.comment.model.CommentService;
import com.comment.model.CommentVO;
import com.region.model.RegionVO;

import jakarta.validation.Valid;

/**
 * AttrController - 景點控制層
 * 
 * 職責：
 * 1. 接收前端 HTTP 請求
 * 2. 呼叫 AttrService 取得資料
 * 3. 將資料傳遞給 Thymeleaf 模板進行渲染
 * 
 * 路由前綴：/attr
 */
@Controller
@RequestMapping("/attr")
public class AttrController {
    
    // ========== 依賴注入 ==========
    private AttrService attrService;
    private OpenDataService openDataService;
    private CommentService commentService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public AttrController(AttrService attrService, OpenDataService openDataService, CommentService commentService) {
        this.attrService = attrService;
        this.openDataService = openDataService;
        this.commentService = commentService;
    }
    
    // ========== 景點列表查詢 ==========
    
    /**
     * 顯示所有景點列表（支援分頁、組合篩選與關鍵字搜尋）
     * 路由：GET /attr/listAll?page=0&keyword=台北&regionId=1&categoryId=2
     * 視圖：templates/attr/listAllAttr.html
     */
    @GetMapping("/listAll")
    public String listAll(@PageableDefault(size = 10, sort = "attrId") Pageable pageable,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer regionId,
                          @RequestParam(required = false) Integer categoryId,
                          ModelMap model) {
        
        // 使用統一的篩選方法處理所有查詢組合
        Page<AttrVO> attrPage = attrService.findAttrByFilters(keyword, regionId, categoryId, pageable);
        
        // 將查詢參數傳回前端（用於表單回填和分頁連結）
        model.addAttribute("attrPage", attrPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRegionId", regionId);
        model.addAttribute("selectedCategoryId", categoryId);
        
        // 載入地區和類別清單供篩選面板使用
        model.addAttribute("regionList", attrService.getAllRegions());
        model.addAttribute("categoryList", attrService.getAllCategories());
        
        return "back-end/attr/listAllAttr";
    }
    
    /**
     * 根據地區篩選景點
     * 路由：GET /attr/filterByRegion?regionId=1
     * 視圖：templates/attr/listAllAttr.html
     */
    @GetMapping("/filterByRegion")
    public String filterByRegion(@RequestParam Integer regionId, ModelMap model) {
        List<AttrVO> attrList = attrService.findByRegionId(regionId);
        model.addAttribute("attrList", attrList);
        model.addAttribute("selectedRegionId", regionId);
        return "back-end/attr/listAllAttr";
    }
    
    /**
     * 根據類別篩選景點
     * 路由：GET /attr/filterByCategory?categoryId=1
     * 視圖：templates/attr/listAllAttr.html
     */
    @GetMapping("/filterByCategory")
    public String filterByCategory(@RequestParam Integer categoryId, ModelMap model) {
        List<AttrVO> attrList = attrService.findByCategoryId(categoryId);
        model.addAttribute("attrList", attrList);
        model.addAttribute("selectedCategoryId", categoryId);
        return "back-end/attr/listAllAttr";
    }
    
    // ========== 景點新增 ==========
    
    /**
     * 顯示新增景點表單
     * 路由：GET /attr/add
     * 視圖：templates/attr/addAttr.html
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        // 建立空白的 AttrVO 物件供表單綁定
        AttrVO attrVO = new AttrVO();
        model.addAttribute("attrVO", attrVO);
        
        // 預載地區與類別下拉選單資料
        List<RegionVO> regionList = attrService.getAllRegions();
        List<CategoryVO> categoryList = attrService.getAllCategories();
        model.addAttribute("regionList", regionList);
        model.addAttribute("categoryList", categoryList);
        
        return "back-end/attr/addAttr";
    }
    
    /**
     * 處理新增景點表單提交
     * 路由：POST /attr/add
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("attrVO") AttrVO attrVO, 
                      BindingResult result, 
                      ModelMap model,
                      @RequestParam("regionVO.regionId") Integer regionId,
                      @RequestParam("categoryVO.categoryId") Integer categoryId) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            // 重新載入下拉選單資料
            List<RegionVO> regionList = attrService.getAllRegions();
            List<CategoryVO> categoryList = attrService.getAllCategories();
            model.addAttribute("regionList", regionList);
            model.addAttribute("categoryList", categoryList);
            return "back-end/attr/addAttr";
        }
        
        // ========== 修正：根據 ID 查詢完整的 RegionVO 和 CategoryVO 物件 ==========
        RegionVO region = attrService.getAllRegions().stream()
                .filter(r -> r.getRegionId().equals(regionId))
                .findFirst()
                .orElse(null);
        
        CategoryVO category = attrService.getAllCategories().stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElse(null);
        
        // 設定完整的關聯物件
        attrVO.setRegionVO(region);
        attrVO.setCategoryVO(category);
        
        // 儲存景點資料
        attrService.save(attrVO);
        
        // 重導向到列表頁（使用 redirect 避免重複提交）
        return "redirect:/attr/listAll";
    }
    
    // ========== 景點編輯 ==========
    
    /**
     * 顯示編輯景點表單
     * 路由：GET /attr/edit/{id}
     * 視圖：templates/attr/editAttr.html
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer attrId, ModelMap model) {
        // 根據 ID 查詢景點資料
        AttrVO attrVO = attrService.findById(attrId);
        
        // 若景點不存在，重導向到列表頁
        if (attrVO == null) {
            return "redirect:/attr/listAll";
        }
        
        model.addAttribute("attrVO", attrVO);
        
        // 預載地區與類別下拉選單資料
        List<RegionVO> regionList = attrService.getAllRegions();
        List<CategoryVO> categoryList = attrService.getAllCategories();
        model.addAttribute("regionList", regionList);
        model.addAttribute("categoryList", categoryList);
        
        return "back-end/attr/editAttr";
    }
    
    /**
     * 處理編輯景點表單提交
     * 路由：POST /attr/edit
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("attrVO") AttrVO attrVO,
                       BindingResult result,
                       ModelMap model,
                       @RequestParam("regionVO.regionId") Integer regionId,
                       @RequestParam("categoryVO.categoryId") Integer categoryId) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            // 重新載入下拉選單資料
            List<RegionVO> regionList = attrService.getAllRegions();
            List<CategoryVO> categoryList = attrService.getAllCategories();
            model.addAttribute("regionList", regionList);
            model.addAttribute("categoryList", categoryList);
            return "back-end/attr/editAttr";
        }
        
        // ========== 修正：根據 ID 查詢完整的 RegionVO 和 CategoryVO 物件 ==========
        RegionVO region = attrService.getAllRegions().stream()
                .filter(r -> r.getRegionId().equals(regionId))
                .findFirst()
                .orElse(null);
        
        CategoryVO category = attrService.getAllCategories().stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElse(null);
        
        // 設定完整的關聯物件
        attrVO.setRegionVO(region);
        attrVO.setCategoryVO(category);
        
        // 更新景點資料
        attrService.update(attrVO);
        
        // 重導向到列表頁
        return "redirect:/attr/listAll";
    }
    
    // ========== 景點刪除 ==========
    
    /**
     * 刪除景點
     * 路由：GET /attr/delete/{id}
     * 刪除後重導向到列表頁
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer attrId) {
        attrService.deleteById(attrId);
        return "redirect:/attr/listAll";
    }
    
    // ========== 景點詳情頁 ==========
    
    /**
     * 顯示景點詳情頁（含評論列表）
     * 路由：GET /attr/detail/{id}
     * 視圖：templates/attr/detail.html
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer attrId, ModelMap model) {
        // 查詢景點資料
        AttrVO attrVO = attrService.findById(attrId);
        
        // 若景點不存在，重導向到列表頁
        if (attrVO == null) {
            return "redirect:/attr/listAll";
        }
        
        // 查詢該景點已上架的評論
        List<CommentVO> commentList = commentService.getApprovedComments(attrId);
        
        // 將景點資料和評論列表放入 Model
        model.addAttribute("attrVO", attrVO);
        model.addAttribute("attr", attrVO);
        model.addAttribute("commentList", commentList);
        model.addAttribute("images", attrVO.getImages());
        
        return "front-end/attr/detailAttr";
    }
    
    // ========== 前台新增評論 ==========
    
    /**
     * 處理前台新增評論表單提交
     * 路由：POST /attr/addComment
     * 成功：重導向回景點詳情頁
     */
    @PostMapping("/addComment")
    public String addComment(@RequestParam("attrId") Integer attrId,
                            @RequestParam("commentContent") String commentContent,
                            @RequestParam("commentScore") Integer commentScore) {
        
        // 建立 CommentVO 物件
        CommentVO commentVO = new CommentVO();
        
        // 查詢景點並關聯
        AttrVO attrVO = attrService.findById(attrId);
        commentVO.setAttrVO(attrVO);
        
        // 設定評論內容與分數
        commentVO.setCommentContent(commentContent);
        commentVO.setCommentScore(commentScore.byteValue());
        
        // 暫時寫死測試用資料
        commentVO.setCustId(1);  // 測試用顧客 ID
        commentVO.setCommentStatus("1");  // 直接上架以便立刻測試
        commentVO.setCommentTime(java.time.LocalDateTime.now());
        
        // 儲存評論
        commentService.addComment(commentVO);
        
        // 重新導向回詳情頁
        return "redirect:/front/attr/detail/" + attrId;
    }
    
    // ========== Open Data 匯入 ==========
    
    /**
     * 從 Open Data API 匯入景點資料
     * 路由：GET /attr/import-opendata
     * 成功後重導向到列表頁並顯示成功訊息
     */
    @GetMapping("/import-opendata")
    public String importOpenData(RedirectAttributes redirectAttributes) {
        try {
            int count = openDataService.importAttractions();
            redirectAttributes.addFlashAttribute("successMessage", 
                "成功從 Open Data 匯入了 " + count + " 筆景點資料！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "匯入 Open Data 失敗：" + e.getMessage());
        }
        return "redirect:/attr/listAll";
    }
    
    // ========== @ModelAttribute 預載下拉選單（選用） ==========
    
    /**
     * 使用 @ModelAttribute 預載地區清單
     * 此方法會在每個 @RequestMapping 方法執行前自動執行
     * 適合用於所有頁面都需要的共用資料
     */
    // @ModelAttribute("regionList")
    // public List<RegionVO> populateRegions() {
    //     return attrService.getAllRegions();
    // }
    
    /**
     * 使用 @ModelAttribute 預載類別清單
     */
    // @ModelAttribute("categoryList")
    // public List<CategoryVO> populateCategories() {
    //     return attrService.getAllCategories();
    // }
}
