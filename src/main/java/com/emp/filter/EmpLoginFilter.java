package com.emp.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component // ⚙️ 加上此註解，Spring Boot 就會自動將其註冊並啟動為全域過濾器
public class EmpLoginFilter extends OncePerRequestFilter { // ⚙️ 繼承 Spring 專用的 OncePerRequestFilter

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 取得瀏覽器發送請求的 URL 路徑
        String uri = request.getRequestURI();

        // 1. 絕對必須放行的白名單：登入頁、登入驗證 Action、以及靜態資源 (不進行過濾)
        if (uri.equals("/back-end/emp/login") ||
            uri.equals("/back-end/emp/loginCheck") ||
            uri.equals("/back-end/emp/logout") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/images/")) {
            return true; // 🚀 回傳 true 代表「跳過此過濾器，直接放行」
        }

        // 2. 必須進行登入檢查的黑名單：後台所有重要管理功能
        if (uri.equals("/manage") ||
            uri.startsWith("/back-end/emp") ||
            uri.startsWith("/back-end/dept") ||
            uri.startsWith("/product") ||
            uri.startsWith("/supplier") ||
            uri.startsWith("/ap") ||
            uri.startsWith("/orders") ||
            uri.equals("/customer/empCustomerList")) {
            return false; // 🔒 回傳 false 代表「必須進行過濾（檢查 Session）」
        }

        // 3. 其餘不在上述清單內的路徑（例如前台公開網頁），默認放行
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 取得 Session 
        HttpSession session = request.getSession();
        
        // 2. 檢查管理員是否登入過
        Object loginEmp = session.getAttribute("loginEmp");

        if (loginEmp == null) {
            // 🔒 若未登入，強制導向至後台登入頁
            response.sendRedirect(request.getContextPath() + "/back-end/emp/login");
        } else {
            // 🔓 若已登入，繼續執行下一個過濾器或 Controller
            filterChain.doFilter(request, response);
        }
    }
}
