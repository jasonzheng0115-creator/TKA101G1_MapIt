package com.prod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cust.model.CustVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/frontend/product")
public class FrontProdController {

	@Autowired
	private ProdService prodSvc;

	// 沒有分頁
	// @GetMapping("/listAll")
	// public String listAllFrontProduct(ModelMap model) {
	// List<ProdVO> list = prodSvc.getAll();
	// model.addAttribute("frontProdList", list);
	// return "front-end/product/listAllProduct";
	// }

	// 有分頁,沒有查詢
	// @GetMapping("/listAll")
	// public String listAll(@RequestParam(value = "page", defaultValue = "1") int
	// page, ModelMap model) {
	//
	// int pageSize = 12;
	// Pageable pageable = PageRequest.of(page - 1, pageSize);
	// Page<ProdVO> pageData = prodSvc.getAll(pageable);
	//
	// model.addAttribute("frontProdList", pageData.getContent());
	// model.addAttribute("currentPage", page);
	// model.addAttribute("totalPages", pageData.getTotalPages());
	// model.addAttribute("prodData", pageData);
	//
	// return "front-end/product/listAllProduct";
	// }

	// 有分頁,有查詢
	@GetMapping("/listAll")
	public String listAll(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", required = false) String keyword,
			HttpSession session,
			ModelMap model) {

		int pageSize = 12;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<ProdVO> pageData;

		// Session 拿出登入的會員，並把真實姓名傳給前端導覽列
		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		if (loginCust != null) {
			model.addAttribute("userName", loginCust.getCustName());
		}

		// 邏輯判斷 是否有關鍵字
		if (keyword != null && !keyword.trim().isEmpty()) {
			// 只搜尋上架中且廠商啟用的商品
			pageData = prodSvc.getFrontActiveProductsByKeyword(keyword.trim(), pageable);
			model.addAttribute("keyword", keyword.trim());
		} else {
			// 沒輸入關鍵字時，只撈上架中且廠商啟用的商品（不撈被停用廠商的商品）
			pageData = prodSvc.getFrontActiveProducts(pageable);
		}

		model.addAttribute("frontProdList", pageData.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageData.getTotalPages());
		model.addAttribute("prodData", pageData);

		return "front-end/product/listAllProduct";
	}

	// 前台大圖點擊後進入詳細資料頁面
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(
			@RequestParam("productId") Integer productId,
			HttpSession session,
			ModelMap model) {

		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		if (loginCust != null) {
			model.addAttribute("userName", loginCust.getCustName());
		}

		ProdVO prodVO = prodSvc.getOneProd(productId);
		model.addAttribute("prodVO", prodVO);
		return "front-end/product/listOneProduct";

	}

	// 給首頁超連結使用
	@GetMapping("/getOne_For_Display")
	public String getOne_For_Display_Get(
			@RequestParam("productId") Integer productId,
			HttpSession session,
			ModelMap model) {

		CustVO loginCust = (CustVO) session.getAttribute("loginCust");
		if (loginCust != null) {
			model.addAttribute("userName", loginCust.getCustName());
		}

		ProdVO prodVO = prodSvc.getOneProd(productId);
		model.addAttribute("prodVO", prodVO);
		return "front-end/product/listOneProduct";
	}

}
