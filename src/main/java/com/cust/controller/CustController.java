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
		return "/back-end/customer/login";
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
			return "/back-end/customer/login";
			}
			//檢查使用者輸入的帳號和密碼都正確
			CustVO custVO = custService.login(cust_account, cust_password);
			//輸入錯誤就返回登入畫面
			if(custVO == null) {
				model.addAttribute("errorMsg","帳號或密碼錯誤");
				return "/back-end/customer/login";
			}
			//成功登入後記憶登入狀態
			session.setAttribute("custVO",custVO);
			//查詢正確就轉向登入成功的畫面
			model.addAttribute("custVO",custVO);
			return "/back-end/customer/loginSuccess";
		}
	
	@GetMapping("/loginSuccess")
	public String LoginSuccess(HttpSession session, ModelMap model) {
		if(session.getAttribute("custVO") == null) {
			return "/back-end/customer/login";
		}
		model.addAttribute("custVO",session.getAttribute("custVO"));
		return "/back-end/customer/loginSuccess";
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
		return "/back-end/customer/register";
	}
	@PostMapping("/register")
	public String register(
		@Valid @ModelAttribute("custVO") CustVO custVO,		
		BindingResult result,ModelMap model){		
		//如果格式驗證有誤(和BindingResult一起使用)
		if(result.hasErrors()) {
			return "/back-end/customer/register";
		}
		//註冊成功，重新導回登入頁面
		try {
		custService.register(custVO);
		return "redirect:/cust/login";
		//如果Service檢查報錯，把訊息回傳
		}catch(RuntimeException e) {
			model.addAttribute("errorMsg",e.getMessage());
			return "/back-end/customer/register";
		}
		}
	
	@GetMapping("/updateProfile")
	public String updateProfile(HttpSession session,ModelMap model) {
	//取得先前會員的就個人資料
	CustVO oldData = (CustVO)session.getAttribute("custVO");
	model.addAttribute("custVO", oldData);
	return "/back-end/customer/updateProfile";
	}
	
	@PostMapping("/updateProfile")
	public String updateProfile(
			//驗證前端傳來的新個人資料
			@Valid @ModelAttribute("custVO") CustVO custVO,
			BindingResult result,HttpSession session,ModelMap model){
			//如果錯誤退回給前端，提供錯誤提示資訊
			if(result.hasErrors()) {
				System.out.println("發生錯誤的欄位與原因如下：" + result.getAllErrors());;
				return "/back-end/customer/updateProfile";
			}
			//正確話就拿出舊資料，為了要確認是要改哪一個會員id的個人資料
			CustVO oldData = (CustVO)session.getAttribute("custVO");
			custVO.setCust_id(oldData.getCust_id());
			//把加上id的資料更新資料庫
			custService.updateProfile(custVO);
			//更新新的資料給
			session.setAttribute("custVO",custVO);
			return "redirect:/cust/loginSuccess";
			
		
	}
}
