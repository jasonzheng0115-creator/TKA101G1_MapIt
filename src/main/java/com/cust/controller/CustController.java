package com.cust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cust.model.CustService;
import com.cust.model.CustVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cust") //設定門牌
public class CustController {
	
	@Autowired //自動new CustService()
	CustService custService;
	
	@GetMapping("/login") //登入功能，指向前端的登入html
	public String loginPage(){
		return "/login";
	}	

	@PostMapping("/loginCheck") //拿前端給的資料
	public String loginCheck(
		//@Request...貼標籤，指定要前端這兩筆資料(需前端對應)
		//HttpSession用來記憶已登入的狀態，到其他頁面不需重新登入
		//ModelMap用來把資料或錯誤訊息，打包送回給前端	
		@RequestParam("cust_account") String cust_account,
		@RequestParam("cust_password") String cust_password,
		HttpSession session,
		ModelMap model) {
	
			//檢查使用者帳號或密碼是否空白，空白就返回登入畫面
			if(cust_account == null || cust_account.trim().isEmpty() || cust_password == null || cust_password.trim().isEmpty()){
			model.addAttribute("errorMsg","帳號或密碼請勿空白");
			return "/login";
			}
			
			//檢查使用者輸入的帳號和密碼都正確
			CustVO custVO = custService.login(cust_account, cust_password);
			//輸入錯誤就返回登入畫面
			if(custVO == null) {
				model.addAttribute("errorMsg","帳號或密碼錯誤");
				return "/login";
			}
			//成功登入後記憶登入狀態
			session.setAttribute("custVO",custVO);
			//查詢正確就轉向登入成功的畫面
			model.addAttribute("custVO",custVO);
			return "back-end/cust/loginSuccess";
		}
	
	@GetMapping("/logout") //登出功能，指向前端的登入html
	public String logout(HttpSession session) {
		session.invalidate(); 
		return "redirect:/cust/login";	
	}
	
	
	@GetMapping("/register") //註冊功能，指向前端的登入html
	public String registerPage(ModelMap model) {
		// 先在model裡塞一個空的CustVO，當前端HTML載入時，Thymeleaf會跟這個空的物件進行綁定
		model.addAttribute("custVO", new CustVO());
		return "/register";
	}
	
	@PostMapping("/register")
	public String register(
		@Valid @ModelAttribute("custVO") CustVO custVO,		
		BindingResult result,ModelMap model){	
		
		//如果格式驗證有誤(和BindingResult一起使用)
		if(result.hasErrors()) {
			return "/register";
		}
		
		//註冊成功，重新導回登入頁面
		try {
		custService.register(custVO);
		return "redirect:/cust/login";
		//如果Service檢查報錯，把訊息回傳
		}catch(RuntimeException e) {
			model.addAttribute("errorMsg",e.getMessage());
			return "/register";
		}
		
		}
}
