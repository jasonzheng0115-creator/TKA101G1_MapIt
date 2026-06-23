package com.prod.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdRepository extends JpaRepository<ProdVO, Integer> {
	
	Page<ProdVO> findAll(Pageable pageable);
	
	List<ProdVO> findByProductStatus(Boolean productStatus);
	
	@Query("from ProdVO where "
			+ "(:name is null or length(trim(:name)) = 0 or productName like %:name%) and "
			+ "(:supplierId is null or splrVO.supplierId = :supplierId) ")
	List<ProdVO> findByCompositeQuery(
			@Param("name") String productName, 
			@Param("supplierId") Integer supplierId);
	
	Page<ProdVO> findByProductNameContaining(String productName, Pageable pageable);
}
