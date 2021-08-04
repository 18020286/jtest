package com.viettel.mve.authservice.service.sysadmin;

import javax.servlet.http.HttpServletResponse;

import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface SysAdminViewAccountService {
	public BaseResponse searchListAccount(long currentUser, SysSearchListAccountRequest request);
	public void exportListAccount(long currentUser, SysExportExcelListAccountRequest request,HttpServletResponse httpResponse);

	public BaseResponse getAccountInfor(String authToken, AdminViewAccountRequest request);

	public BaseResponse getListRoleByUser(AdminGetListRoleByUserRequest request);

	public BaseResponse getDetailRolesByUser(String langKey, AdminGetListRoleByUserRequest request);
	
	public BaseResponse getListProvinceByAccountManagement(long userId);

}
