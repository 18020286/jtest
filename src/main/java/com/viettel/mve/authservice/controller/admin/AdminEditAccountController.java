package com.viettel.mve.authservice.controller.admin;

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
import com.viettel.mve.authservice.service.admin.AdminEditAccountService;
import com.viettel.mve.client.request.auth.AdminCreateAccountRequest;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.UpdateAccountRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.utils.JsonUtils;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_ENTERPRISE_AC_MANAGER)
public class AdminEditAccountController extends BaseController {

	@Autowired
	private AdminEditAccountService editAccountService;

	@RequestMapping(path = "/createAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> createAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) long uid,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid,
			@RequestParam(name = "avartar", required = false) MultipartFile avartar,
			@RequestParam("accountInfor") String accountInfor) {
		AdminCreateAccountRequest request = JsonUtils.fromJson(accountInfor, AdminCreateAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateAdminCreateAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.createAccount(uid, eid, request, avartar);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateAccountInformation", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAccountInformation(@RequestHeader(name = JwtDefine.KEY_USER_ID) long uid,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid, @RequestBody UpdateAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateUpdateAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateAccount(uid, eid, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateAvartar", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAvartar(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid,
			@RequestParam(name = "avartar", required = false) MultipartFile avartar,
			@RequestParam("userId") long userId) {
		BaseResponse response = AuthValidateUtils.validateUpdateAvartarRequest(userId, avartar);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateAvartar(updateUser, userId, eid, avartar);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> resetPassword(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid, @RequestBody AdminResetPassRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminResetPassRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(editAccountService.resetPassword(updateUser, eid, request));
		}
	}

	@RequestMapping(path = "/updateStatus", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateStatus(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid,
			@RequestBody AdminUpdateUserStatusRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminUpdateUserStatusRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(editAccountService.updateStatus(updateUser, eid, request));
		}
	}

	@RequestMapping(path = "/updateUserRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateUserRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid,
			@RequestBody AdminUpdateUserRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminUpdateUserRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateUserRole(updateUser, eid, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/deleteAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> deleteAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long eid,
			@RequestBody AdminDeleteAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminDeleteAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(editAccountService.deleteAccount(updateUser, eid, request));
		}
	}

}
