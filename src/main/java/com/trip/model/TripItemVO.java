package com.trip.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.attr.model.AttrVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRIP_ITEM")
public class TripItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Integer itemId;

    // 多對一：對應 TripVO 實體，外鍵為 TRIP_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRIP_ID", nullable = false)
    private TripVO trip;

    // 多對一：對應 AttrVO 實體，外鍵為 ATTR_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTR_ID", nullable = false)
    private AttrVO attraction;

    @Column(name = "SEQ_NO")
    private Integer seqNo;

    @Column(name = "ARRIVAL_TIME")
    private LocalDateTime arrivalTime;

    @Column(name = "DEP_TIME")
    private LocalDateTime depTime;

    @Column(name = "TRAVEL_TIME")
    private Integer travelTime;
    
    @Column(name = "ITEM_NOTE", length = 500)
    private String itemNote;

    // ==========================================
    // 1. 建構子 (Constructors)
    // ==========================================
    
    // Hibernate 強烈建議一定要有無參數建構子 (No-Arg Constructor)
    public TripItemVO() {
    }

    // 全參數建構子 (方便在程式中直接 new 物件)
//    public TripItem(Integer itemId, TripVO trip, AttrVO attraction, Integer seqNo, 
//                    LocalDateTime arrivalTime, LocalDateTime depTime, Integer travelTime) {
    public TripItemVO(Integer itemId, TripVO trip, Integer seqNo, 
                    LocalDateTime arrivalTime, LocalDateTime depTime, Integer travelTime) {
        this.itemId = itemId;
        this.trip = trip;
//        this.attraction = attraction;
        this.seqNo = seqNo;
        this.arrivalTime = arrivalTime;
        this.depTime = depTime;
        this.travelTime = travelTime;
    }

    // ==========================================
    // 2. Getter & Setter 方法
    // ==========================================

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public TripVO getTrip() {
        return trip;
    }

    public void setTrip(TripVO trip) {
        this.trip = trip;
    }

    public com.attr.model.AttrVO getAttraction() {
        return attraction;
    }
    
    public void setAttraction(com.attr.model.AttrVO attraction) {
        this.attraction = attraction;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepTime() {
        return depTime;
    }

    public void setDepTime(LocalDateTime depTime) {
        this.depTime = depTime;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }
    
    public String getItemNote() {
        return itemNote;
    }

    public void setItemNote(String itemNote) {
        this.itemNote = itemNote;
    }
}