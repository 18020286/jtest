package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "ATTACH")
public class Attach extends BaseEntity {
	@Column(name = "USER_ID")
	private BigInteger userId;

	@Column(name = "ATTACH_FILE_NAME")
	private String attactFileName;
	
	@Column(name = "ATTACH_FILE_TYPE")
	private String attactFileType;

	@Column(name = "MEDIA_ID")
	private BigInteger mediaId;

	@Column(name = "ATTACH_TYPE")
	private int attachType;

	public BigInteger getUserId() {
		return userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

	public String getAttactFileName() {
		return attactFileName;
	}

	public void setAttactFileName(String attactFileName) {
		this.attactFileName = attactFileName;
	}

	public BigInteger getMediaId() {
		return mediaId;
	}

	public void setMediaId(BigInteger mediaId) {
		this.mediaId = mediaId;
	}

	public int getAttachType() {
		return attachType;
	}

	public void setAttachType(int attachType) {
		this.attachType = attachType;
	}

	public String getAttactFileType() {
		return attactFileType;
	}

	public void setAttactFileType(String attactFileType) {
		this.attactFileType = attactFileType;
	}
}
