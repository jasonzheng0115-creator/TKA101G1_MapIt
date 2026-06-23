package com.trip.model; // 嚴格遵守：VO 放在 model 套件中

import java.io.Serializable;

import com.cust.model.CustVO;

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
@Table(name = "COLLAB_ITEM")
public class CollabItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COLLAB_ID")
    private Integer collabId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRIP_ID", nullable = false)
    private TripVO tripVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUST_ID", nullable = false)
    private CustVO custVO;

    // ==========================================
    // 建構子 (Constructors)
    // ==========================================
    public CollabItemVO() {
    }

    // ==========================================
    // Getter & Setter
    // ==========================================
    public Integer getCollabId() {
        return collabId;
    }

    public void setCollabId(Integer collabId) {
        this.collabId = collabId;
    }

    public TripVO getTripVO() {
        return tripVO;
    }

    public void setTripVO(TripVO tripVO) {
        this.tripVO = tripVO;
    }

    public CustVO getCustVO() {
        return custVO;
    }

    public void setCustVO(CustVO custVO) {
        this.custVO = custVO;
    }

}
