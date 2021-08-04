package com.viettel.mve.authservice.service.sysadmin;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.GetUserRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminUpdateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysApproveAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysCreateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysUpdateRegisUserRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface SysAdminEditAccountService {
	public BaseResponse createAccount(long updateUser, SysCreateAccountRequest request, MultipartFile[] files,
			MultipartFile avartar);

	public BaseResponse resetPassword(long updateUser, AdminResetPassRequest request);

	public BaseResponse updateStatus(long updateUser, AdminUpdateUserStatusRequest request);

	public BaseResponse deleteAccount(long updateUser, AdminDeleteAccountRequest request);

	public BaseResponse updateAccount(long updateUser, SysAdminUpdateAccountRequest request, MultipartFile[] files);

	public BaseResponse updateAvartar(long updateUser, long userId, MultipartFile avartar);

	public BaseResponse updateUserRole(long updateUser, AdminUpdateUserRoleRequest request);

	public BaseResponse getApproveUserInfor(String authToken, GetUserRequest request);

	//public BaseResponse approveUser(long approveUser, MultipartFile regisForm, MultipartFile bussinessLicense,
	//		MultipartFile personalCard, SysApproveAccountRequest request);
	
	//public BaseResponse updateRegisterInforUser(long updateUser, MultipartFile regisForm, MultipartFile bussinessLicense,
	//		MultipartFile personalCard, SysUpdateRegisUserRequest request);
	
	public BaseResponse updateRegisterInforUserV2(long updateUser, MultipartFile[] regisForm, 
			MultipartFile[] bussinessLicense, MultipartFile[] personalCard, 
			MultipartFile[] other, SysUpdateRegisUserRequest request, long[] deletedMedia);
	
	public BaseResponse approveUserV2(long approveUser, MultipartFile[] regisForm, 
			MultipartFile[] bussinessLicense, MultipartFile[] personalCard, 
			MultipartFile[] other, SysApproveAccountRequest request, long[] deletedMedia);
	
	public BaseResponse updateAccountV2(long updateUser, SysAdminUpdateAccountRequest request, MultipartFile[] regisForm, 
			MultipartFile[] bussinessLicense, MultipartFile[] personalCard, 
			MultipartFile[] other);


    public ResponseEntity<InputStreamResource> downloadTemplateImportAccount(ConfigValue configValue) throws IOException, Exception;

    public ResponseEntity<InputStreamResource> actionImportAccount(long updateUser, MultipartFile fileImport, ConfigValue configValue) throws IOException, Exception;
}
