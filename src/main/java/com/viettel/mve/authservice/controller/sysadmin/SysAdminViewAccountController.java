package com.viettel.mve.authservice.controller.sysadmin;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.sysadmin.SysAdminViewAccountService;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.utils.MVEUtils;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_SYS_ADMIN_AC_MANAGER)
public class SysAdminViewAccountController extends BaseController {

	@Autowired
	private SysAdminViewAccountService viewAccountService;

	@RequestMapping(path = "/searchListAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> searchListAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody SysSearchListAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateSearchListAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(viewAccountService.searchListAccount(userId, request));
		}
	}
	
	@RequestMapping(path = "/exportExcelListAccount", method = RequestMethod.POST)
	public void exportExcelListAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestBody SysExportExcelListAccountRequest request,HttpServletResponse httpResponse) {
		viewAccountService.exportListAccount(userId, request,httpResponse);
	}

	@RequestMapping(path = "/viewAccountInfor", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> viewAccountInfor(
			@RequestHeader(name = JwtDefine.KEY_AUTH_TOKEN) String authToken,
			@RequestBody AdminViewAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminViewAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(viewAccountService.getAccountInfor(authToken, request));
		}
	}

	@RequestMapping(path = "/getListRoleByUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getListRoleByUser(@RequestBody AdminGetListRoleByUserRequest request) {
		return responseOkStatus(viewAccountService.getListRoleByUser(request));
	}

	@RequestMapping(path = "/getDetailRolesByUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getDetailRolesByUser(@RequestBody AdminGetListRoleByUserRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		return responseOkStatus(viewAccountService.getDetailRolesByUser(currentLang, request));
	}
	
	@RequestMapping(path = "/getListProvinceByAccountManagement", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getListProvinceByAccountManagement(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId) {
		return responseOkStatus(viewAccountService.getListProvinceByAccountManagement(userId));
	}

}
