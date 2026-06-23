package com.attr.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import com.category.model.CategoryVO;
import com.region.model.RegionVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * AttrVO - 景點實體類別
 * 對應資料表：ATTRACTION
 * 主鍵類型：自動遞增 (Auto Increment)
 */
@Entity
@Table(name = "ATTRACTION")
public class AttrVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer attrId;
    private String attrName;
    private RegionVO regionVO;
    private CategoryVO categoryVO;
    private String attrAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private String attrTel;
    private String openTime;
    private String isOpen;
    private Integer attrVotes;
    private Integer attrStars;
    private BigDecimal avgStars;
    private Set<AttrImageVO> images = new LinkedHashSet<>();
    
    // ========== 建構子 ==========
    public AttrVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTR_ID")
    public Integer getAttrId() {
        return attrId;
    }
    
    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }
    
    @NotEmpty(message = "景點名稱: 請勿空白")
    @Column(name = "ATTR_NAME", length = 100, nullable = false)
    public String getAttrName() {
        return attrName;
    }
    
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
    
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "REGION_ID", referencedColumnName = "REGION_ID")
    public RegionVO getRegionVO() {
        return regionVO;
    }
    
    public void setRegionVO(RegionVO regionVO) {
        this.regionVO = regionVO;
    }
    
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID")
    public CategoryVO getCategoryVO() {
        return categoryVO;
    }
    
    public void setCategoryVO(CategoryVO categoryVO) {
        this.categoryVO = categoryVO;
    }
    
    @NotEmpty(message = "景點地址: 請勿空白")
    @Column(name = "ATTR_ADDRESS", length = 255, nullable = false)
    public String getAttrAddress() {
        return attrAddress;
    }
    
    public void setAttrAddress(String attrAddress) {
        this.attrAddress = attrAddress;
    }
    
    @Column(name = "LAT", precision = 10, scale = 8)
    public BigDecimal getLat() {
        return lat;
    }
    
    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }
    
    @Column(name = "LNG", precision = 11, scale = 8)
    public BigDecimal getLng() {
        return lng;
    }
    
    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }
    
    @Column(name = "ATTR_TEL", length = 30)
    public String getAttrTel() {
        return attrTel;
    }
    
    public void setAttrTel(String attrTel) {
        this.attrTel = attrTel;
    }
    
    @Column(name = "OPEN_TIME", length = 30)
    public String getOpenTime() {
        return openTime;
    }
    
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }
    
    @Column(name = "IS_OPEN", length = 30, nullable = false)
    public String getIsOpen() {
        return isOpen;
    }
    
    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
    
    @Column(name = "ATTR_VOTES")
    public Integer getAttrVotes() {
        return attrVotes;
    }
    
    public void setAttrVotes(Integer attrVotes) {
        this.attrVotes = attrVotes;
    }
    
    @Column(name = "ATTR_STARS")
    public Integer getAttrStars() {
        return attrStars;
    }
    
    public void setAttrStars(Integer attrStars) {
        this.attrStars = attrStars;
    }
    
    @Column(name = "AVG_STARS", precision = 2, scale = 1)
    public BigDecimal getAvgStars() {
        return avgStars;
    }
    
    public void setAvgStars(BigDecimal avgStars) {
        this.avgStars = avgStars;
    }
    
    @OneToMany(mappedBy = "attrVO", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<AttrImageVO> getImages() {
        return images;
    }
    
    public void setImages(Set<AttrImageVO> images) {
        this.images = images;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "AttrVO [attrId=" + attrId + ", attrName=" + attrName + ", attrAddress=" + attrAddress 
                + ", lat=" + lat + ", lng=" + lng + ", isOpen=" + isOpen + "]";
    }
}
