package com.trip.model;

import com.cust.model.CustVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripItemRepository tripItemRepository;

    @Autowired
    private CollabItemRepository collabItemRepository;

    @Autowired
    private CollabItemService collabItemService;

    // 1. 取得特定會員擁有的所有行程
    public List<TripVO> getTripsByCustomer(CustVO custVO) {
        // 1.1 先找出自己建立的所有行程
        List<TripVO> myTrips = tripRepository.findByCustVO(custVO);

        // 1.2 建立一個新的 ArrayList 合併清單，並把自己的行程放進去
        List<TripVO> allTrips = new java.util.ArrayList<>(myTrips);

        // 1.3 透過 collabItemRepository，找出所有跟「這個會員(custVO)」有關的協作紀錄
        List<CollabItemVO> collabItems = collabItemRepository.findByCustVO(custVO);

        // 1.4 用簡單的 for 迴圈，把共享行程一筆一筆加進合併清單
        for (CollabItemVO collab : collabItems) {
            TripVO collabTrip = collab.getTripVO(); // 取得該筆協作對應的行程物件

            // 防呆：如果這個行程沒有在清單中，才加進去（避免重複加入）
            if (!allTrips.contains(collabTrip)) {
                allTrips.add(collabTrip);
            }
        }

        // 1.5 為清單中的所有行程載入協作者 (排除擁有者自己，避免重複)
        for (TripVO trip : allTrips) {
            List<CollabItemVO> collabs = collabItemRepository.findByTripVO_TripId(trip.getTripId());
            List<CustVO> collabCusts = new java.util.ArrayList<>();
            for (CollabItemVO collab : collabs) {
                CustVO collabCust = collab.getCustVO();
                if (collabCust != null && !collabCust.getCustId().equals(trip.getCustVO().getCustId())) {
                    collabCusts.add(collabCust);
                }
            }
            trip.setCollaborators(collabCusts);
        }

        // 1.6 回傳合併完成 hometown行程清單
        return allTrips;
    }

    // 2. 新增行程
    @Transactional // 加上交易控管，確保資料庫寫入安全
    public TripVO createTrip(TripVO trip, CustVO owner) {
        // 綁定行程的擁有者
        trip.setCustVO(owner);
        // 存入資料庫並回傳 (因為回傳的物件才會有資料庫自動生成的 TripId)
        return tripRepository.save(trip);
    }

    // 3. 取得特定行程的編輯畫面 (放寬權限防護：擁有者與協作者皆可進入)
    public TripVO getTripByIdAndPermission(Integer tripId, Integer loginCustId) {
        // 1. 從資料庫找行程，並收到一個 Optional 禮物盒
        Optional<TripVO> optionalTrip = tripRepository.findById(tripId);

        // 2. 使用 isEmpty() 檢查這個盒子是不是空的 (即資料庫根本沒這筆行程)
        if (optionalTrip.isEmpty()) {
            return null;// 如果是空盒子，直接回傳 null (防呆 1)
        }

        // 3. 確定裡面有禮物後，呼叫 get() 打開盒子，拿出真正的 TripVO 物件
        TripVO trip = optionalTrip.get();

        // 防呆 2 (權限控管)：確認登入的會員是否具有編輯此行程的權限
        // 將參數由左至右傳遞給 collabItemService.hasEditPermission 方法進行檢查
        if (!collabItemService.hasEditPermission(tripId, loginCustId)) {
            return null; // 沒有編輯權限，回傳 null 拒絕存取
        }

        return trip;
    }

    // 4. 物理刪除行程（連同明細和共同編輯者一起清掉）
    @Transactional
    public void deleteTrip(Integer tripId, Integer loginCustId) {
        // 防呆 1：先確認行程存在
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        // 防呆 2：只有行程「擁有者」才能刪除（共同編輯者不行）
        if (!trip.getCustVO().getCustId().equals(loginCustId)) {
            throw new RuntimeException("只有行程擁有者才能刪除行程！");
        }

        // ★ 刪除順序很重要：先刪子表，再刪母表
        // 第一步：刪掉所有共同編輯者紀錄（COLLAB_ITEM）
        collabItemRepository.deleteByTripVO_TripId(tripId);

        // 第二步：刪掉所有景點明細（TRIP_ITEM）
        tripItemRepository.deleteByTrip_TripId(tripId);

        // 第三步：最後才刪行程本身（TRIP）
        tripRepository.deleteById(tripId);
    }

    // 5. 更新行程基本資訊
    @Transactional
    public void updateTripInfo(Integer tripId, String tripName, java.sql.Date tripDate, Boolean tripStatus,
            Integer loginCustId) {
        // 找出要更新的行程
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        // 權限防護：要有編輯權限才可以修改 (包含擁有者與協作者)
        if (!collabItemService.hasEditPermission(tripId, loginCustId)) {
            throw new RuntimeException("你沒有權限修改此行程！");
        }

        // 更新資料
        trip.setTripName(tripName);
        trip.setTripDate(tripDate);
        trip.setTripStatus(tripStatus);

        // 儲存進資料庫
        tripRepository.save(trip);
    }

}