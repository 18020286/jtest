package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import com.viettel.mve.authservice.core.db.object.WrapObjectString;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "PERMISSION")
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "PermissionItemMapping", classes = {
				@ConstructorResult(targetClass = PermissionItem.class, columns = { @ColumnResult(name = "ID"),
						@ColumnResult(name = "PERMISSION_NAME") }) }),
		@SqlResultSetMapping(name = "PermissionStringMapping", classes = {
				@ConstructorResult(targetClass = WrapObjectString.class, columns = {
						@ColumnResult(name = "PERMISSION_CODE") }) }) })
public class Permission extends BaseEntity {

	public static final int PERMISSION_TYPE_ENTERPRISE = 0;
	public static final int PERMISSION_TYPE_ADMIN = 1;

	@Column(name = "PERMISSION_CODE")
	private String permissionCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "PERMISSION_TYPE")
	private int permissionType;

	@Column(name = "PARENT_PERMISSION")
	private BigInteger parent;

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getParent() {
		return parent;
	}

	public void setParent(BigInteger parent) {
		this.parent = parent;
	}

	public int getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(int permissionType) {
		this.permissionType = permissionType;
	}

}
