package com.viettel.mve.authservice.service.common;

import java.util.Date;

import com.viettel.mve.client.request.auth.ValidateRegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.client.request.auth.ChangePasswordRequest;
import com.viettel.mve.client.request.auth.RegisterAccountRequest;
import com.viettel.mve.client.request.auth.UpdateRequiredInforRequest;
import com.viettel.mve.client.request.auth.object.ModifyEnterpriseInfor;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.intercomm.request.CheckRequireLoginRequest;

public interface AccountService {
	BaseResponse changePassword(long updateUser, long userId, ChangePasswordRequest request);

	BaseResponse updateRequiredInfor(long userId, UpdateRequiredInforRequest request, long updateUser);

	BaseResponse forgetAccount(String email);

	//BaseResponse registerAccount(RegisterAccountRequest accountInforRequest, MultipartFile regisForm,
	//		MultipartFile bussinessLicense, MultipartFile personalCard);

	void setLastModifyDate(long userId, Date lastModify);

	BaseResponse checkRequireLogin(CheckRequireLoginRequest request);
	
	void sendNotifyNewRegisEmail(ModifyEnterpriseInfor enterpriseInfor, RegisterAccountInfor accountInfor);
	
	BaseResponse registerAccountV2(RegisterAccountRequest accountInforRequest, MultipartFile[] regisForms,
			MultipartFile[] bussinessLicenses, MultipartFile[] personalCards, MultipartFile[] other);

	BaseResponse validateRegisterInfo(ValidateRegisterRequest request);

	public BaseResponse createEnterprise(ModifyEnterpriseInfor enterpriseInfor);
	
	public MVEUser createUserFromRegisterRequest(RegisterAccountInfor accountInfor);
	
	void sendNotifyImportAccountEmail(ModifyEnterpriseInfor enterpriseInfor, RegisterAccountInfor accountInfor, String password);
}
