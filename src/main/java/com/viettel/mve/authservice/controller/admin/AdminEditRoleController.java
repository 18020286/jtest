package com.viettel.mve.authservice.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.admin.AdminEditRoleService;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_ENTERPRISE_AC_MANAGER)
public class AdminEditRoleController extends BaseController {
	@Autowired
	private AdminEditRoleService adminEditRoleService;

	@RequestMapping(path = "/createRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> createRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminCreateRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateCreateRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = adminEditRoleService.createRole(userId, enterpriseId, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminUpdateRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateUpdateRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = adminEditRoleService.updateRole(userId, enterpriseId, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/deleteRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> deleteRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminDeleteRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminDeleteRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = adminEditRoleService.deleteRole(userId, enterpriseId, request);
			return responseOkStatus(response);
		}
	}
}
