package com.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackendIndexController {

    // 進入後台首頁 (儀表板)
    @GetMapping("/manage")
    public String manageIndex() {
        return "back-end/index"; // templates/back-end/index.html
    }
}
