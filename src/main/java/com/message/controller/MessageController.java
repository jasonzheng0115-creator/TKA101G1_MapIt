package com.message.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cust.model.CustVO;
import com.message.model.MessageService;
import com.message.model.MessageVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 顯示會員的通知匣頁面
     */
    @GetMapping
    public String showMessages(ModelMap model, HttpSession session) {
        // 1. 確認是否已登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return "redirect:/customer/login"; // 未登入則導向登入頁
        }

        // 2. 獲取該會員的通知與未讀數量
        Integer custId = loginCust.getCustId();
        List<MessageVO> messageList = messageService.getMessagesByCustId(custId);
        long unreadCount = messageService.getUnreadCount(custId);

        // 3. 將資料打包送給前端
        model.addAttribute("userName", loginCust.getCustName());
        model.addAttribute("messageList", messageList);
        model.addAttribute("unreadCount", unreadCount);

        // 回傳對應的 HTML 檔案路徑
        return "front-end/customer/message";
    }

    /**
     * 處理前端傳來的「標記為已讀」請求 (AJAX 呼叫)
     */
    @PostMapping("/read/{msgId}")
    @ResponseBody
    public String markMessageAsRead(@PathVariable Integer msgId, HttpSession session) {
        // 簡單的安全防護：確認有登入才能操作
        if (session.getAttribute("loginCust") == null) {
            return "error: not logged in";
        }

        // 呼叫 Service 更新狀態
        boolean success = messageService.markAsRead(msgId);
        
        if (success) {
            return "success";
        } else {
            return "error: message not found";
        }
    }

    /**
     * 讀取本地端推播圖片
     */
    @GetMapping("/image/{msgId}")
    public org.springframework.http.ResponseEntity<byte[]> getMessageImage(@PathVariable("msgId") Integer msgId) {
        String msgPicPath = messageService.getMessagePicPath(msgId);
        if (msgPicPath == null || msgPicPath.trim().isEmpty()) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }

        try {
            java.io.File imgFile = new java.io.File(msgPicPath);
            if (!imgFile.exists()) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            byte[] imageBytes = java.nio.file.Files.readAllBytes(imgFile.toPath());

            org.springframework.http.MediaType mediaType = org.springframework.http.MediaType.IMAGE_JPEG;
            if (msgPicPath.toLowerCase().endsWith(".png")) {
                mediaType = org.springframework.http.MediaType.IMAGE_PNG;
            } else if (msgPicPath.toLowerCase().endsWith(".gif")) {
                mediaType = org.springframework.http.MediaType.IMAGE_GIF;
            }

            return org.springframework.http.ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}

