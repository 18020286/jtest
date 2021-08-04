package com.viettel.mve.authservice.controller.sysadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.service.sysadmin.SysAdminViewRoleService;
import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminGetListPermissionRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.utils.MVEUtils;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_SYS_ADMIN_ROLE_MANAGER)
public class SysAdminViewRoleController extends BaseController {

	@Autowired
	private SysAdminViewRoleService sysadminViewRoleService;

	@RequestMapping(path = "/getListPermission", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getListPermission(@RequestBody SysAdminGetListPermissionRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = sysadminViewRoleService.getListPermission(currentLang, request);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/searchListRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> searchListRole(@RequestBody SysAdminSearchRoleRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		return responseOkStatus(sysadminViewRoleService.searchListRole(request, currentLang));
	}

	@RequestMapping(path = "/getListRole", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getListRole() {
		BaseResponse response = sysadminViewRoleService.getListRoles();
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/getRoleDetail", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getRoleDetail(@RequestBody AdminGetRoleDetailRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = sysadminViewRoleService.getRoleDetail(currentLang, request);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/getListRoleForCreateAccount", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getListRoleForCreateAccount(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId) {
		return responseOkStatus(sysadminViewRoleService.getListRoleForCreateAccount(userId));
	}

	@RequestMapping(path = "/getListRoleForViewAccount", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse> getListRoleForViewAccount(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId) {
		return responseOkStatus(sysadminViewRoleService.getListRoleForViewAccount(userId));
	}

	@RequestMapping(path = "/getListRoleDetail", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getListRoleDetail(
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long enterpriseId,
			@RequestBody AdminGetListRoleDetailRequest request) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		BaseResponse response = sysadminViewRoleService.getListRoleDetail(currentLang, enterpriseId, request);
		return responseOkStatus(response);
	}

}
