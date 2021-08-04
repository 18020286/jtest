package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "ACCOUNT_PROVINCE_LIMIT")
public class AccountProvinceLimit extends BaseEntity {
	@Column(name = "USER_ID")
	private BigInteger userId;

	@Column(name = "PROVINCE_CODE")
	private String provinceCode;

	public BigInteger getUserId() {
		return userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

}
