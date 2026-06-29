package com.cust.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cust.model.CustService;
import com.cust.model.CustVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer") // 設定門牌
public class CustController {

	@Autowired // 自動new CustService()
	CustService custService;

	@GetMapping("/login") // 登入功能，指向前端的登入html
	public String loginPage() {
		return "front-end/customer/login";
	}

	@PostMapping("/loginCheck") // 拿前端給的資料
	public String loginCheck(
			// @Request...貼標籤，指定要前端這兩筆資料(需前端對應)
			// HttpSession用來記憶已登入的狀態，到其他頁面不需重新登入
			// ModelMap用來把資料或錯誤訊息，打包送回給前端
			@RequestParam("custAccount") String custAccount,
			@RequestParam("custPassword") String custPassword,
			HttpSession session,
			ModelMap model) {
		// 檢查使用者帳號或密碼是否空白，空白就返回登入畫面
		if (custAccount == null || custAccount.trim().isEmpty() || custPassword == null
				|| custPassword.trim().isEmpty()) {
			model.addAttribute("errorMsg", "帳號或密碼請勿空白");
			return "front-end/customer/login";
		}
		// 檢查使用者輸入的帳號和密碼都正確
		CustVO custVO = custService.login(custAccount, custPassword);
		// 輸入錯誤就返回登入畫面
		if (custVO == null) {
			model.addAttribute("errorMsg", "帳號或密碼錯誤");
			return "front-end/customer/login";
		}
		// 成功登入後，存入session長期記憶，保持登入狀態
		session.setAttribute("loginCust", custVO);
		// 查詢正確就轉向登入成功的畫面
		model.addAttribute("loginCust", custVO);

		// 檢查是否有被 LoginFilter 記下的原請求路徑 (location)
		String location = (String) session.getAttribute("location");
		if (location != null) {
			session.removeAttribute("location"); // 移出 session 以防後續登入重複重導向
			return "redirect:" + location;
		} else {
			return "redirect:/";
		}

	}

	@GetMapping("/loginSuccess") // 過濾器使用，讓使用者無法條過登入功能，直接透過網址登入
	public String LoginSuccess(HttpSession session, ModelMap model) {
		if (session.getAttribute("loginCust") == null) {
			return "front-end/customer/login";
		}
		model.addAttribute("loginCust", session.getAttribute("loginCust"));
		return "front-end/customer/loginSuccess";
	}

	@GetMapping("/logout") // 登出功能，指向前端的登入html
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/register") // 註冊功能，指向前端的登入html
	public String registerPage(ModelMap model) {
		// 先在model裡塞一個空的CustVO，當前端HTML載入時，Thymeleaf會跟這個空的物件進行綁定
		model.addAttribute("newCust", new CustVO());
		return "front-end/customer/register";
	}

	@PostMapping("/register")
	public String register(
			@Valid @ModelAttribute("newCust") CustVO custVO,
			BindingResult result, ModelMap model) {
		// 如果格式驗證有誤(和BindingResult一起使用)
		if (result.hasErrors()) {
			return "front-end/customer/register";
		}
		// 註冊成功，重新導回登入頁面
		try {
			custService.register(custVO);
			return "redirect:/customer/login";
			// 如果Service檢查報錯，把訊息回傳
		} catch (RuntimeException e) {
			model.addAttribute("errorMsg", e.getMessage());
			return "front-end/customer/register";
		}
	}

