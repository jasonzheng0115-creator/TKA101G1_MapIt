package com.trip.model;

import com.cust.model.CustVO;
import com.cust.model.CustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollabItemService {

    @Autowired
    private CollabItemRepository collabItemRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CustRepository custRepository;

    // 1. 檢查某個會員是否有權限編輯某個行程（是擁有者 或 是群組成員）
    public boolean hasEditPermission(Integer tripId, Integer custId) {
        // 第一：先檢查是不是行程的「擁有者」
        // .findById()回傳Optional，.orElse(null)如果沒東西就回傳null
        TripVO trip = tripRepository.findById(tripId).orElse(null);
        // 找出這個行程的建立者的ID，與目前登入者的ID進行比對(.equals)
        if (trip != null && trip.getCustVO().getCustId().equals(custId)) {
            return true; // 是擁有者
        }

        // 第二：如果不是擁有者，再檢查是不是「群組成員」
        CollabItemVO collab = collabItemRepository.findByTripVO_TripIdAndCustVO_CustId(tripId, custId);
        return collab != null; // 找得到代表有權限，找不到代表沒有
    }

    // 2. 取得某個行程的所有群組成員清單
    public List<CollabItemVO> getCollaborators(Integer tripId) {
        return collabItemRepository.findByTripVO_TripId(tripId);
    }

    // 3. 透過「帳號名稱」找出ID後，呼叫addCollaborator方法
    @Transactional
    public CollabItemVO addCollaboratorByAccount(Integer tripId, String custAccount, Integer loggedInCustId) {

        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        // 權限檢查：只有行程「擁有者」才能新增群組成員
        if (!trip.getCustVO().getCustId().equals(loggedInCustId)) {
            throw new RuntimeException("只有行程建立者可以管理群組成員！");
        }

        // 透過前端傳到控制器再傳來的custAccount(字串)，去資料庫抓出好友的會員實體(custVO)
        CustVO friend = custRepository.findByAccount(custAccount);
        if (friend == null) {
            throw new RuntimeException("找不到此帳號，請確認朋友的帳號是否正確！");
        }

        // 透過collabItemVO中的 (tripVO中的TripId) 以及 (custVO中的CustId) 來找出協作名單明細
        CollabItemVO existing = collabItemRepository.findByTripVO_TripIdAndCustVO_CustId(tripId, friend.getCustId());
        if (existing != null) {
            throw new RuntimeException("此會員已經是群組成員了！");
        }

        // 不能把行程擁有者加為群組成員（他本來就有權限）
        // 從tripVO中，透過custVO找到行程建立者的custId，並與好友的會員實體(custVO)的custId進行比對
        if (trip.getCustVO().getCustId().equals(friend.getCustId())) {
            throw new RuntimeException("此會員是行程建立者！");
        }

        // 建立新的協作名單明細物件(CollabItemVO)並儲存
        CollabItemVO newCollab = new CollabItemVO();
        newCollab.setTripVO(trip);
        newCollab.setCustVO(friend);
        return collabItemRepository.save(newCollab);

    }

    // 4. 刪除一位群組成員
    @Transactional
    public void removeCollaborator(Integer collabId) {
        collabItemRepository.deleteById(collabId);
    }

    // 5. 根據tripId來刪除那個行程的所有群組成員
    @Transactional
    public void removeAllCollaboratorsByTripId(Integer tripId) {
        collabItemRepository.deleteByTripVO_TripId(tripId);
    }

    // 6. 根據 ID 找出協作名單紀錄 (CollabItemVO)，有找到表示他是群組成員 (移除群組成員時，檢查用)
    public CollabItemVO getCollabById(Integer collabId) {
        return collabItemRepository.findById(collabId).orElse(null);
    }

    // 7. 退出群組 (群組成員在列表點擊「退出編輯」時執行)
    @Transactional
    public void exitCollaboration(Integer tripId, Integer custId) {
        // 先根據行程 ID 和會員 ID 找出該筆協作紀錄
        CollabItemVO collab = collabItemRepository.findByTripVO_TripIdAndCustVO_CustId(tripId, custId);

        // 防呆：如果找不到這筆紀錄，代表他本來就不是群組成員
        if (collab != null) {
            // 從資料庫中刪除這筆協作關係
            collabItemRepository.delete(collab);
        } else {
            throw new RuntimeException("你本來就不是此行程的群組成員！");
        }
    }

}
