package com.splr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String listAllSupplier(ModelMap model) {
		List<SplrVO> list = splrSvc.getAll();
		model.addAttribute("supplierListData", list);
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
