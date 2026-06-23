package com.trip.model; // 嚴格遵守：Repository 放在 model 套件中

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CollabItemRepository extends JpaRepository<CollabItemVO, Integer> {

    // 1. 找出某個行程的所有共同編輯者
    List<CollabItemVO> findByTripVO_TripId(Integer tripId);

    // 2. 查詢某個會員是否是某行程的共同編輯者（用來做權限檢查）
    CollabItemVO findByTripVO_TripIdAndCustVO_CustId(Integer tripId, Integer custId);

    // 3. 刪除某個行程下的所有共同編輯者（行程被物理刪除時，要連協作者一起清掉）
    void deleteByTripVO_TripId(Integer tripId);

}
