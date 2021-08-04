package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "USER_ROLES")
public class UserRole extends BaseEntity {
	@Column(name = "ROLE_ID")
	private BigInteger roleId;

	@Column(name = "USER_ID")
	private BigInteger userId;

	public BigInteger getRoleId() {
		return roleId;
	}

	public void setRoleId(BigInteger roleId) {
		this.roleId = roleId;
	}

	public BigInteger getUserId() {
		return userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

}
