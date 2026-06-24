package com.splr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.splr.model.SplrService;
import com.splr.model.SplrVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/supplier")
public class SplrController {
	
	@Autowired
	private SplrService splrSvc;
	
	@GetMapping("/select_page")
	public String select_Page(ModelMap model) {
		return "back-end/supplier/select_page";
	}
	
	@GetMapping("/listAllSupplier")
	public String listAllSupplier(
			@RequestParam(value = "page", defaultValue = "1") int page,
			ModelMap model) {
		
		int pageSize = 10;
		Pageable pageable = PageRequest.of(page -1, pageSize); // 設定要看第 page 頁，且「一頁只顯示 pageSize 筆」資料
		Page<SplrVO> pageData = splrSvc.getAll(pageable);      // 呼叫 Service getAll()
		
		model.addAttribute("supplierListData", pageData.getContent()); // 傳給前端原本的表格資料（轉成 List）
		model.addAttribute("currentPage", page);                   // 當前頁碼
		model.addAttribute("totalPages", pageData.getTotalPages());   // 總頁數
		
		return "back-end/supplier/listAllSupplier";
	}
	
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(Integer supplierId, ModelMap model) {
		
		if (supplierId == null) {
			model.addAttribute("errorMsg", "請先輸入廠商編號再進行查詢!");
			return "back-end/supplier/select_page";
		}
		
		SplrVO splrVO = splrSvc.getOneSplr(supplierId);
		
		if (splrVO == null) {
			model.addAttribute("errorMsg", "找不到此廠商編號，請重新輸入！");
			return "back-end/supplier/select_page";
		}
		
		model.addAttribute("splrVO", splrVO);
		return "back-end/supplier/listOneSupplier";
	}
	
	@GetMapping("/addSupplier")
	public String addSplr(ModelMap model) {
		model.addAttribute("splrVO", new SplrVO());
		return "back-end/supplier/addSupplier";
	}
	
	@PostMapping("/insert")
	public String insert(@Valid SplrVO splrVO, BindingResult result, ModelMap model) {
		
		if (result.hasErrors()) {
			return "back-end/supplier/addSupplier";
		}
		
			splrSvc.addSplr(splrVO);
			
			return "redirect:/supplier/select_page";
	}
	
	
	@PostMapping("/getOne_For_Update")
	public String getOne_For_Update(Integer supplierId, ModelMap model) {
		
		SplrVO splrVO = splrSvc.getOneSplr(supplierId);
		model.addAttribute("splrVO", splrVO);
		return "back-end/supplier/update_supplier_input";
	}
	
	@PostMapping("/update")
	public String update(@Valid SplrVO splrVO, BindingResult result, ModelMap model) {
		
		if (result.hasErrors()) {
			return "back-end/supplier/update_supplier_input";
		}
		
		splrSvc.updateSplr(splrVO);
		
		return "redirect:/supplier/select_page";
	}
	
	@PostMapping("/delete")
	public String delete(Integer supplierId, ModelMap model) {
		
		splrSvc.deleteSplr(supplierId);
		
		return "redirect:/supplier/select_page";	
	}
}
