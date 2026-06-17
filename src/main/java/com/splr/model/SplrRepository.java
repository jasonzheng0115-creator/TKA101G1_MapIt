package com.splr.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SplrRepository extends JpaRepository<SplrVO, Integer> {
						                             
	Page<SplrVO> findAll(Pageable pageable);
	
	@Query("from SplrVO where "
			+ "(?1 is null or supplierName like ?1) and "
			+ "(?2 is null or supplierContact like ?2) "
			+ "order by supplierId")
	List<SplrVO> findByCompositeQuery(String name, String contact);
}
