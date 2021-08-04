package com.viettel.mve.authservice.controller.sysadmin;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.service.sysadmin.SysAdminEditAccountService;
import com.viettel.mve.client.constant.GlobalConstant.AccountFileType;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.GetUserRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminUpdateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysApproveAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysCreateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysUpdateRegisUserRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.constant.URLPrefixDefine;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.JsonUtils;

@RestController
@RequestMapping(URLPrefixDefine.PREFIX_SYS_ADMIN_AC_MANAGER)
public class SysAdminEditAccountController extends BaseController {

	@Autowired
	private SysAdminEditAccountService editAccountService;
	
	@Autowired
	private ConfigValue configValue;


	@RequestMapping(path = "/createAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> createAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestParam(name = "avartar", required = false) MultipartFile avartar,
			@RequestParam(name = "files", required = false) MultipartFile[] files,
			@RequestParam("accountInfor") String accountInfor) {
		SysCreateAccountRequest accountInforRequest = JsonUtils.fromJson(accountInfor, SysCreateAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateCreateAccountRequest(accountInforRequest, files);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.createAccount(userId, accountInforRequest, files, avartar);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateAccountInformation", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAccountInformation(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) String uid,
			@RequestParam(name = "files", required = false) MultipartFile[] files,
			@RequestParam("accountInfor") String accountInfor) {
		SysAdminUpdateAccountRequest accountInforRequest = JsonUtils.fromJson(accountInfor,
				SysAdminUpdateAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateAdminUpdateAccountRequest(accountInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long userId = Long.parseLong(uid);
			response = editAccountService.updateAccount(userId, accountInforRequest, files);
			return responseOkStatus(response);
		}
	}
	
	@RequestMapping(path = "/v2/updateAccountInformation", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAccountInformationV2(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long userId,
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile[] regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile[] bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile[] personalCard,
			@RequestParam(name = AccountFileType.OTHER_FILE, required = false) MultipartFile[] other,
			@RequestParam("accountInfor") String accountInfor) {
		SysAdminUpdateAccountRequest accountInforRequest = JsonUtils.fromJson(accountInfor,
				SysAdminUpdateAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateAdminUpdateAccountRequest(accountInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateAccountV2(userId, accountInforRequest,
					regisForm, bussinessLicense, personalCard, other);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/updateAvartar", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateAvartar(@RequestHeader(name = JwtDefine.KEY_USER_ID) String uid,
			@RequestParam(name = "avartar", required = false) MultipartFile avartar,
			@RequestParam("userId") long userId) {
		BaseResponse response = AuthValidateUtils.validateUpdateAvartarRequest(userId, avartar);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long updateUser = Long.parseLong(uid);
			response = editAccountService.updateAvartar(updateUser, userId, avartar);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> resetPassword(@RequestHeader(name = JwtDefine.KEY_USER_ID) String uid,
			@RequestBody AdminResetPassRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminResetPassRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long userId = Long.parseLong(uid);
			return responseOkStatus(editAccountService.resetPassword(userId, request));
		}
	}

	@RequestMapping(path = "/updateStatus", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateStatus(@RequestHeader(name = JwtDefine.KEY_USER_ID) String uid,
			@RequestBody AdminUpdateUserStatusRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminUpdateUserStatusRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long userId = Long.parseLong(uid);
			return responseOkStatus(editAccountService.updateStatus(userId, request));
		}
	}

	@RequestMapping(path = "/deleteAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> deleteAccount(@RequestHeader(name = JwtDefine.KEY_USER_ID) String uid,
			@RequestBody AdminDeleteAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminDeleteAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long userId = Long.parseLong(uid);
			return responseOkStatus(editAccountService.deleteAccount(userId, request));
		}
	}

	@RequestMapping(path = "/updateUserRole", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateUserRole(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestBody AdminUpdateUserRoleRequest request) {
		BaseResponse response = AuthValidateUtils.validateAdminUpdateUserRoleRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateUserRole(updateUser, request);
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/getApproveUserInfor", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getApproveUserInfor(
			@RequestHeader(name = JwtDefine.KEY_AUTH_TOKEN) String authToken, @RequestBody GetUserRequest request) {
		return responseOkStatus(editAccountService.getApproveUserInfor(authToken, request));
	}

	/**
	@RequestMapping(path = "/approveUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> approveUser(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile personalCard,
			@RequestParam("approveInfor") String approveInfor) {
		SysApproveAccountRequest accountInforRequest = JsonUtils.fromJson(approveInfor, SysApproveAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateSysApproveAccountRequest(accountInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.approveUser(updateUser, regisForm, bussinessLicense, personalCard,
					accountInforRequest);
			return responseOkStatus(response);
		}
	}**/

	/**
	@RequestMapping(path = "/updateRegisterInforUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateRegisterInforUser(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile personalCard,
			@RequestParam("updateInfor") String updateInfor) {
		SysUpdateRegisUserRequest updateInforRequest = JsonUtils.fromJson(updateInfor, SysUpdateRegisUserRequest.class);
		BaseResponse response = AuthValidateUtils.validateSysUpdateRegisUserRequest(updateInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = editAccountService.updateRegisterInforUser(updateUser, regisForm, bussinessLicense, personalCard,
					updateInforRequest);
			return responseOkStatus(response);
		}
	}**/
	
	@RequestMapping(path = "/v2/updateRegisterInforUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> updateRegisterInforUserV2(
			@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile[] regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile[] bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile[] personalCard,
			@RequestParam(name = AccountFileType.OTHER_FILE, required = false) MultipartFile[] other,
			@RequestParam(name = "deletedFile", required = false) String deletedFile,
			@RequestParam("updateInfor") String updateInfor) {
		SysUpdateRegisUserRequest updateInforRequest = JsonUtils.fromJson(updateInfor, SysUpdateRegisUserRequest.class);
		BaseResponse response = AuthValidateUtils.validateSysUpdateRegisUserRequest(updateInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long[] deletedMedia = null;
			if(!StringUtility.isNullOrEmpty(deletedFile) && deletedFile.split(",").length > 0) {
				deletedMedia = Arrays.stream(deletedFile.split(",")).mapToLong(Long::parseLong).toArray();
			}
			response = editAccountService.updateRegisterInforUserV2(updateUser, regisForm, bussinessLicense, 
					personalCard, other, updateInforRequest, deletedMedia);
			return responseOkStatus(response);
		}
	}
	
	@RequestMapping(path = "/v2/approveUser", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> approveUserV2(@RequestHeader(name = JwtDefine.KEY_USER_ID) long updateUser,
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile[] regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile[] bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile[] personalCard,
			@RequestParam(name = AccountFileType.OTHER_FILE, required = false) MultipartFile[] other,
			@RequestParam(name = "deletedFile", required = false) String deletedFile,
			@RequestParam("approveInfor") String approveInfor) {
		SysApproveAccountRequest accountInforRequest = JsonUtils.fromJson(approveInfor, SysApproveAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateSysApproveAccountRequest(accountInforRequest);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			long[] deletedMedia = null;
			if(!StringUtility.isNullOrEmpty(deletedFile) && deletedFile.split(",").length > 0) {
				deletedMedia = Arrays.stream(deletedFile.split(",")).mapToLong(Long::parseLong).toArray();
			}
			response = editAccountService.approveUserV2(updateUser, regisForm, bussinessLicense,
					personalCard, other,accountInforRequest,  deletedMedia);
			return responseOkStatus(response);
		}
	}


    @GetMapping(path = "/download-template")
    public ResponseEntity<InputStreamResource> downloadTemplate(HttpServletRequest req)
            throws IOException, Exception {
        return editAccountService.downloadTemplateImportAccount(configValue);
    }

    @RequestMapping(path = "/import-account", method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> importAccount(
            @RequestHeader(name = JwtDefine.KEY_USER_ID, required = false) long updateUser,
            @RequestParam(name = "importFile") MultipartFile importFile) throws IOException, Exception {
        return editAccountService.actionImportAccount(updateUser, importFile,configValue);
    }
}
