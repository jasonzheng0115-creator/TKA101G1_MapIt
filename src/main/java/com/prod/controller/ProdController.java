package com.prod.controller;

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

import com.prod.model.ProdService;
import com.prod.model.ProdVO;
import com.splr.model.SplrService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/product")
public class ProdController {
	
	@Autowired
	private ProdService prodSvc;
	
	@Autowired
	private SplrService splrSvc;
	
	@GetMapping("/select_page")
	public String select_page(ModelMap model) {
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/product/select_page";
	}
		
	@GetMapping("/listAllProduct")
	public String listAllProduct(@RequestParam(value = "page", defaultValue = "1") int page, ModelMap model) {
		
		int pageSize = 10;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<ProdVO> pageData = prodSvc.getAll(pageable);
		
		model.addAttribute("prodData", pageData);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageData.getTotalPages());

		return "back-end/product/listAllProduct";
	}
	
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(@RequestParam("productId") Integer productId, ModelMap model) {
		if (productId == null) {
			model.addAttribute("errorMsg", "請先輸入商品編號再進行查詢!");
			return "back-end/product/select_page";
		}
		
		ProdVO prodVO = prodSvc.getOneProd(productId);
		
		if (prodVO == null) {
			model.addAttribute("errorMsg", "找不到此商品編號，請重新輸入!");
			return "back-end/product/select_page";
		}
		
		model.addAttribute("prodVO", prodVO);
		return "back-end/product/listOneProduct";
	}
	
	@GetMapping("/getByProductStatus")
	public String getByProductStatus(@RequestParam("productStatus") Boolean productStatus, ModelMap model) {
		List<ProdVO> list = prodSvc.getByProductStatus(productStatus);
		model.addAttribute("productStatusList", list);
		return "back-end/product/listAllProduct";
	}
	
	@PostMapping("/listProductByCompositeQuery")
	public String listProdByCompositeQuery(
			@RequestParam(value = "productName", required = false) String productName,
			@RequestParam(value = "supplierId", required = false) Integer supplierId,
			ModelMap model) {
		
		List<ProdVO> list = prodSvc.getByCompositeQuery(productName, supplierId);
		model.addAttribute("prodVOList", list);
		return "back-end/product/listAllProduct";
	}
	
	@GetMapping("/addProduct")
	public String addProd(ModelMap model) {
		model.addAttribute("prodVO", new ProdVO());
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/product/addProduct";
	}
	
	@PostMapping("/insert")
	public String insert(@Valid ProdVO prodVO, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/product/addProduct";
		}
		
		prodSvc.addProd(prodVO);
		return "redirect:/product/select_page";
	}
	
	@PostMapping("/getOne_For_Update")
	public String getOne_For_Update(@RequestParam("productId") Integer productId, ModelMap model) {
		ProdVO prodVO = prodSvc.getOneProd(productId);
		model.addAttribute("prodVO", prodVO);
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/product/update_product_input";
	}
	
	@PostMapping("/update")
	public String update(@Valid ProdVO prodVO, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/product/update_product_input";
		}
		prodSvc.updateProd(prodVO);
		return "redirect:/product/select_page";
	}
	
	@PostMapping("/delete")
	public String delete(@RequestParam("productId") Integer productId, ModelMap model) {
		prodSvc.deleteProd(productId);
		return "redirect:/product/select_page";
	}
	
}
