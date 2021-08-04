package com.viettel.mve.authservice.service.admin;

import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.client.request.auth.AdminCreateAccountRequest;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.UpdateAccountRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface AdminEditAccountService {
	public BaseResponse createAccount(long updateUser, long enterpriseId, AdminCreateAccountRequest request,
			MultipartFile avartar);

	public BaseResponse updateAccount(long updateUser, long enterpriseId, UpdateAccountRequest request);

	public BaseResponse updateAvartar(long updateUser, long userId, long enterpriseId, MultipartFile avartar);

	public BaseResponse resetPassword(long updateUser, long enterpriseId, AdminResetPassRequest request);

	public BaseResponse updateStatus(long updateUser, long enterpriseId, AdminUpdateUserStatusRequest request);
	
	public BaseResponse updateUserRole(long updateUser, long enterpriseId, AdminUpdateUserRoleRequest request);
	
	public BaseResponse deleteAccount(long updateUser, long enterpriseId, AdminDeleteAccountRequest request);

}
