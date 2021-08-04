package com.viettel.mve.authservice.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.admin.AdminViewAccountService;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_ENTERPRISE_AC_MANAGER)
public class AdminViewAccountController extends BaseController {

	@Autowired
	private AdminViewAccountService viewAccountService;

	@RequestMapping(path = "/searchListAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> searchListAccount(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestHeader(name = JwtDefine.KEY_USER_NAME) String userName,
			@RequestHeader(name = JwtDefine.KEY_AUTH_TOKEN) String token,
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId, @RequestBody AdminGetListAccountRequest request) {
		BaseResponse response = viewAccountService.searchListAccount(token, enterpriseId, userId, userName, request);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/viewAccountInfor", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> viewAccountInfor(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestHeader(name = JwtDefine.KEY_USER_NAME) String userName,
			@RequestHeader(name = JwtDefine.KEY_AUTH_TOKEN) String token,
			@RequestBody AdminViewAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminViewAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = viewAccountService.getAccountInfor(token, enterpriseId, userName, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/getListRoleByUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getListRoleByUser(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminGetListRoleByUserRequest request) {
		return responseOkStatus(viewAccountService.getListRoleByUser(enterpriseId, request));
	}

}
