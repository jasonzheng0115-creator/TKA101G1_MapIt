package com.fav.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.attr.model.AttrVO;

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
 * FavoriteVO - 收藏清單實體類別
 * 對應資料表：ATTRACTION_COLLECT
 * 主鍵類型：自動遞增 (Auto Increment)
 */
@Entity
@Table(name = "ATTRACTION_COLLECT")
public class FavoriteVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer collectId;
    private AttrVO attrVO;
    private Integer custId;  // 暫時使用 Integer，等組員建立 Customer 表後再改為物件關聯
    private LocalDateTime collectTime;
    
    // ========== 建構子 ==========
    public FavoriteVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COLLECT_ID")
    public Integer getCollectId() {
        return collectId;
    }
    
    public void setCollectId(Integer collectId) {
        this.collectId = collectId;
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
    
    @NotNull(message = "顧客編號: 請勿空白")
    @Column(name = "CUST_ID", nullable = false)
    public Integer getCustId() {
        return custId;
    }
    
    public void setCustId(Integer custId) {
        this.custId = custId;
    }
    
    @Column(name = "COLLECT_TIME")
    public LocalDateTime getCollectTime() {
        return collectTime;
    }
    
    public void setCollectTime(LocalDateTime collectTime) {
        this.collectTime = collectTime;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "FavoriteVO [collectId=" + collectId + ", custId=" + custId 
                + ", collectTime=" + collectTime + "]";
    }
}
