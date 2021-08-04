package com.viettel.mve.authservice.common;

import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.AdminCreateAccountRequest;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.ChangePasswordRequest;
import com.viettel.mve.client.request.auth.ForgetAccountRequest;
import com.viettel.mve.client.request.auth.GetResetPassCodeRequest;
import com.viettel.mve.client.request.auth.LoginRequest;
import com.viettel.mve.client.request.auth.RegisterAccountRequest;
import com.viettel.mve.client.request.auth.ResetPassRequest;
import com.viettel.mve.client.request.auth.UpdateAccountRequest;
import com.viettel.mve.client.request.auth.UpdateRequiredInforRequest;
import com.viettel.mve.client.request.auth.object.ModifyEnterpriseInfor;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.client.request.auth.object.UserStatus;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminUpdateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysApproveAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysCreateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysUpdateRegisUserRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.ValidateUtils;

public class AuthValidateUtils {
	public static BaseResponse validateChangePasswordRequest(ChangePasswordRequest request) {
		if (StringUtility.isNullOrEmpty(request.getOldPassword())
				|| StringUtility.isNullOrEmpty(request.getNewPassword())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateCreateAccountRequest(SysCreateAccountRequest request, MultipartFile[] files) {
		if (StringUtility.isNullOrEmpty(request.getUsername())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(request.getFullName())) {
			return ResponseDefine.responseInvalidError();
		}
		if (request.getRoleId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		/*
		 * if (StringUtility.isNullOrEmpty(request.getPersonalId())) { return
		 * ResponseDefine.responseInvalidError(); }
		 */
		if (StringUtility.isNullOrEmpty(request.getEmail())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(request.getPhone())) {
			return ResponseDefine.responseInvalidError();
		}
		if (request.getRoleId() == RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId()
				&& StringUtility.isNullOrEmpty(request.getEnterpriseIdentify())) {
			return ResponseDefine.responseInvalidError();
		}

		if (!ValidateUtils.checkOptionDateFormatIsValid(request.getBirthday(), DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}

		if (request.getRoleId() == RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId()
				&& (files == null || files.length == 0)) {
			return ResponseDefine.responseInvalidError("Missing attact file");
		}
		return null;
	}

	public static BaseResponse validateUpdateRequiredInforRequest(UpdateRequiredInforRequest request) {
		if (StringUtility.isNullOrEmpty(request.getEmail())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateLoginRequest(LoginRequest request) {
		if (StringUtility.isNullOrEmpty(request.getUsername()) || StringUtility.isNullOrEmpty(request.getPassword())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateGetResetPassCodeRequest(GetResetPassCodeRequest request) {
		if (StringUtility.isNullOrEmpty(request.getUsername()) || StringUtility.isNullOrEmpty(request.getEmail())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateResetPassRequest(ResetPassRequest request) {
		if (StringUtility.isNullOrEmpty(request.getCode()) || StringUtility.isNullOrEmpty(request.getNewPassword())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateForgetAccountRequest(ForgetAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getEmail())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateAdminResetPassRequest(AdminResetPassRequest request) {
		if (request.getUserId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateAdminUpdateUserStatusRequest(AdminUpdateUserStatusRequest request) {
		if (request.getLstUserStatus() == null || request.getLstUserStatus().isEmpty()) {
			return ResponseDefine.responseInvalidError();
		}
		for (UserStatus item : request.getLstUserStatus()) {
			if (item.getUserId() <= 0) {
				return ResponseDefine.responseInvalidError();
			}
		}

		return null;
	}

	public static BaseResponse validateAdminDeleteAccountRequest(AdminDeleteAccountRequest request) {
		if (request.getUserId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateAdminViewAccountRequest(AdminViewAccountRequest request) {
		if (request.getUserId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateAdminUpdateAccountRequest(SysAdminUpdateAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getAccountInformation().getFullName())
				/*
				 * ||
				 * StringUtility.isNullOrEmpty(request.getAccountInformation().getPersonalId())
				 */
				|| StringUtility.isNullOrEmpty(request.getAccountInformation().getEmail())
				|| StringUtility.isNullOrEmpty(request.getAccountInformation().getPhone())) {
			return ResponseDefine.responseInvalidError();
		}
		String birthday = request.getAccountInformation().getBirthday();
		if (!ValidateUtils.checkOptionDateFormatIsValid(birthday, DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}

		return null;
	}

	public static BaseResponse validateUpdateAvartarRequest(long userId, MultipartFile file) {
		if (file == null) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateSearchListAccountRequest(SysSearchListAccountRequest request) {
		if (!ValidateUtils.checkRequiredDateFormatIsValid(request.getFromDate(), DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}
		if (!ValidateUtils.checkRequiredDateFormatIsValid(request.getToDate(), DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}
		return null;
	}

	public static BaseResponse validateSearchListAccountRequest(SysExportExcelListAccountRequest request) {
		if (!ValidateUtils.checkRequiredDateFormatIsValid(request.getFromDate(), DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}
		if (!ValidateUtils.checkRequiredDateFormatIsValid(request.getToDate(), DateUtility.DATE_FORMAT_STR)) {
			return ResponseDefine.responseInvalidDateFormatError();
		}
		return null;
	}

	public static BaseResponse validateCreateRoleRequest(AdminCreateRoleRequest request) {
		if (StringUtility.isNullOrEmpty(request.getRoleName())) {
			return ResponseDefine.responseInvalidError();
		}
		if (request.getPermissions() == null || request.getPermissions().isEmpty()) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateUpdateRoleRequest(AdminUpdateRoleRequest request) {
		if (request.getRoleId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		BaseResponse response = validateCreateRoleRequest(request);
		if (response != null) {
			return response;
		}
		return null;
	}

	public static BaseResponse validateAdminDeleteRoleRequest(AdminDeleteRoleRequest request) {
		if (request.getRoleId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateAdminCreateAccountRequest(AdminCreateAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getUsername())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(request.getFullName())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(request.getPhone())) {
			return ResponseDefine.responseInvalidError();
		}
		/*
		 * if (StringUtility.isNullOrEmpty(request.getPersonalId())) { return
		 * ResponseDefine.responseInvalidError(); }
		 */
		return null;
	}

	public static BaseResponse validateUpdateAccountRequest(UpdateAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getFullName())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(request.getPhone())) {
			return ResponseDefine.responseInvalidError();
		}
		/*
		 * if (StringUtility.isNullOrEmpty(request.getPersonalId())) { return
		 * ResponseDefine.responseInvalidError(); }
		 */
		return null;
	}

	public static BaseResponse validateAdminUpdateUserRoleRequest(AdminUpdateUserRoleRequest request) {
		if (request.getUserId() <= 0) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateSysAdminCreateRoleRequest(SysAdminCreateRoleRequest request) {
		if (request.getRoleType() != Role.ROLE_TYPE_ADMIN && request.getRoleType() != Role.ROLE_TYPE_ENTERPRISE) {
			return ResponseDefine.responseInvalidError();
		}

		BaseResponse rs = validateCreateRoleRequest(request);
		if (rs != null) {
			return rs;
		}
		return null;
	}

	private static BaseResponse validateModifyEnterpriseInfor(ModifyEnterpriseInfor enterpriseInfor) {
		if (StringUtility.isNullOrEmpty(enterpriseInfor.getEnterpriseName())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(enterpriseInfor.getProvinceCode())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(enterpriseInfor.getDistrictCode())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(enterpriseInfor.getTownCode())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(enterpriseInfor.getStreetNumber())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	private static BaseResponse validateRegisterAccountInfor(RegisterAccountInfor accountInfor) {
		if (StringUtility.isNullOrEmpty(accountInfor.getUsername())) {
			return ResponseDefine.responseInvalidError();
		}
		return validateModifyAccountInfor(accountInfor);
	}

	private static BaseResponse validateModifyAccountInfor(RegisterAccountInfor accountInfor) {
		if (StringUtility.isNullOrEmpty(accountInfor.getFullName())) {
			return ResponseDefine.responseInvalidError();
		}
		/*
		 * if (StringUtility.isNullOrEmpty(accountInfor.getPersonalId())) { return
		 * ResponseDefine.responseInvalidError(); } if
		 * (!ValidateUtils.checkRequiredDateFormatIsValid(accountInfor.getPersonalIdDate
		 * (), DateUtility.DATE_FORMAT_STR)) { return
		 * ResponseDefine.responseInvalidError(); } if
		 * (StringUtility.isNullOrEmpty(accountInfor.getPersonalIdArea())) { return
		 * ResponseDefine.responseInvalidError(); }
		 */
		if (StringUtility.isNullOrEmpty(accountInfor.getEmail())) {
			return ResponseDefine.responseInvalidError();
		}
		if (StringUtility.isNullOrEmpty(accountInfor.getPhone())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateRegisterAccountRequest(RegisterAccountRequest request, MultipartFile[] files) {
		BaseResponse response = validateModifyEnterpriseInfor(request.getEnterpriseInfor());
		if (response != null) {
			return response;
		}
		response = validateRegisterAccountInfor(request.getAccountInfor());
		if (response != null) {
			return response;
		}
		if (files == null || files.length < 1) {
			return ResponseDefine.responseInvalidError();
		}
		for (MultipartFile multipartFile : files) {
			if (multipartFile == null) {
				return ResponseDefine.responseInvalidError();
			}
		}
		return null;
	}

	public static BaseResponse validateRegisterAccountRequestV2(RegisterAccountRequest request, 
			MultipartFile[] regisForm,
			MultipartFile[] bussinessLicense,
			MultipartFile[] personalCard,
			MultipartFile[] others) {
		BaseResponse response = validateModifyEnterpriseInfor(request.getEnterpriseInfor());
		if (response != null) {
			return response;
		}
		response = validateRegisterAccountInfor(request.getAccountInfor());
		if (response != null) {
			return response;
		}
		if(regisForm == null || regisForm.length < 1) {
			return ResponseDefine.responseInvalidError();
		}
		for (MultipartFile multipartFile : regisForm) {
			if (multipartFile == null) {
				return ResponseDefine.responseInvalidError();
			}
		}
		if(bussinessLicense == null || bussinessLicense.length < 1) {
			return ResponseDefine.responseInvalidError();
		}
		for (MultipartFile multipartFile : bussinessLicense) {
			if (multipartFile == null) {
				return ResponseDefine.responseInvalidError();
			}
		}
		if(personalCard == null || personalCard.length < 1) {
			return ResponseDefine.responseInvalidError();
		}
		for (MultipartFile multipartFile : personalCard) {
			if (multipartFile == null) {
				return ResponseDefine.responseInvalidError();
			}
		}
		if(others != null && others.length > 1) {
			for (MultipartFile multipartFile : others) {
				if (multipartFile == null) {
					return ResponseDefine.responseInvalidError();
				}
			}
		}
		return null;
	}

	private static BaseResponse validateApproveAccount(SysApproveAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getEnterpriseInfor().getBccsId())) {
			return ResponseDefine.responseInvalidError();
		}

		BaseResponse response = validateModifyEnterpriseInfor(request.getEnterpriseInfor());
		if (response != null) {
			return response;
		}
		return validateModifyAccountInfor(request.getAccountInfor());
	}

	private static BaseResponse validateRejectAccount(SysApproveAccountRequest request) {
		if (StringUtility.isNullOrEmpty(request.getReason())) {
			return ResponseDefine.responseInvalidError();
		}
		return null;
	}

	public static BaseResponse validateSysApproveAccountRequest(SysApproveAccountRequest request) {
		if (request.getStatus() != StatusDefine.AccountStatus.REJECT.getValue()
				&& request.getStatus() != StatusDefine.AccountStatus.STATUS_NEW.getValue()) {
			return ResponseDefine.responseInvalidError("Invalid status");
		}
		if (request.getStatus() == StatusDefine.AccountStatus.REJECT.getValue()) {
			return validateRejectAccount(request);
		} else {
			return validateApproveAccount(request);
		}
	}

	public static BaseResponse validateSysUpdateRegisUserRequest(SysUpdateRegisUserRequest request) {
		if (StringUtility.isNullOrEmpty(request.getEnterpriseInfor().getBccsId())) {
			return ResponseDefine.responseInvalidError();
		}

		BaseResponse response = validateModifyEnterpriseInfor(request.getEnterpriseInfor());
		if (response != null) {
			return response;
		}
		return validateModifyAccountInfor(request.getAccountInfor());
	}

}
