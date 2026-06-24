package com.ap.controller;

import java.util.List;

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

import com.ap.model.ApService;
import com.ap.model.ApVO;
import com.splr.model.SplrService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ap")
public class ApController {
	
	@Autowired
	private ApService apSvc;
	
	@Autowired
	private SplrService splrSvc;
	
	@GetMapping("/select_page")
	public String select_page(ModelMap model) {
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/ap/select_page";
	}
	
	@GetMapping("/addAp")
	public String addAp(ModelMap model) {
		model.addAttribute("apVO", new ApVO());
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/ap/addAp";
	}
	
	@PostMapping("/insert")
	public String insert(@Valid ApVO apVO, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/ap/addAp";
		}
		apSvc.addAp(apVO);
		return "redirect:/ap/select_page";	
	}
	
	@PostMapping("/getOne_For_Update")
	public String getOne_For_Update(Integer apId, ModelMap model) {
		ApVO apVO = apSvc.getOneAp(apId);
		model.addAttribute("apVO", apVO);
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/ap/update_ap_input";
	}
	
	@PostMapping("/update")
	public String update(@Valid ApVO apVO, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/ap/update_ap_input";
		}
		apSvc.updateAp(apVO);
		return "redirect:/ap/select_page";	
	}
	
	@PostMapping("/delete")
	public String delete(Integer apId, ModelMap model) {
		apSvc.deleteAp(apId);
		return "redirect:/ap/select_page";
	}
	
	@GetMapping("/listAllAp")
	public String listAllAp(@RequestParam(value = "page", defaultValue = "1") int page, ModelMap model) {
		
		int pageSize = 10;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<ApVO> pageData = apSvc.getAll(pageable);
		
		model.addAttribute("apData", pageData);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageData.getTotalPages());
		
		return "back-end/ap/listAllAp";
	}
	
	@PostMapping("/listApByCompositeQuery")
	public String listApByCompositeQuery(
			@RequestParam(value = "supplierId", required = false) Integer supplierId, 
			@RequestParam(value = "supplierName", required = false) String supplierName,
			ModelMap model) {
		
		List<ApVO> list = apSvc.getByCompositeQuery(supplierId, supplierName);
		model.addAttribute("apList", list);
		return "back-end/ap/listAllAp";
	}
	
	@GetMapping("/getByApStatus")
	public String getByApStatus(@RequestParam("apStatus") Boolean apStatus, ModelMap model) {
		List<ApVO> list = apSvc.getByApStatus(apStatus);
		model.addAttribute("apList", list);
		return "back-end/ap/listAllAp";
	}

}
