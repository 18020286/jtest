package com.viettel.mve.authservice.service.sysadmin;

import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminGetListPermissionRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface SysAdminViewRoleService {
	public BaseResponse searchListRole(SysAdminSearchRoleRequest request, String langKey);

	public BaseResponse getListPermission(String langKey, SysAdminGetListPermissionRequest request);

	public BaseResponse getRoleDetail(String langKey, AdminGetRoleDetailRequest request);

	public BaseResponse getListRoleForCreateAccount(long currentUser);

	public BaseResponse getListRoleForViewAccount(long currentUser);

	public BaseResponse getListRoles();
	
	public BaseResponse getListRoleDetail(String langKey, long enterpriseId, AdminGetListRoleDetailRequest request);

}
