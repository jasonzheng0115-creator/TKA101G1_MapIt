package com.trip.model;

import com.cust.model.CustVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripVO, Integer> {

    // 關鍵功能：根據「建立者（CustVO）」找出他所有的行程
    // Spring Data JPA 會去對照 TripVO 裡面的 custVO 屬性！
    List<TripVO> findByCustVO(CustVO custVO);
}