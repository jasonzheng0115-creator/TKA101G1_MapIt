package com.comment.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CommentService - 評論業務邏輯層
 * 
 * 職責：
 * 1. 封裝所有與評論相關的業務邏輯
 * 2. 呼叫 CommentRepository 進行資料存取
 * 3. 提供評論狀態切換與評分重算功能
 * 4. 提供給 Controller 層使用
 */
@Service
@Transactional
public class CommentService {
    
    // ========== 依賴注入 ==========
    private CommentRepository commentRepository;
    
    /**
     * 建構子注入（推薦方式，Spring 4.3+ 無需 @Autowired）
     */
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    // ========== 評論新增方法 ==========
    
    /**
     * 新增評論
     * @param commentVO 評論物件
     * @return 儲存後的評論物件
     */
    public CommentVO addComment(CommentVO commentVO) {
        // 設定評論時間為當前時間
        if (commentVO.getCommentTime() == null) {
            commentVO.setCommentTime(LocalDateTime.now());
        }
        // 預設狀態為待審核 (0)
        if (commentVO.getCommentStatus() == null) {
            commentVO.setCommentStatus("0");
        }
        return commentRepository.save(commentVO);
    }
    
    // ========== 評論更新方法 ==========
    
    /**
     * 更新評論
     * @param commentVO 評論物件（必須包含 commentId）
     * @return 更新後的評論物件
     */
    public CommentVO updateComment(CommentVO commentVO) {
        return commentRepository.save(commentVO);
    }
    
    // ========== 評論刪除方法 ==========
    
    /**
     * 刪除評論
     * @param commentId 評論 ID
     */
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
    
    // ========== 評論查詢方法 ==========
    
    /**
     * 根據評論 ID 查詢單一評論
     * @param commentId 評論 ID
     * @return 評論物件，若不存在則回傳 null
     */
    public CommentVO getOneComment(Integer commentId) {
        Optional<CommentVO> optional = commentRepository.findById(commentId);
        return optional.orElse(null);
    }
    
    /**
     * 查詢所有評論
     * @return 所有評論列表
     */
    public List<CommentVO> getAll() {
        return commentRepository.findAll();
    }
    
    /**
     * 根據景點 ID 查詢已上架的評論
     * 只查詢狀態為 '1' (已上架) 的評論
     * 
     * @param attrId 景點 ID
     * @return 已上架的評論列表
     */
    public List<CommentVO> getApprovedComments(Integer attrId) {
        return commentRepository.findApprovedCommentsByAttrId(attrId);
    }
    
    // ========== 評論狀態切換方法 ==========
    
    /**
     * 切換評論狀態（上架/下架）
     * 狀態切換後會自動重新計算該景點的平均評分
     * 
     * @param commentId 評論 ID
     * @param newStatus 新狀態 ("0": 待審核, "1": 已上架, "2": 已下架)
     */
    public void updateCommentStatus(Integer commentId, String newStatus) {
        CommentVO comment = getOneComment(commentId);
        if (comment != null) {
            comment.setCommentStatus(newStatus);
            commentRepository.save(comment);
            
            // 狀態切換後，重新計算該景點的評分統計
            if (comment.getAttrVO() != null && comment.getAttrVO().getAttrId() != null) {
                recalculateStats(comment.getAttrVO().getAttrId());
            }
        }
    }
    
    // ========== 評分統計重算方法 ==========
    
    /**
     * 重新計算指定景點的評分統計
     * 只計算已上架 (COMMENT_STATUS = '1') 的評論
     * 更新 ATTRACTION 表的 AVG_STARS, ATTR_VOTES, ATTR_STARS 欄位
     * 
     * @param attrId 景點 ID
     */
    public void recalculateStats(Integer attrId) {
        commentRepository.recalculateStats(attrId);
    }
}
