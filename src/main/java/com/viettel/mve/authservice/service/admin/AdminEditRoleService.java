package com.viettel.mve.authservice.service.admin;

import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface AdminEditRoleService {
	public BaseResponse createRole(long createUser, long enterpriseId, AdminCreateRoleRequest request);

	public BaseResponse updateRole(long updateUser, long enterpriseId, AdminUpdateRoleRequest request);
	
	public BaseResponse deleteRole(long updateUser, long enterpriseId, AdminDeleteRoleRequest request);
}
