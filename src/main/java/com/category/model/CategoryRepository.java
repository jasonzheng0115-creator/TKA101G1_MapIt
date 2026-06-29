package com.category.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CategoryRepository - 類別資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 */
public interface CategoryRepository extends JpaRepository<CategoryVO, Integer> {
    
}
