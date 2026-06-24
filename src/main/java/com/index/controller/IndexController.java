package com.index.controller;

import com.cust.model.CustVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String showIndexPage(HttpSession session, Model model) {
        // 1. 嘗試從伺服器的「記憶保險箱 (Session)」中，拿出名為 "loginCust" 的資料
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        // 2. 判斷保險箱裡有沒有這個人（是不是已經登入了？）
        if (loginCust != null) {
            // 3. 如果有登入，就把他的名字拿出來，裝進準備送到前端的包裹 (Model) 裡面
            // 並在包裹上貼個標籤叫做 "userName"
            model.addAttribute("userName", loginCust.getCustName());
        }

        // 4. 回傳字串 "index"，這是在告訴 Spring Boot：「請幫我把包裹送到 index.html 這個網頁去」
        return "front-end/index";
    }
}