package com.viettel.mve.authservice.service.sysadmin;

import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminCreateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface SysAdminEditRoleService {
	public BaseResponse createRole(long createUser, SysAdminCreateRoleRequest request);

	public BaseResponse updateRole(long updateUser, AdminUpdateRoleRequest request);

	public BaseResponse deleteRole(long updateUser, AdminDeleteRoleRequest request);
}
