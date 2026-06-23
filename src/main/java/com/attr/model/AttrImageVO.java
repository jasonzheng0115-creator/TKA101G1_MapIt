package com.attr.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * AttrImageVO - 景點圖片實體類別
 * 對應資料表：attr_image (小寫表名，符合 MySQL 規範)
 * 主鍵類型：自動遞增 (Auto Increment)
 * 關聯：多對一關聯到 AttrVO (一個景點可以有多張圖片)
 * 
 * 更新說明 (2026/06/22)：
 * - 從 com.image.model.ImageVO 重構為 com.attr.model.AttrImageVO
 * - 修正 @Table 為小寫表名 "attr_image"，符合 MySQL 資料庫規範
 * - 符合 CLAUDE.md 專案規範：使用 com.attr 模組，避免 AI 發明的包名
 */
@Entity
@Table(name = "attr_image")
public class AttrImageVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer imageId;
    private AttrVO attrVO;
    private String imagePath;
    private String imageUrl;
    private Boolean isMain;
    private LocalDateTime uploadTime;
    
    // ========== 建構子 ==========
    public AttrImageVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    public Integer getImageId() {
        return imageId;
    }
    
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
    
    @NotNull(message = "景點: 請選擇景點")
    @ManyToOne
    @JoinColumn(name = "ATTR_ID", referencedColumnName = "ATTR_ID")
    public AttrVO getAttrVO() {
        return attrVO;
    }
    
    public void setAttrVO(AttrVO attrVO) {
        this.attrVO = attrVO;
    }
    
    @Column(name = "IMAGE_PATH", length = 500)
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Column(name = "IMAGE_URL", length = 500)
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @NotNull(message = "是否為主圖: 請勿空白")
    @Column(name = "IS_MAIN", nullable = false)
    public Boolean getIsMain() {
        return isMain;
    }
    
    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }
    
    @NotNull(message = "上傳時間: 請勿空白")
    @Column(name = "UPLOAD_TIME", nullable = false)
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
    
    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "AttrImageVO [imageId=" + imageId + ", imagePath=" + imagePath 
                + ", imageUrl=" + imageUrl + ", isMain=" + isMain 
                + ", uploadTime=" + uploadTime + "]";
    }
}
