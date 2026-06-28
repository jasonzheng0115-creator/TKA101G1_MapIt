package com.prod.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.multipart.MultipartFile;

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

	// 指定電腦裡的實體硬碟資料夾路徑
	private static final String UPLOAD_DIR = "C:/upload/products/";

	@GetMapping("/select_page")
	public String select_page(ModelMap model) {
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/product/select_page";
	}

	@GetMapping("/listAllProduct")
	public String listAllProduct(
			@RequestParam(value = "page", defaultValue = "1") int page,
			ModelMap model) {

		int pageSize = 10;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<ProdVO> pageData = prodSvc.getAll(pageable);

		model.addAttribute("prodData", pageData.getContent());
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

	// 新增商品（處理圖片實體上傳 ＋ 4合1 欄位 JSON 打包）
	@PostMapping("/insert")
	public String insert(
			@Valid ProdVO prodVO,
			BindingResult result,
			@RequestParam("productImgFile") MultipartFile file, // 接收圖片檔
			ModelMap model) {

		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/product/addProduct";
		}

		// 實體硬碟圖片上傳
		if (file != null && !file.isEmpty()) {

			try {

				File dir = new File(UPLOAD_DIR);
				if (!dir.exists())
					dir.mkdirs(); // C:/upload/products/ 不存在就自動創立

				// 用 UUID 防止檔名重複衝突
				String originalFilename = file.getOriginalFilename();
				String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
				String saveFilename = UUID.randomUUID().toString() + ext;

				// 將實體檔案寫入硬碟
				File destFile = new File(dir, saveFilename);
				file.transferTo(destFile);

				prodVO.setProductImg(saveFilename);

			} catch (IOException e) {
				model.addAttribute("errorMsg", "圖片上傳失敗：" + e.getMessage());
				model.addAttribute("supplierList", splrSvc.getAll());
				return "back-end/product/addProduct";
			}
		}

		prodSvc.addProd(prodVO);
		return "redirect:/product/listAllProduct";
	}

	@PostMapping("/getOne_For_Update")
	public String getOne_For_Update(@RequestParam("productId") Integer productId, ModelMap model) {
		ProdVO prodVO = prodSvc.getOneProd(productId);
		model.addAttribute("prodVO", prodVO);
		model.addAttribute("supplierList", splrSvc.getAll());
		return "back-end/product/update_product_input";
	}

	// 修改商品（同步支援圖片實體覆蓋 ＋ JSON 更新）
	@PostMapping("/update")
	public String update(
			@Valid ProdVO prodVO,
			BindingResult result,
			@RequestParam(value = "productImgFile", required = false) MultipartFile file, // 圖片檔案
			ModelMap model) {

		if (result.hasErrors()) {
			model.addAttribute("supplierList", splrSvc.getAll());
			return "back-end/product/update_product_input";
		}

		// 圖片覆蓋
		if (file != null && !file.isEmpty()) {
			try {
				File dir = new File(UPLOAD_DIR);
				if (!dir.exists())
					dir.mkdirs();

				String originalFilename = file.getOriginalFilename();
				String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
				String saveFilename = UUID.randomUUID().toString() + ext;

				File destFile = new File(dir, saveFilename);
				file.transferTo(destFile);

				prodVO.setProductImg(saveFilename);

			} catch (IOException e) {
				model.addAttribute("errorMsg", "圖片更新失敗：" + e.getMessage());
				model.addAttribute("supplierList", splrSvc.getAll());
				return "back-end/product/update_product_input";
			}
		} else {

			// 如果這次沒上傳新圖，去資料庫把原本的舊檔名撈回來維持原樣，防止被空值洗掉
			ProdVO oldProd = prodSvc.getOneProd(prodVO.getProductId());
			if (oldProd != null) {
				prodVO.setProductImg(oldProd.getProductImg());
			}
		}

		prodSvc.updateProd(prodVO);
		return "redirect:/product/listAllProduct";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("productId") Integer productId, ModelMap model) {
		prodSvc.deleteProd(productId);
		return "redirect:/product/listAllProduct";
	}

}
