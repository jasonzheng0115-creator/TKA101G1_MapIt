package com.trip.model;

import java.sql.Date;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "TRIP")
public class TripVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRIP_ID")
	private Integer tripId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUST_ID")
	private CustVO custVO;

	@Column(name = "TRIP_NAME")
	@NotEmpty(message = "請填入行程名稱")
	private String tripName;

	@Column(name = "TRIP_DATE")
	@NotNull(message = "請選擇行程日期")
	private Date tripDate;

	@Column(name = "TRIP_STATUS")
	private Boolean tripStatus = false; // 預設為私有;

	@Transient
	private java.util.List<CustVO> collaborators;

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public CustVO getCustVO() {
		return custVO;
	}

	public void setCustVO(CustVO custVO) {
		this.custVO = custVO;
	}

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public Date getTripDate() {
		return tripDate;
	}

	public void setTripDate(Date tripDate) {
		this.tripDate = tripDate;
	}

	public Boolean getTripStatus() {
		return tripStatus;
	}

	public void setTripStatus(Boolean tripStatus) {
		this.tripStatus = tripStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public java.util.List<CustVO> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(java.util.List<CustVO> collaborators) {
		this.collaborators = collaborators;
	}
}
