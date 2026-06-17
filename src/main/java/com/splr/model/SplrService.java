package com.splr.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SplrService {
	
	@Autowired
	private SplrRepository repository;
	
	public void addSplr(SplrVO splrVO) {
		repository.save(splrVO);
	}
	
	public void updateSplr(SplrVO splrVO) {
		repository.save(splrVO);
	}
	
	public void deleteSplr(Integer supplierId) {
		if (repository.existsById(supplierId)) {
			repository.deleteById(supplierId);		
		}
	}

	public SplrVO getOneSplr(Integer supplierId) {
		Optional<SplrVO> optional = repository.findById(supplierId); 
		return optional.orElse(null);  
	}
	
	public List<SplrVO> getAll() {
		return repository.findAll();
	}
	
	public Page<SplrVO> getAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<SplrVO> getByCompositeQuery(String name, String contact) {
		String trimname = (name != null) ? name.trim() : null;
		String trimcontact = (contact != null) ? contact.trim() : null;
		return repository.findByCompositeQuery(trimname, trimcontact);
	}
	
}
 