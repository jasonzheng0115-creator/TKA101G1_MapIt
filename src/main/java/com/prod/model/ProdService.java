package com.prod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ProdService {
	
	@Autowired
	private ProdRepository repository;
	
	public void addProd(ProdVO prodVO) {
		repository.save(prodVO);
	}
	
	public void updateProd(ProdVO prodVO) {
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
	
}
