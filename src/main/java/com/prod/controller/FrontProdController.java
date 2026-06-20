package com.prod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prod.model.ProdService;
import com.prod.model.ProdVO;

@Controller
@RequestMapping("/frontend/product")
public class FrontProdController {
	
	@Autowired
	private ProdService prodSvc;
	
	@GetMapping("/listAll")
	public String listAllFrontProduct(ModelMap model) {
		List<ProdVO> list = prodSvc.getAll();
		model.addAttribute("frontProdList", list);
		return "front-end/product/listAllProduct";
	}
}
