package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import com.viettel.mve.authservice.core.db.object.WrapObjectString;
import com.viettel.mve.client.response.auth.object.RoleItem;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;
import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "ROLE")
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "RoleItemMapping", classes = {
				@ConstructorResult(targetClass = RoleItem.class, columns = { @ColumnResult(name = "ID"),
						@ColumnResult(name = "ROLE_NAME"), @ColumnResult(name = "DESCRIPTION"),
						@ColumnResult(name = "IS_SYSTEM") }) }),
		@SqlResultSetMapping(name = "SysAdminRoleItemMapping", classes = {
				@ConstructorResult(targetClass = SysAdminRoleItem.class, columns = { @ColumnResult(name = "ID"),
						@ColumnResult(name = "ROLE_NAME"), @ColumnResult(name = "USER_NAME"),
						@ColumnResult(name = "PERMISSIONS_NAME"), @ColumnResult(name = "DESCRIPTION"),
						@ColumnResult(name = "IS_SYSTEM") }) }),
		@SqlResultSetMapping(name = "RoleStringMapping", classes = {
				@ConstructorResult(targetClass = WrapObjectString.class, columns = {
						@ColumnResult(name = "ROLES") }) }) })
@NamedNativeQueries({
		@NamedNativeQuery(name = "Role.getRolesByEnterprise", resultSetMapping = "RoleItemMapping", query = "SELECT ID, ROLE_NAME, DESCRIPTION, IS_SYSTEM  "
				+ "FROM role where ((BUSINESS_ID = ?1 and IS_SYSTEM = 0) or (IS_SYSTEM = 1 and IS_VISIBILITY = 1)) and ROLE_TYPE = 0 and is_del = 0 "),
		@NamedNativeQuery(name = "Role.getRoleByID", resultSetMapping = "RoleItemMapping", query = "SELECT ID, ROLE_NAME, DESCRIPTION, IS_SYSTEM  "
				+ "FROM role where ID = ?1 and is_del = 0 and (IS_SYSTEM = 0 or (IS_SYSTEM = 1 and IS_VISIBILITY = 1)) "),
		@NamedNativeQuery(name = "Role.getListSysAdminRole", resultSetMapping = "RoleItemMapping", query = "SELECT ID, ROLE_NAME, DESCRIPTION, IS_SYSTEM  "
				+ "FROM role where is_del = 0 and ROLE_TYPE = 1 and (IS_SYSTEM = 0 or (IS_SYSTEM = 1 and IS_VISIBILITY = 1)) "),
		@NamedNativeQuery(name = "Role.getRoleByIDS", resultSetMapping = "RoleItemMapping", query = "SELECT ID, ROLE_NAME, DESCRIPTION, IS_SYSTEM  "
				+ "FROM role where ID in ?1 and is_del = 0") })
public class Role extends BaseEntity {

	public static final int ROLE_TYPE_ENTERPRISE = 0;
	public static final int ROLE_TYPE_ADMIN = 1;

	@Column(name = "ROLE_CODE")
	private String roleCode;

	@Column(name = "ROLE_NAME")
	private String roleName;

	@Column(name = "ROLE_NAME_SEARCH")
	private String roleNameSearch;

	@Column(name = "ROLE_TYPE")
	private int roleType;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "BUSINESS_ID")
	private BigInteger businessId;

	@Column(name = "IS_SYSTEM")
	private int isSystem;

	@Column(name = "IS_VISIBILITY")
	private int isVisibility;

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getBusinessId() {
		return businessId;
	}

	public void setBusinessId(BigInteger businessId) {
		this.businessId = businessId;
	}

	public int getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(int isSystem) {
		this.isSystem = isSystem;
	}

	public int getIsVisibility() {
		return isVisibility;
	}

	public void setIsVisibility(int isVisibility) {
		this.isVisibility = isVisibility;
	}

	public String getRoleNameSearch() {
		return roleNameSearch;
	}

	public void setRoleNameSearch(String roleNameSearch) {
		this.roleNameSearch = roleNameSearch;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

}
