package com.splr.controller;

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
import com.prod.model.ProdRepository;
import com.splr.model.SplrService;
import com.splr.model.SplrVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/supplier")
public class SplrController {

	@Autowired
	private SplrService splrSvc;

	@Autowired
	private ApService apSvc;

	@Autowired
	private ProdRepository prodRepository;

	@GetMapping("/select_page")
	public String select_Page(ModelMap model) {
		return "back-end/supplier/select_page";
	}

	@GetMapping("/listAllSupplier")
	public String listAllSupplier(
			@RequestParam(value = "page", defaultValue = "1") int page,
			ModelMap model) {

		int pageSize = 10;
		Pageable pageable = PageRequest.of(page - 1, pageSize); // 設定要看第 page 頁，且「一頁只顯示 pageSize 筆」資料
		Page<SplrVO> pageData = splrSvc.getAll(pageable); // 呼叫 Service getAll()

		model.addAttribute("supplierListData", pageData.getContent()); // 傳給前端原本的表格資料（轉成 List）
		model.addAttribute("currentPage", page); // 當前頁碼
		model.addAttribute("totalPages", pageData.getTotalPages()); // 總頁數

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
	
	@PostMapping("/getByName_For_Display")
		public String getByName_For_Display(
				@RequestParam("supplierName") String supplierName,
				ModelMap model) {
			
			if (supplierName == null || supplierName.trim().isEmpty()) {
					model.addAttribute("errorMsgName", "請先輸入廠商名稱再進行查詢!");
					
					return "back-end/supplier/select_page";
			}
			
			List<SplrVO> list = splrSvc.getByCompositeQuery("%" + supplierName.trim() + "%", null);
			
			if (list.isEmpty()) {
				model.addAttribute("errorMsgName", "找不到符合的廠商名稱，請重新輸入！");
				return "back-end/supplier/select_page";
			}
			
			
			model.addAttribute("supplierListData", list);
			model.addAttribute("currentPage", 1);
			model.addAttribute("totalPages", 1);
			
			return "back-end/supplier/listAllSupplier";
		
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

		return "redirect:/supplier/listAllSupplier";
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

		return "redirect:/supplier/listAllSupplier";
	}

	// @PostMapping("/delete")
	// public String delete(Integer supplierId, ModelMap model) {
	//
	// splrSvc.deleteSplr(supplierId);
	//
	// return "redirect:/supplier/listAllSupplier";
	// }

	@PostMapping("/delete")
	public String delete(
			@RequestParam("supplierId") Integer supplierId,
			ModelMap model,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		// 檢查應付帳款（是否還有未付款 false 的帳單）
		boolean hasUnpaid = apSvc.hasUnpaidApBySupplier(supplierId);
		if (hasUnpaid) {
			// 將錯誤訊息塞入 model，並導回列表頁
			model.addAttribute("errorMsg", "❌ 停用失敗：該廠商尚有未結清（未付款）之應付帳款！");

			// 為了讓原本列表頁的分頁與資料不會因為攔截而消失，重新呼叫一次撈取分頁的 method
			return listAllSupplier(page, model);
		}

		// 檢查商品狀態（該廠商名下是否還有商品正處於「上架中 true」的狀態）
		boolean hasActiveProduct = prodRepository.existsBySplrVO_SupplierIdAndProductStatus(supplierId, true);
		if (hasActiveProduct) {
			// 提示管理員必須先將廠商商品全部下架
			model.addAttribute("errorMsg", "❌ 停用失敗：該廠商目前仍有商品上架中，請先至商品管理將其下架！");
			return listAllSupplier(page, model);
		}

		// 執行安全停用邏輯（改狀態而不刪資料）
		SplrVO splrVO = splrSvc.getOneSplr(supplierId);
		if (splrVO != null) {
			splrVO.setSupplierStatus(false); // 將狀態改成已停用 (false)
			splrSvc.updateSplr(splrVO); // 存回資料庫
			model.addAttribute("successMsg", "🟢 廠商已成功停用！");
		}

		// 停用成功後，貼心地重新導向回原本的那一頁分頁
		return "redirect:/supplier/listAllSupplier?page=" + page;
	}

	// 重新啟用廠商
	@PostMapping("/resume")
	public String resume(@RequestParam("supplierId") Integer supplierId,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		SplrVO splrVO = splrSvc.getOneSplr(supplierId);
		if (splrVO != null) {
			splrVO.setSupplierStatus(true); // 將狀態改回 1 (true) 啟用中
			splrSvc.updateSplr(splrVO); // 儲存修改
		}

		// 導回原本的那一頁分頁
		return "redirect:/supplier/listAllSupplier?page=" + page;
	}
}
