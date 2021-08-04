package com.viettel.mve.authservice.service.admin;

import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface AdminViewRoleService {
	public BaseResponse getListRoles(long enterpriseId);

	public BaseResponse getAllPermission(String langKey);

	public BaseResponse getRoleDetail(String langKey, long enterpriseId, AdminGetRoleDetailRequest request);
	
	public BaseResponse getListRoleDetail(String langKey, long enterpriseId, AdminGetListRoleDetailRequest request);
}
