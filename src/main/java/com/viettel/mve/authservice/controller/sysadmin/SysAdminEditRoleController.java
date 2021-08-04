package com.viettel.mve.authservice.controller.sysadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.sysadmin.SysAdminEditRoleService;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminCreateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_SYS_ADMIN_ROLE_MANAGER)
public class SysAdminEditRoleController extends BaseController {
	@Autowired
	private SysAdminEditRoleService sysAdminEditRoleService;

	@RequestMapping(path = "/createRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> createRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody SysAdminCreateRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateSysAdminCreateRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = sysAdminEditRoleService.createRole(userId, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody AdminUpdateRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateUpdateRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = sysAdminEditRoleService.updateRole(userId, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/deleteRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> deleteRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody AdminDeleteRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminDeleteRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = sysAdminEditRoleService.deleteRole(userId, request);
			return responseOkStatus(response);
		}
	}
}
