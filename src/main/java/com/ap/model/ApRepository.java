package com.ap.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApRepository extends JpaRepository<ApVO, Integer> {
	
	Page<ApVO> findAll(Pageable pageable);
	
	@Query("from ApVO where "
			+ "(:supplierId is null or splrVO.supplierId = :supplierId) and "
			+ "(:supplierName is null or length(trim(:supplierName)) = 0 or splrVO.supplierName like %:supplierName%) ")
	List<ApVO> findByCompositeQuery(
			@Param("supplierId") Integer supplierId,
			@Param("supplierName") String supplierName);
	
	List<ApVO> findByApStatus(Boolean apStatus);
	
	// 檢查是否存在屬於某廠商且付款狀態為特定值(例如 false)的帳款
	boolean existsBySplrVO_SupplierIdAndApStatus(Integer supplierId, Boolean apStatus);
}
