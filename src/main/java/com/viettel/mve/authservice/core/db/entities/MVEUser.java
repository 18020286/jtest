package com.viettel.mve.authservice.core.db.entities;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.client.response.auth.object.ListAccountItem;
import com.viettel.mve.common.base.entities.BaseEntity;

@Entity(name = "USER")
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "ListAccountItemMapping", classes = {
				@ConstructorResult(targetClass = ListAccountItem.class, columns = { @ColumnResult(name = "ID"),
						@ColumnResult(name = "BUSINESS_ID"),@ColumnResult(name = "USER_NAME"), @ColumnResult(name = "FULL_NAME"),
						@ColumnResult(name = "CREATED_DATE"), @ColumnResult(name = "UPDATED_DATE"),
						@ColumnResult(name = "APPROVED_DATE"), @ColumnResult(name = "REJECT_REASON"),
						@ColumnResult(name = "STATUS"), @ColumnResult(name = "ACCOUNT_TYPE"),
						@ColumnResult(name = "CREATED_USER_NAME") }) }),
		@SqlResultSetMapping(name = "AdminListAccountItemMapping", classes = {
				@ConstructorResult(targetClass = AdminListAccountItem.class, columns = { @ColumnResult(name = "ID"),
						@ColumnResult(name = "USER_NAME"), @ColumnResult(name = "FULL_NAME"),
						@ColumnResult(name = "PERSIONAL_ID"), @ColumnResult(name = "CREATED_DATE"),
						@ColumnResult(name = "STATUS"), @ColumnResult(name = "PHONE") }) }) })
public class MVEUser extends BaseEntity {
	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "FULL_NAME")
	private String fullName;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "POSITION")
	private String position;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "BIRTHDAY")
	private Date birthday;

	@Column(name = "AVARTAR")
	private String avartar;

	@Column(name = "BUSINESS_ID")
	private BigInteger businessId;

	@Column(name = "PERSIONAL_ID")
	private String personalId;

	@Column(name = "PERSIONAL_AREA")
	private String personalArea;

	@Column(name = "PERSIONAL_DATE")
	private String personalDate;

	@Column(name = "STATUS")
	private int status;

	@Column(name = "APPROVE_USER")
	private BigInteger approveUser;

	@Column(name = "IS_SEND_APPROVE_EMAIL")
	private int isSendApproveEmail;

	@Column(name = "REJECT_REASON")
	private String rejectReason;
	
	@Column(name = "APPROVED_DATE")
	private Date approvedDate;

	@Column(name = "SEX")
	private int sex;

	@Column(name = "IS_SYSTEM")
	private int isSystem;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getAvartar() {
		return avartar;
	}

	public void setAvartar(String avartar) {
		this.avartar = avartar;
	}

	public BigInteger getBusinessId() {
		return businessId;
	}

	public void setBusinessId(BigInteger businessId) {
		this.businessId = businessId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(int isSystem) {
		this.isSystem = isSystem;
	}

	public String getPersonalArea() {
		return personalArea;
	}

	public void setPersonalArea(String personalArea) {
		this.personalArea = personalArea;
	}

	public String getPersonalDate() {
		return personalDate;
	}

	public void setPersonalDate(String personalDate) {
		this.personalDate = personalDate;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public BigInteger getApproveUser() {
		return approveUser;
	}

	public void setApproveUser(BigInteger approveUser) {
		this.approveUser = approveUser;
	}

	public int getIsSendApproveEmail() {
		return isSendApproveEmail;
	}

	public void setIsSendApproveEmail(int isSendApproveEmail) {
		this.isSendApproveEmail = isSendApproveEmail;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	
}
