package com.comment.model;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * CommentVO - 評論實體類別
 * 對應資料表：COMMENT
 * 主鍵類型：自動遞增 (Auto Increment)
 */
@Entity
@Table(name = "COMMENT")
public class CommentVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 屬性欄位 ==========
    private Integer commentId;
    private AttrVO attrVO;
    private Integer custId;  // 暫時使用 Integer，等組員建立 Customer 表後再改為物件關聯
    private String commentContent;
    private Byte commentScore;
    private LocalDateTime commentTime;
    private String commentStatus;
    
    // ========== 建構子 ==========
    public CommentVO() {
        super();
    }
    
    // ========== Getter & Setter (JPA 註解放在 Getter 上) ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    public Integer getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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
    
    @NotEmpty(message = "評論內容: 請勿空白")
    @Column(name = "COMMENT_CONTENT", length = 1000)
    public String getCommentContent() {
        return commentContent;
    }
    
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
    
    @NotNull(message = "評分: 請選擇評分")
    @Min(value = 1, message = "評分: 最低為 1 分")
    @Max(value = 5, message = "評分: 最高為 5 分")
    @Column(name = "COMMENT_SCORE", nullable = false)
    public Byte getCommentScore() {
        return commentScore;
    }
    
    public void setCommentScore(Byte commentScore) {
        this.commentScore = commentScore;
    }
    
    @Column(name = "COMMENT_TIME")
    public LocalDateTime getCommentTime() {
        return commentTime;
    }
    
    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }
    
    @Column(name = "COMMENT_STATUS", length = 10)
    public String getCommentStatus() {
        return commentStatus;
    }
    
    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "CommentVO [commentId=" + commentId + ", custId=" + custId 
                + ", commentScore=" + commentScore + ", commentTime=" + commentTime 
                + ", commentStatus=" + commentStatus + "]";
    }
}
