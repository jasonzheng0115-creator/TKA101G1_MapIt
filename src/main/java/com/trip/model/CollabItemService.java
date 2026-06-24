package com.trip.model; // 嚴格遵守：Service 放在 model 套件中

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

    // 1. 檢查某個會員是否有權限編輯某個行程（是擁有者 或 是共同編輯者）
    public boolean hasEditPermission(Integer tripId, Integer custId) {
        // 第一關：先檢查是不是行程的「擁有者」
        TripVO trip = tripRepository.findById(tripId).orElse(null);
        if (trip != null && trip.getCustVO().getCustId().equals(custId)) {
            return true; // 是擁有者，直接放行
        }

        // 第二關：如果不是擁有者，再檢查是不是「共同編輯者」
        CollabItemVO collab = collabItemRepository.findByTripVO_TripIdAndCustVO_CustId(tripId, custId);
        return collab != null; // 找得到代表有權限，找不到代表沒有
    }

    // 2. 取得某個行程的所有共同編輯者清單
    public List<CollabItemVO> getCollaborators(Integer tripId) {
        return collabItemRepository.findByTripVO_TripId(tripId);
    }

    // 3. 新增一位共同編輯者
    @Transactional
    public CollabItemVO addCollaborator(Integer tripId, Integer custId) {
        // 防呆：檢查這個人是不是已經是共同編輯者了
        CollabItemVO existing = collabItemRepository.findByTripVO_TripIdAndCustVO_CustId(tripId, custId);
        if (existing != null) {
            throw new RuntimeException("此會員已經是共同編輯者！");
        }

        // 找出行程和會員
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));
        CustVO cust = custRepository.findById(custId)
                .orElseThrow(() -> new RuntimeException("找不到會員"));

        // 防呆：不能把行程擁有者加為共同編輯者（他本來就有權限）
        if (trip.getCustVO().getCustId().equals(custId)) {
            throw new RuntimeException("行程擁有者不需要被加為共同編輯者！");
        }

        // 建立並儲存
        CollabItemVO newCollab = new CollabItemVO();
        newCollab.setTripVO(trip);
        newCollab.setCustVO(cust);
        return collabItemRepository.save(newCollab);
    }

    // 4. 移除一位共同編輯者
    @Transactional
    public void removeCollaborator(Integer collabId) {
        collabItemRepository.deleteById(collabId);
    }

    // 5. 刪除某個行程的所有共同編輯者（給行程物理刪除時用）
    @Transactional
    public void removeAllCollaboratorsByTripId(Integer tripId) {
        collabItemRepository.deleteByTripVO_TripId(tripId);
    }

    // 6. 透過「帳號」新增一位共同編輯者
    @Transactional
    public CollabItemVO addCollaboratorByAccount(Integer tripId, String custAccount, Integer loggedInCustId) {

        // 權限檢查：只有行程「擁有者」才能新增朋友！
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        if (!trip.getCustVO().getCustId().equals(loggedInCustId)) {
            throw new RuntimeException("只有行程建立者可以管理共同編輯人！");
        }

        // 透過我們剛剛加的魔法方法，用字串去資料庫抓出朋友的會員實體
        CustVO friend = custRepository.findByAccount(custAccount);
        if (friend == null) {
            throw new RuntimeException("找不到此帳號，請確認朋友的帳號是否正確！");
        }

        // 直接呼叫我們在階段 A 寫好的方法 (原本就具備防呆機制，例如不能重複加)
        return addCollaborator(tripId, friend.getCustId());
    }

}
