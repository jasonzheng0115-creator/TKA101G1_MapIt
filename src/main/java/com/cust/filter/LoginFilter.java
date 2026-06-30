package com.cust.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginFilter extends OncePerRequestFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String uri = request.getRequestURI();

		// 絕對必須公開的白名單
		if (uri.equals("/customer/login") ||
				uri.equalsIgnoreCase("/customer/loginCheck") ||
				uri.equals("/customer/register") ||
				uri.startsWith("/customer/emp") || // 排除後台會員管理相關 (empCustomerList, empUpdateCustomer)
				uri.startsWith("/customer/api") || // 排除會員後台 AJAX API
				uri.startsWith("/orders/backend-") // 排除後台訂單管理
		) {
			return true;
		}
		// 黑名單
		if (uri.startsWith("/customer") ||
				uri.startsWith("/order") ||
				uri.startsWith("/trip") ||
				uri.startsWith("/front/favorite/list"))
		{
			return false;
		}
		// 剩下的全部公開
		return true;
	}

	@Override // 做filter的基礎
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// 取得要求要登入的是哪一個路徑
		String uri = request.getServletPath();
		// 取得 session
		HttpSession session = request.getSession();
		// 檢查使用者是否登入過
		Object account = session.getAttribute("loginCust");
		// 如果為null，代表未登入過
		if (account == null) {
			// session存入當前路徑，以便登入後跳轉回此路徑
			session.setAttribute("location", uri);
			// 重導向到登入頁
			response.sendRedirect(request.getContextPath() + "/customer/login");
		} else {
			// 已登入，繼續下一個過濾器或Controller
			filterChain.doFilter(request, response);
		}

	}
}
