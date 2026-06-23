package com.region.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * RegionVO - 地區實體類別
 * 對應資料表：REGION
 * 主鍵類型：手動設定 (Manual PK)
 */
@Entity
@Table(name = "REGION")
public class RegionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer regionId;
    private String regionName;
    
    // ========== 建構子 ==========
    public RegionVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @Column(name = "REGION_ID")
    public Integer getRegionId() {
        return regionId;
    }
    
    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }
    
    @NotEmpty(message = "地區名稱: 請勿空白")
    @Column(name = "REGION_NAME", length = 20)
    public String getRegionName() {
        return regionName;
    }
    
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "RegionVO [regionId=" + regionId + ", regionName=" + regionName + "]";
    }
}
