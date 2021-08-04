package com.viettel.mve.authservice.service.admin;

import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface AdminViewAccountService {
	public BaseResponse searchListAccount(String token, long enterpriseId, long currentUser, String currentUserName,
			AdminGetListAccountRequest request);

	public BaseResponse getAccountInfor(String token, long enterpriseId, String currentUserName,
			AdminViewAccountRequest request);

	public BaseResponse getListRoleByUser(long enterpriseId, AdminGetListRoleByUserRequest request);

}
