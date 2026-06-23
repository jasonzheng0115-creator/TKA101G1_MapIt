package com.trip.model;

import com.trip.model.TripItemVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripItemRepository extends JpaRepository<TripItemVO, Integer> {

    // 找出某個行程的所有明細，並且依照順序 (SeqNo) 由小到大排好
    List<TripItemVO> findByTrip_TripIdOrderBySeqNoAsc(Integer tripId);

    void deleteByTrip_TripId(Integer tripId);

}
