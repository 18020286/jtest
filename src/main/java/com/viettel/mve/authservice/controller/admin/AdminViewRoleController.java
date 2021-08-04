package com.viettel.mve.authservice.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.service.admin.AdminViewRoleService;
import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.utils.MVEUtils;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_ENTERPRISE_AC_MANAGER)
public class AdminViewRoleController extends BaseController {

	@Autowired
	protected AdminViewRoleService adminViewRoleService;

	@RequestMapping(path = "/getAllPermission", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getAllPermission() {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = adminViewRoleService.getAllPermission(currentLang);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/getListRole", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getListRole(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId) {
		BaseResponse response = adminViewRoleService.getListRoles(enterpriseId);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/getRoleDetail", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getRoleDetail(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminGetRoleDetailRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = adminViewRoleService.getRoleDetail(currentLang, enterpriseId, request);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/getListRoleDetail", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getListRoleDetail(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminGetListRoleDetailRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = adminViewRoleService.getListRoleDetail(currentLang, enterpriseId, request);
		return responseOkStatus(response);
	}

}
