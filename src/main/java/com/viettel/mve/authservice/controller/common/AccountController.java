package com.viettel.mve.authservice.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.authservice.service.sysadmin.SysAdminEditAccountService;
import com.viettel.mve.client.request.auth.ChangePasswordRequest;
import com.viettel.mve.client.request.auth.UpdateRequiredInforRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.spring.BaseController;

@RestController
public class AccountController extends BaseController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private SysAdminEditAccountService editAccountService;

	@RequestMapping(path = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> changePassword(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody ChangePasswordRequest request) {
		BaseResponse response = AuthValidateUtils.validateChangePasswordRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = accountService.changePassword(userId, userId, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateRequiredInfor", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateRequiredInfor(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody UpdateRequiredInforRequest request) {
		BaseResponse response = AuthValidateUtils.validateUpdateRequiredInforRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = accountService.updateRequiredInfor(userId, request, userId);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateAvartar", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAvartar(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestParam(name = "avartar", required = false) MultipartFile avartar) {
		BaseResponse response = AuthValidateUtils.validateUpdateAvartarRequest(userId, avartar);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateAvartar(userId, userId, avartar);
			return responseOkStatus(response);
		}
	}
}
