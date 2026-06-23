package com.category.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * CategoryVO - 類別實體類別
 * 對應資料表：CATEGORY
 * 主鍵類型：自動遞增 (Auto Increment)
 */
@Entity
@Table(name = "CATEGORY")
public class CategoryVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer categoryId;
    private String categoryName;
    
    // ========== 建構子 ==========
    public CategoryVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    @NotEmpty(message = "類別名稱: 請勿空白")
    @Column(name = "CATEGORY_NAME", length = 50, nullable = false)
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "CategoryVO [categoryId=" + categoryId + ", categoryName=" + categoryName + "]";
    }
}
