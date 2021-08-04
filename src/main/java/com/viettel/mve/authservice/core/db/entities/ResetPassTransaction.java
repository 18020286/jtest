package com.viettel.mve.authservice.core.db.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "RESET_PASS_TRANSACTION")
public class ResetPassTransaction extends BaseEntity{

	@OneToOne
	@JoinColumn(name = "USER_ID")
	private MVEUser user;

	@Column(name = "RESET_PASS_CODE")
	private String resetPassCode;

	@Column(name = "EXPIRED_DATE")
	private Date expireDate;

	@Column(name = "STATUS")
	private int status;

	public MVEUser getUser() {
		return user;
	}

	public void setUser(MVEUser user) {
		this.user = user;
	}

	public String getResetPassCode() {
		return resetPassCode;
	}

	public void setResetPassCode(String resetPassCode) {
		this.resetPassCode = resetPassCode;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