	@GetMapping("/updateProfile") // 修改個人資料功能，指向前端的登入html
	public String updateProfile(HttpSession session, ModelMap model) {
		// 取得先前會員的就個人資料
		CustVO oldData = (CustVO) session.getAttribute("loginCust");
		model.addAttribute("loginCust", oldData);
		return "front-end/customer/updateProfile";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(
			// 驗證前端傳來的新個人資料
			@Valid @ModelAttribute("loginCust") CustVO custVO, BindingResult result, HttpSession session,
			// 拿取前端的檔案
			@RequestParam("cust_img_file") MultipartFile file,
			// 防呆required = false 代表沒傳來也沒關係
			@RequestParam(value = "remove_img_flag", required = false) String removeImgFlag, ModelMap model) {
		// 如果錯誤退回給前端，提供錯誤提示資訊
		if (result.hasErrors()) {
			System.out.println("發生錯誤的欄位與原因如下：" + result.getAllErrors());
			;
			return "front-end/customer/updateProfile";
		}
		// 正確話就拿出舊資料，為了要確認是要改哪一個會員id的個人資料
		CustVO oldData = (CustVO) session.getAttribute("loginCust");
		custVO.setCustId(oldData.getCustId());
		// 如果使用者選擇移除頭像
		if ("true".equals(removeImgFlag)) {
			custVO.setCustImg(null);
		} else if (file.isEmpty()) {
			// 使用者沒有選新照片，沿用原本舊照片的路徑
			custVO.setCustImg(oldData.getCustImg());
		} else {
			// 使用者選新照片
			try {
				// 取得專案的絕對路徑
				String projectPath = System.getProperty("user.dir");
				// 合成存檔的目標資料夾
				String uploadDirectory = projectPath + "/uploads/avatars";
				// 建立代表資料夾的File物件
				File folder = new File(uploadDirectory);

				if (!folder.exists()) {
					folder.mkdirs();
				}
				// 取得原本照片的檔名
				String originalFilename = file.getOriginalFilename();
				// 取出副檔名
				String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
				// 合成唯一的黨名
				String newFileName = "avatar_" + custVO.getCustId() + fileExtension;
				// 建立目的地的file物件，不能用+號，因為照片命名回黏在一起，不會存進指定的資料夾裡，也不能用"/"拼，因為不同作業系統會用\/，符號不同容易錯誤
				File saveFile = new File(uploadDirectory, newFileName);
				// 複製檔案存入硬碟
				file.transferTo(saveFile);
				// 將虛擬相對路徑存入資料庫
				custVO.setCustImg("/uploads/avatars/" + newFileName);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMsg", "照片上傳失敗:" + e.getMessage());
				return "front-end/customer/updateProfile";
			}
		}
		// 將更新後的資料寫回資料庫
		custService.updateProfile(custVO);
		// 更新新的資料給
		session.setAttribute("loginCust", custVO);
		return "redirect:/customer/loginSuccess";
	}

	@GetMapping("/empCustomerList") // 後台查詢所有會員資料功能，進入後台頁面，預設顯示所有會員
	public String empCustomerList(ModelMap model) {
		// 預設一個空的查找條件，讓複合查詢撈出所有已存在會員的資料
		Map<String, String[]> emptyMap = new HashMap<>();
		List<CustVO> list = custService.getAll(emptyMap);

		model.addAttribute("custList", list);
		return "back-end/customer/empCustomerList";
	}

	@PostMapping("/empCustomerList")
	public String listAllEmp(HttpServletRequest req, ModelMap model) {
		// 把前端給的所有條件，用map裝起來
		Map<String, String[]> map = req.getParameterMap();
		// 將map丟給service，並篩選出符合條件的會員資料
		List<CustVO> list = custService.getAll(map);
		// 防呆，如果清單是空的，沒有任何符合條件的會員資料
		if (list.isEmpty()) {
			model.addAttribute("erroeMsg", "查無符合條件的會員資料");
		}
		model.addAttribute("custList", list);
		return "back-end/customer/empCustomerList";

	}

	// 後台修改單筆會員資料功能(用前後端分離的方式寫)
	@GetMapping("/empUpdateCustomer") // 只是把空白的網頁丟給瀏覽器
	public String empUpdateCustomer() {
		return "back-end/customer/empUpdateCustomer";
	}

	// 載入好網頁後，將資料庫會員舊資料，做成JSON給前端API
	@GetMapping("/api/getOne")
	@ResponseBody // 這個標籤是指只給JSON資料，不給HTML網頁
	public CustVO getOneCustomerJson(
			@RequestParam("custId") Integer custId) {
		return custService.getOneCust(custId);
	}

	@PostMapping("/api/update") // 把要前端要修改JSON格式的新資料接進來
	@ResponseBody
	public String updateCustomerJson(
			@Valid @RequestBody CustVO custVO,
			BindingResult result) {
		if (result.hasErrors()) {
			String errorMsg = result.getFieldError().getDefaultMessage();
			return errorMsg;
		}
		custService.updateProfile(custVO);
		return "success";
	}

	@GetMapping("/selectPage")
	public String selectPage() {
		return null;
	}

	@PostMapping("/selectPage")
	public String selectPage(
			ModelMap model) {

		return null;
	}

	@GetMapping("/ticket") // 票券匣功能
	public String ticket() {
		return "front-end/ticket/ticket";
	}

	@GetMapping("/message") // 通知功能
	public String message() {
		return "front-end/message/message";
	}

	@GetMapping("/orderHistory") // 歷史訂單功能
	public String orderHistory() {
		return "front-end/customer/orderHistory";
	}

}
