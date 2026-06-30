package com.message.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageVO, Integer> {
    
    /**
     * 查詢某位會員的所有通知訊息，並依照時間由新到舊排序(給eclipse的註解)
     * @param custId 會員編號
     * @return 該會員的訊息列表
     */
    @Query("SELECT messageVO FROM MessageVO messageVO WHERE messageVO.custVO.custId = ?1 ORDER BY messageVO.msgDatetime DESC")
    List<MessageVO> findByCustIdOrderByMsgDatetimeDesc(Integer custId);
    
    /**
     * 查詢某位會員的「未讀」訊息數量(給eclipse的註解)
     * @param custId 會員編號
     * @return 未讀訊息數量
     */
    @Query("SELECT COUNT(messageVO) FROM MessageVO messageVO WHERE messageVO.custVO.custId = ?1 AND messageVO.msgStatus = 0")
    long countUnreadMessagesByCustId(Integer custId);
}
