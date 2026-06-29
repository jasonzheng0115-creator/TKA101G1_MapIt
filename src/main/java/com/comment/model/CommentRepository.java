package com.comment.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CommentRepository - 評論資料存取介面
 * 繼承 JpaRepository 提供基本 CRUD 操作
 */
public interface CommentRepository extends JpaRepository<CommentVO, Integer> {
    
    /**
     * 根據景點 ID 查詢已上架的評論
     * 只查詢狀態為 '1' (已上架) 的評論
     * 
     * @param attrId 景點 ID
     * @return 已上架的評論列表
     */
    @Query("SELECT c FROM CommentVO c WHERE c.attrVO.attrId = :attrId AND c.commentStatus = '1' ORDER BY c.commentTime DESC")
    List<CommentVO> findApprovedCommentsByAttrId(@Param("attrId") Integer attrId);
    
    /**
     * 重新計算指定景點的平均評分
     * 只計算已上架 (COMMENT_STATUS = '1') 的評論
     * 更新 ATTRACTION 表的 AVG_STARS, ATTR_VOTES, ATTR_STARS 欄位
     * 
     * @param attrId 景點 ID
     */
    
    @Modifying
    @Query(value = "UPDATE ATTRACTION a " +
                   "SET a.AVG_STARS = (SELECT ROUND(AVG(c.COMMENT_SCORE), 1) " +
                   "                   FROM COMMENT c " +
                   "                   WHERE c.ATTR_ID = :attrId AND c.COMMENT_STATUS = '1'), " +
                   "    a.ATTR_VOTES = (SELECT COUNT(*) " +
                   "                    FROM COMMENT c " +
                   "                    WHERE c.ATTR_ID = :attrId AND c.COMMENT_STATUS = '1'), " +
                   "    a.ATTR_STARS = (SELECT SUM(c.COMMENT_SCORE) " +
                   "                    FROM COMMENT c " +
                   "                    WHERE c.ATTR_ID = :attrId AND c.COMMENT_STATUS = '1') " +
                   "WHERE a.ATTR_ID = :attrId", 
           nativeQuery = true)
    void recalculateStats(@Param("attrId") Integer attrId);
}
