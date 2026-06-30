package com.ap.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ApService {
	
	@Autowired
	private ApRepository repository;
	
	public void addAp(ApVO apVO) {
		repository.save(apVO);
	}
	
	public void updateAp(ApVO apVO) {
		repository.save(apVO);
	}
	
	public void deleteAp(Integer apId) {
		if (repository.existsById(apId)) {
			repository.deleteById(apId);
		}
	}
	
	public List<ApVO> getAll() {
		return repository.findAll();
	}
	
	public Page<ApVO> getAll(Pageable pageable){
		return repository.findAll(pageable);
	}
	
	public ApVO getOneAp(Integer apId) {
		Optional<ApVO> optional = repository.findById(apId);
		return optional.orElse(null);
	}
	
	public List<ApVO> getByCompositeQuery(Integer supplierId, String supplierName) {
		String trimname = (supplierName != null && !supplierName.trim().isEmpty()) ? supplierName.trim() : null;
		return repository.findByCompositeQuery(supplierId, trimname);
	}
	
	public List<ApVO> getByApStatus(Boolean apStatus) {
		return repository.findByApStatus(apStatus);
	}
	
	// 給controller檢查是否有未付款帳單
		public boolean hasUnpaidApBySupplier(Integer supplierId) {
			// 傳入廠商編號，並且狀態查 false（代表未付款）
			return repository.existsBySplrVO_SupplierIdAndApStatus(supplierId, false);
		}

}
