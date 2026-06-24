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
        return tripRepository.findByCustVO(custVO);
    }

    // 2. 新增行程
    @Transactional // 加上交易控管，確保資料庫寫入安全
    public TripVO createTrip(TripVO trip, CustVO owner) {
        // 綁定行程的擁有者
        trip.setCustVO(owner);
        // 存入資料庫並回傳 (因為回傳的物件才會有資料庫自動生成的 TripId)
        return tripRepository.save(trip);
    }

    // 3. 取得特定行程的編輯畫面 (包含嚴格的權限防護)
    public TripVO getTripByIdAndOwner(Integer tripId, Integer loggedInCustId) {
        Optional<TripVO> optionalTrip = tripRepository.findById(tripId);

        // 防呆 1：如果資料庫裡根本沒有這個行程
        if (optionalTrip.isEmpty()) {
            return null;
        }

        TripVO trip = optionalTrip.get();

        // 防呆 2 (權限控管)：確認登入的會員，是不是這個行程的擁有者
        if (!trip.getCustVO().getCustId().equals(loggedInCustId)) {
            return null; // 不是擁有者，回傳 null 拒絕存取
        }

        return trip;
    }

    // 4. 物理刪除行程（連同明細和共同編輯者一起清掉）
    @Transactional
    public void deleteTrip(Integer tripId, Integer loggedInCustId) {
        // 防呆 1：先確認行程存在
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        // 防呆 2：只有行程「擁有者」才能刪除（共同編輯者不行）
        if (!trip.getCustVO().getCustId().equals(loggedInCustId)) {
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
    public void updateTripInfo(Integer tripId, String tripName, java.sql.Date tripDate, Boolean tripStatus, Integer loggedInCustId) {
        // 找出要更新的行程
        TripVO trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("找不到行程"));

        // 權限防護：要有編輯權限才可以修改 (包含擁有者與協作者)
        if (!collabItemService.hasEditPermission(tripId, loggedInCustId)) {
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