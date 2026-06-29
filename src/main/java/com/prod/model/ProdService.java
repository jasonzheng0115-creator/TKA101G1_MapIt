package com.prod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ticket.model.TicketService;


@Service
public class ProdService {
	
	@Autowired
	private ProdRepository repository;
	// ========= 導入票券ticketService =========
	@Autowired 
	private TicketService ticketService;
	// ========================================
	
	public void addProd(ProdVO prodVO) {
		repository.save(prodVO);
		// ======= 攔截存檔動作，自動生成商品對應的未售出票券 =======
		ticketService.addTickets(prodVO, prodVO.getProductQty());
		// ===================================================
	}
	
	public void updateProd(ProdVO prodVO) {
		// ========= 特定商品更新庫存，自動增加或作廢票券 =========
		//findById回傳的是Optional，所以加入orElse自動拆箱，如果是空值就給null
		ProdVO oldProd = repository.findById(prodVO.getProductId()).orElse(null);
		ticketService.addOrCancelTicket(oldProd, prodVO);
		// ==================================================
		repository.save(prodVO);
	}
	
	public void deleteProd(Integer productId) {
		if (repository.existsById(productId)) {
			repository.deleteById(productId);
		}	

	}
	
	public ProdVO getOneProd(Integer productId) {
		Optional<ProdVO> optional = repository.findById(productId);
		return optional.orElse(null);
	}
	
	public List<ProdVO> getAll() {
		return repository.findAll();
	}
	
	public Page<ProdVO> getAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<ProdVO> getByProductStatus(Boolean productStatus) {
		return repository.findByProductStatus(productStatus);
	}
	
	public List<ProdVO> getByCompositeQuery(String productName, Integer supplierId) {
		String trimname = (productName != null && !productName.trim().isEmpty()) ? productName.trim() : null;
		return repository.findByCompositeQuery(trimname, supplierId);
	}
	
	public Page<ProdVO> gerProductByKeyword(String keyword, Pageable pageable) {
		return repository.findByProductNameContaining(keyword, pageable);
	}
	
	// 取得隨機上架商品
	public List<ProdVO> getRandomProducts(int limit) {
		return repository.findRandomProducts(limit);
	}
	
}
