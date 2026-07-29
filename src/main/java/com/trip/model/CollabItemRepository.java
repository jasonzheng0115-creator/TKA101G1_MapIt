package com.trip.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.cust.model.CustVO;

@Repository
public interface CollabItemRepository extends JpaRepository<CollabItemVO, Integer> {

    // 1. 根據tripId找出所有群組成員(CollabItemVO)
    List<CollabItemVO> findByTripVO_TripId(Integer tripId);

    // 2. 查詢某個會員是否是某行程的群組成員(CollabItemVO)（用來做權限檢查）
    CollabItemVO findByTripVO_TripIdAndCustVO_CustId(Integer tripId, Integer custId);

    // 3. 刪除某個行程下的所有群組成員（行程被刪除時，要連群組成員明細一起清掉）
    void deleteByTripVO_TripId(Integer tripId);

    // 4. 根據會員（CustVO）找出他被加入的所有旅遊行程紀錄（CollabItemVO）
    List<CollabItemVO> findByCustVO(CustVO custVO);

}
