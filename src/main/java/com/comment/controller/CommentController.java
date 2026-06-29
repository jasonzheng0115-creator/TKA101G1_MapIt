package com.comment.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.comment.model.CommentService;
import com.comment.model.CommentVO;

import jakarta.validation.Valid;

/**
 * CommentController - 評論控制層
 * 
 * 職責：
 * 1. 接收前端 HTTP 請求
 * 2. 呼叫 CommentService 取得資料
 * 3. 將資料傳遞給 Thymeleaf 模板進行渲染
 * 4. 處理評論狀態切換與評分重算
 * 
 * 路由前綴：/comment
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    
    // ========== 依賴注入 ==========
    private CommentService commentService;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    // ========== 評論列表查詢 ==========
    
    /**
     * 顯示所有評論列表
     * 路由：GET /comment/listAll
     * 視圖：templates/comment/listAll.html
     */
    @GetMapping("/listAll")
    public String listAll(ModelMap model) {
        List<CommentVO> commentList = commentService.getAll();
        model.addAttribute("commentList", commentList);
        return "back-end/comment/listAll";
    }
    
    // ========== 評論新增 ==========
    
    /**
     * 顯示新增評論表單
     * 路由：GET /comment/add
     * 視圖：templates/comment/add.html
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        // 建立空白的 CommentVO 物件供表單綁定
        CommentVO commentVO = new CommentVO();
        model.addAttribute("commentVO", commentVO);
        return "back-end/comment/add";
    }
    
    /**
     * 處理新增評論表單提交
     * 路由：POST /comment/add
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("commentVO") CommentVO commentVO,
                      BindingResult result,
                      ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/comment/add";
        }
        
        // 儲存評論資料
        commentService.addComment(commentVO);
        
        // 重導向到列表頁（使用 redirect 避免重複提交）
        return "redirect:/comment/listAll";
    }
    
    // ========== 評論編輯 ==========
    
    /**
     * 顯示編輯評論表單
     * 路由：GET /comment/update?commentId=1
     * 視圖：templates/comment/update.html
     */
    @GetMapping("/update")
    public String updateForm(@RequestParam("commentId") Integer commentId, ModelMap model) {
        // 根據 ID 查詢評論資料
        CommentVO commentVO = commentService.getOneComment(commentId);
        
        // 若評論不存在，重導向到列表頁
        if (commentVO == null) {
            return "redirect:/comment/listAll";
        }
        
        model.addAttribute("commentVO", commentVO);
        return "back-end/comment/update";
    }
    
    /**
     * 處理編輯評論表單提交
     * 路由：POST /comment/update
     * 成功：重導向到列表頁
     * 失敗：返回表單頁並顯示錯誤訊息
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("commentVO") CommentVO commentVO,
                         BindingResult result,
                         ModelMap model) {
        
        // 若驗證失敗，返回表單頁
        if (result.hasErrors()) {
            return "back-end/comment/update";
        }
        
        // 更新評論資料
        commentService.updateComment(commentVO);
        
        // 重導向到列表頁
        return "redirect:/comment/listAll";
    }
    
    // ========== 評論刪除 ==========
    
    /**
     * 刪除評論
     * 路由：GET /comment/delete?commentId=1
     * 刪除後重導向到列表頁
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("commentId") Integer commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/comment/listAll";
    }
    
    // ========== 評論狀態切換 ==========
    
    /**
     * 切換評論狀態（上架/下架）
     * 路由：GET /comment/updateStatus?commentId=1&status=1
     * 
     * 狀態說明：
     * - "0": 待審核
     * - "1": 已上架（顯示在前台，計入評分）
     * - "2": 已下架（不顯示在前台，不計入評分）
     * 
     * 狀態切換後會自動重新計算該景點的平均評分
     */
    @GetMapping("/updateStatus")
    public String updateStatus(@RequestParam("commentId") Integer commentId,
                               @RequestParam("status") String status) {
        
        // 呼叫 Service 層的狀態切換方法（內含評分重算邏輯）
        commentService.updateCommentStatus(commentId, status);
        
        // 重導向到列表頁
        return "redirect:/comment/listAll";
    }
}
