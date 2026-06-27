package com.trip.model; // 嚴格遵守：Service 放在 model 套件中

import com.attr.model.AttrRepository;
import com.attr.model.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // 標註為 Spring 管理的 Service 元件
public class TripItemService {

    @Autowired
    private TripItemRepository tripItemRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AttrRepository attrRepository;

    @Autowired
    private CollabItemService collabItemService;

    // 1. 負責「將景點加入行程」的核心邏輯
    @Transactional // 加上這行：保證過程中如果出錯，資料庫會自動還原，不會存入一半的髒資料
    public void addArrcToTrip(Integer tripId, Integer attrId) {
        // 尋找行程與景點，找不到就拋出錯誤讓 Controller 去接
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));
        AttrVO attr = attrRepository.findById(attrId)
                .orElseThrow(() -> new RuntimeException("找不到景點"));

        // 計算這是第幾個景點
        List<TripItemVO> existingItems = tripItemRepository.findByTrip_TripIdOrderBySeqNoAsc(tripId);
        int nextSeqNo = existingItems.size() + 1;

        // 建立並儲存明細
        TripItemVO newItem = new TripItemVO();
        newItem.setTrip(trip);
        newItem.setAttraction(attr);
        newItem.setSeqNo(nextSeqNo);

        tripItemRepository.save(newItem);
    }

    // 2. 負責「撈出明細並包裝成前端需要的格式」的核心邏輯
    public List<Map<String, Object>> getTripItemsFormatForFrontend(Integer tripId) {
        List<TripItemVO> items = tripItemRepository.findByTrip_TripIdOrderBySeqNoAsc(tripId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (TripItemVO item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("itemId", item.getItemId());
            map.put("seqNo", item.getSeqNo());
            map.put("attrName", item.getAttraction().getAttrName());
            map.put("attrAddress", item.getAttraction().getAttrAddress());
            // 處理備註空值
            map.put("itemNote", item.getItemNote() != null ? item.getItemNote() : "");

            // 新增：把 LocalDateTime 轉換成字串回傳給前端 (處理空值)
            map.put("arrivalTime", item.getArrivalTime() != null ? item.getArrivalTime().toString() : "");
            map.put("depTime", item.getDepTime() != null ? item.getDepTime().toString() : "");
            // 經緯度
            map.put("latitude", item.getAttraction().getLat());
            map.put("longitude", item.getAttraction().getLng());

            resultList.add(map);
        }

        return resultList;
    }

    // 3. 刪除單一景點明細，並重新計算剩餘景點的排序 (SeqNo)
    @Transactional
    public void deleteTripItem(Integer itemId, Integer loggedInCustId) {
        // 找出要刪除的明細
        TripItemVO itemToDelete = tripItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("找不到該景點明細"));

        Integer tripId = itemToDelete.getTrip().getTripId();

        // 權限防護：不是擁有者也不是共同編輯者，就不准刪！
        if (!collabItemService.hasEditPermission(tripId, loggedInCustId)) {
            throw new RuntimeException("你沒有權限修改此行程！");
        }

        // 記下被刪除的序號
        int deletedSeqNo = itemToDelete.getSeqNo();

        // 刪除該明細
        tripItemRepository.delete(itemToDelete);

        // ★ 核心邏輯：將原本排在它後面的景點，順序全部往前遞減 1
        List<TripItemVO> remainingItems = tripItemRepository.findByTrip_TripIdOrderBySeqNoAsc(tripId);
        for (TripItemVO item : remainingItems) {
            if (item.getSeqNo() > deletedSeqNo) {
                item.setSeqNo(item.getSeqNo() - 1);
                tripItemRepository.save(item);
            }
        }
    }

    // 4. 更新景點明細的內容（包含到達時間、離開時間、備忘錄）
    @Transactional
    public void updateTripItemDetails(Integer itemId, java.time.LocalDateTime arrivalTime,
            java.time.LocalDateTime depTime, String itemNote,
            Integer loggedInCustId) {
        TripItemVO item = tripItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("找不到該景點明細"));

        Integer tripId = item.getTrip().getTripId();

        // 權限防護
        if (!collabItemService.hasEditPermission(tripId, loggedInCustId)) {
            throw new RuntimeException("你沒有權限修改此行程！");
        }

        // 更新資料
        item.setArrivalTime(arrivalTime);
        item.setDepTime(depTime);
        item.setItemNote(itemNote);

        tripItemRepository.save(item);
    }

    // 5. 重新排序行程明細的序號 (SeqNo)
    @Transactional
    public void reorderTripItems(List<Integer> itemIds, Integer loginCustId) {
        if (itemIds == null || itemIds.isEmpty())
            return;

        // 1. 用第一個 ID 撈出明細以取得行程 ID
        TripItemVO firstItem = tripItemRepository.findById(itemIds.get(0))
                .orElseThrow(() -> new RuntimeException("找不到景點明細"));
        Integer tripId = firstItem.getTrip().getTripId();

        // 2. 權限防護：檢查是否有編輯權限（建立者或協作者）
        if (!collabItemService.hasEditPermission(tripId, loginCustId)) {
            throw new RuntimeException("你沒有權限修改此行程！");
        }

        // 3. 用簡單的 for 迴圈，依據傳入的順序更新資料庫中的 seqNo
        for (int i = 0; i < itemIds.size(); i++) {
            Integer itemId = itemIds.get(i);
            TripItemVO item = tripItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("找不到景點明細 ID: " + itemId));

            // 安全防呆：防範跨行程非法修改他人明細
            if (!item.getTrip().getTripId().equals(tripId)) {
                throw new RuntimeException("非法操作：不允許跨行程修改順序！");
            }

            item.setSeqNo(i + 1); // 序號從 1 開始遞增
            tripItemRepository.save(item);
        }
    }

}