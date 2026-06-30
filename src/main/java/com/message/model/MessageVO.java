package com.message.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "message")
public class MessageVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MSG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer msgId;

	// 會員CustVO建立多對一關聯
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUST_ID")
	private CustVO custVO;

	@Column(name = "MSG_HEADLINE")
	@NotEmpty(message="訊息標題，請勿空白")
	private String msgHeadline;

	@Column(name = "MSG_CONTENT")
	private String msgContent;

	@Column(name = "MSG_PIC")
	private String msgPic;

	@Column(name = "MSG_DATETIME")
	private Timestamp msgDatetime;

	@Column(name = "MSG_STATUS", insertable = false)
	private Byte msgStatus;

	
	public MessageVO() {
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public CustVO getCustVO() {
		return custVO;
	}

	public void setCustVO(CustVO custVO) {
		this.custVO = custVO;
	}

	public String getMsgHeadline() {
		return msgHeadline;
	}

	public void setMsgHeadline(String msgHeadline) {
		this.msgHeadline = msgHeadline;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgPic() {
		return msgPic;
	}

	public void setMsgPic(String msgPic) {
		this.msgPic = msgPic;
	}

	public Timestamp getMsgDatetime() {
		return msgDatetime;
	}

	public void setMsgDatetime(Timestamp msgDatetime) {
		this.msgDatetime = msgDatetime;
	}

	public Byte getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(Byte msgStatus) {
		this.msgStatus = msgStatus;
	}
}
