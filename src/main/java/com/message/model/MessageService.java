package com.message.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cust.model.CustRepository;
import com.cust.model.CustVO;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repository;

    /**
     * 取得該會員所有的通知訊息 (由新到舊排序)
     */
    public List<MessageVO> getMessagesByCustId(Integer custId) {
        return repository.findByCustIdOrderByMsgDatetimeDesc(custId);
    }

    /**
     * 取得該會員目前的未讀訊息總數
     */
    public long getUnreadCount(Integer custId) {
        return repository.countUnreadMessagesByCustId(custId);
    }

    /**
     * 將指定的訊息標記為已讀
     * @param msgId 訊息編號
     * @return 標記是否成功
     */
    public boolean markAsRead(Integer msgId) {
        Optional<MessageVO> optionalMessage = repository.findById(msgId);
        if (optionalMessage.isPresent()) {
            MessageVO message = optionalMessage.get();
            // 如果原本是未讀(0)，才需要改成已讀(1)並存檔
            if (message.getMsgStatus() == 0) {
                message.setMsgStatus((byte) 1);
                repository.save(message);
            }
            return true;
        }
        return false;
    }

    /**
     * 取得訊息的圖片絕對路徑
     */
    public String getMessagePicPath(Integer msgId) {
        Optional<MessageVO> optionalMessage = repository.findById(msgId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get().getMsgPic();
        }
        return null;
    }

    @Autowired
    private CustRepository custRepository;

    /**
     * 發送給全體會員
     */
    @Transactional
    public void sendNotificationToAll(String title, String content, String msgPicPath) {
        List<CustVO> allCusts = custRepository.findAll();
        List<MessageVO> messages = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (CustVO cust : allCusts) {
            MessageVO msg = new MessageVO();
            msg.setCustVO(cust);
            msg.setMsgHeadline(title);
            msg.setMsgContent(content);
            msg.setMsgPic(msgPicPath); // 將本地端絕對路徑存入 DB
            msg.setMsgDatetime(now);
            // msgStatus 在 DB default 是 0，但還是明確塞 0 保險
            msg.setMsgStatus((byte) 0);
            messages.add(msg);
        }
        repository.saveAll(messages);
    }
    /**
     * 發送給單一會員
     */
    public void sendNotificationToUser(Integer custId, String title, String content, String msgPicPath) {
    	//去資料庫找會員 (回傳一個 Optional 盒子)
        Optional<CustVO> oneCust = custRepository.findById(custId);
        if (oneCust.isPresent()) {
        	CustVO cust = oneCust.get();
            MessageVO msg = new MessageVO();
            msg.setCustVO(cust); //收件人
            msg.setMsgHeadline(title);
            msg.setMsgContent(content);
            msg.setMsgPic(msgPicPath); // 將本地端絕對路徑存入 DB
            msg.setMsgDatetime(new Timestamp(System.currentTimeMillis()));
            msg.setMsgStatus((byte) 0); // 0 代表未讀
            repository.save(msg);
        }
    }
}
