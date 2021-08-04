package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "ROLE_PERMISSIONS")
public class RolePermission extends BaseEntity {
	@Column(name = "ROLE_ID")
	private BigInteger roleId;

//	@OneToOne
//	@JoinColumn(name = "PERMISSION_ID", nullable = true)
//	private Permission permission;

	@Column(name = "PERMISSION_ID")
	private BigInteger permissionId;

	public BigInteger getRoleId() {
		return roleId;
	}

	public void setRoleId(BigInteger roleId) {
		this.roleId = roleId;
	}

	public BigInteger getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(BigInteger permissionId) {
		this.permissionId = permissionId;
	}

}
