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

	// 購物車推薦熱銷商品 -> 利用productStatus（上架狀態）跟 purchasedQty（累積銷量）
	List<ProdVO> findTop4ByProductStatusTrueOrderByPurchasedQtyDesc();
	// findTop4 ➔ 翻譯成 SQL：LIMIT 4（我只要前 4 筆資料）
	// By ➔ 翻譯成 SQL：WHERE（後面開始接篩選條件）
	// ProductStatusTrue ➔ 翻譯成 SQL：product_status = true（限定要有上架的）
	// OrderByPurchasedQtyDesc ➔ 翻譯成 SQL：ORDER BY purchased_qty DESC（按照累積銷量由高到低排序）

	// 之前學的寫法如下:
	//	@Query("from ProdVO where productStatus = true order by purchasedQty desc")
	//	List<ProdVO> getTopSellingProducts(Pageable pageable);
	
	
	// 隨機抓取指定數量且狀態為上架的商品(放首頁用)
	// nativeQuery = true：直接用原生的資料庫 SQL 語法
	// ORDER BY RAND()： 順序隨機排列
	// LIMIT :limit：限制只拿幾筆
	@Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_STATUS = 1 ORDER BY RAND() LIMIT :limit", nativeQuery = true)
	List<ProdVO> findRandomProducts(@Param("limit") int limit);

}
