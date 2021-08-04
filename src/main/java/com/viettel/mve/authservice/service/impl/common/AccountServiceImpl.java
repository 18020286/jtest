package com.viettel.mve.authservice.service.impl.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.viettel.mve.client.request.auth.ValidateRegisterRequest;
import com.viettel.mve.client.response.auth.ValidateRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.external.intercomm.NotificationClient;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.ChangePasswordRequest;
import com.viettel.mve.client.request.auth.RegisterAccountRequest;
import com.viettel.mve.client.request.auth.UpdateRequiredInforRequest;
import com.viettel.mve.client.request.auth.object.ModifyEnterpriseInfor;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.client.request.customer.ModifyEnterpriseRequest;
import com.viettel.mve.client.request.notification.SendMultipleEmailRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.CreateAccountResponse;
import com.viettel.mve.client.response.auth.ForgetAccountResponse;
import com.viettel.mve.client.response.auth.object.ForgetAccountInfor;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.caching.RedisCaching;
import com.viettel.mve.common.intercomm.request.CheckRequireLoginRequest;
import com.viettel.mve.common.intercomm.request.GetEnterpriseByIDReq;
import com.viettel.mve.common.intercomm.response.CheckRequireLoginResp;
import com.viettel.mve.common.intercomm.response.GetEnterpriseByIDResp;
import com.viettel.mve.common.intercomm.response.ModifyEnterpriseResp;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.JsonUtils;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("AccountService")
public class AccountServiceImpl extends BaseAccountServiceImpl implements AccountService {
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private ConfigValue configValue;

	@Autowired
	private NotificationClient notificationClient;

	@Autowired
	private TaskExecutor taskExecutor;

	private static final String REDIS_PREFIX_KEY = "AUTH:";
	private static final String REDIS_LAST_MODIFY_KEY = REDIS_PREFIX_KEY + "LAST_MODIFY:";

	@Autowired
	private RedisCaching redisCaching;

	@Override
	public BaseResponse createEnterprise(ModifyEnterpriseInfor enterpriseInfor) {
		ModifyEnterpriseRequest request = new ModifyEnterpriseRequest();
		request.setModifyUser(-1);
		request.setEnterpriseInfor(enterpriseInfor);
		ModifyEnterpriseResp rs = customerClient.createEnterprise(request);
		if (rs.getErrorCode() == ErrorDefine.OK && rs.getEnterpriseId() == 0) {
			throw new RuntimeException("getEnterpriseId null empty");
		}
		return rs;
	}

	@Override
	public MVEUser createUserFromRegisterRequest(RegisterAccountInfor accountInfor) {
		MVEUser user = new MVEUser();
		user.setFullName(accountInfor.getFullName());
		user.setUserName(accountInfor.getUsername());
		user.setCreateDate(new Date());
		user.setPhone(accountInfor.getPhone());
		user.setEmail(accountInfor.getEmail());
		user.setStatus(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		//user.setPassword(encoder.encode(configValue.getDefaultPassword()));
		user.setPersonalId(accountInfor.getPersonalId());
		user.setPosition(accountInfor.getPosition());
		user.setPersonalDate(accountInfor.getPersonalIdDate());
		user.setPersonalArea(accountInfor.getPersonalIdArea());
		return user;
	}

	@Override
	public void sendNotifyNewRegisEmail(ModifyEnterpriseInfor enterpriseInfor, RegisterAccountInfor accountInfor) {
		if (!StringUtility.isNullOrEmpty(configValue.getRegisNotiEmails())) {
			String[] toEmails = configValue.getRegisNotiEmails().split(",");
			if (toEmails == null || toEmails.length == 0) {
				return;
			}
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					SendMultipleEmailRequest request = new SendMultipleEmailRequest();
					request.setToEmails(toEmails);
					request.setEmailTitle("[MyViettel Enterprise] Yêu cầu tạo tài khoản");
					StringBuilder content = new StringBuilder();
					content.append("Tên doanh nghiệp: ").append(enterpriseInfor.getEnterpriseName()).append("<br>");
					content.append("Tài khoản: ").append(accountInfor.getUsername()).append("<br>");
					content.append("Tên tài khoản: ").append(accountInfor.getFullName()).append("<br>");
					//content.append("Số CMND: ").append(accountInfor.getPersonalId()).append("<br>");
					content.append("Email: ").append(accountInfor.getEmail()).append("<br>");
					content.append("Điện thoại: ").append(accountInfor.getPhone()).append("<br>");
					content.append("Địa chỉ: ").append(enterpriseInfor.getEnterpriseAddress());
					request.setHtmlContent(content.toString());
					notificationClient.sendMultipleEmail(request);
				}
			});
		}
	}
	@Override
	public void sendNotifyImportAccountEmail(ModifyEnterpriseInfor enterpriseInfor, RegisterAccountInfor accountInfor, String password) {
		if (!StringUtility.isNullOrEmpty(configValue.getRegisNotiEmails())) {
			String[] toEmails = accountInfor.getEmail().split(",");
			if (toEmails == null || toEmails.length == 0) {
				return;
			}
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					SendMultipleEmailRequest request = new SendMultipleEmailRequest();
					request.setToEmails(toEmails);
					request.setEmailTitle("[MyViettel Enterprise] Tạo tài khoản thành công");
					StringBuilder content = new StringBuilder();
					content.append("Tên doanh nghiệp: ").append(enterpriseInfor.getEnterpriseName()).append("<br>");
					content.append("Tài khoản: ").append(accountInfor.getUsername()).append("<br>");
					content.append("Mật khẩu: ").append(password).append("<br>");
					content.append("Tên tài khoản: ").append(accountInfor.getFullName()).append("<br>");
					content.append("Email: ").append(accountInfor.getEmail()).append("<br>");
					content.append("Điện thoại: ").append(accountInfor.getPhone()).append("<br>");
					content.append("Địa chỉ: ").append(enterpriseInfor.getEnterpriseAddress());
					request.setHtmlContent(content.toString());
					notificationClient.sendMultipleEmail(request);
				}
			});
		}
	}

	@Override
	public BaseResponse changePassword(long updateUser, long userId, ChangePasswordRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.account.notexist"));
		} else if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
			return ResponseDefine
					.responseInvalidError(MessagesUtils.getMessage("message.password.error.oldpass.incorrect"));
		} else if (encoder.matches(request.getNewPassword(), user.getPassword())) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.password.error.sameoldpass"));
		} else {
			user.setPassword(encoder.encode(request.getNewPassword()));
			user.setUpdateDate(new Date());
			user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
			if (user.getStatus() == StatusDefine.AccountStatus.STATUS_NEW.getValue()) {
				user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
			}
			userRepository.save(user);
			return ResponseDefine.responseOK();
		}
	}

	@Override
	public BaseResponse updateRequiredInfor(long userId, UpdateRequiredInforRequest request, long updateUser) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.account.notexist"));
		} else {
			user.setEmail(request.getEmail());
			user.setUpdateDate(new Date());
			user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
			userRepository.save(user);
			MVELoggingUtils.logMVEDebug("[UPDATE-INFOR]: [%s]\nUSER: [%ld]\nUSER-UPDATRE: [%ld]",
					JsonUtils.toJson(request), userId, updateUser);
			return ResponseDefine.responseOK();
		}
	}

	@Override
	public BaseResponse forgetAccount(String email) {
		List<MVEUser> users = userRepository.findActiveAccountByEmail(email);
		if (users == null || users.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.email.invalid"));
		} else {
			ForgetAccountResponse response = new ForgetAccountResponse();
			List<ForgetAccountInfor> accountInfors = new ArrayList<ForgetAccountInfor>();
			GetEnterpriseByIDReq req = new GetEnterpriseByIDReq();
			GetEnterpriseByIDResp resp;
			for (MVEUser user : users) {
				ForgetAccountInfor account = new ForgetAccountInfor();
				account.setUsername(user.getUserName());
				if (user.getBusinessId() != null) {
					req.setEnterpriseId(MVEUtils.convertIDValueToLong(user.getBusinessId()));
					resp = customerClient.getEnterpriseById(req);
					if (resp!=null && resp.getErrorCode() == ErrorDefine.OK && resp.getEnterprise() != null) {
						account.setEnterpriseName(resp.getEnterprise().getEnterpriseName());
					}
				}
				accountInfors.add(account);
			}
			response.setErrorCode(ErrorDefine.OK);
			response.setAccountInfors(accountInfors);
			return response;
		}
	}

	/**
	@Override
	public BaseResponse registerAccount(RegisterAccountRequest accountInforRequest, MultipartFile regisForm,
			MultipartFile bussinessLicense, MultipartFile personalCard) {
		MVEUser rsUser = null;
		long bussinessId = 0;
		try {
			String userName = accountInforRequest.getAccountInfor().getUsername();
			//userName = userName + "_" + configValue.getSuffixAdmin().toUpperCase();
			accountInforRequest.getAccountInfor().setUsername(userName.toUpperCase());
			BaseResponse rsValidate = validateUsername(accountInforRequest.getAccountInfor().getUsername());
			if (rsValidate != null) {
				return rsValidate;
			}
			BaseResponse rs = createEnterprise(accountInforRequest.getEnterpriseInfor());
			if (rs.getErrorCode() != ErrorDefine.OK) {
				return ResponseDefine.responseError(rs.getErrorCode(), rs.getMessage());
			}
			bussinessId = ((ModifyEnterpriseResp) rs).getEnterpriseId();
			MVEUser user = createUserFromRegisterRequest(accountInforRequest.getAccountInfor());
			user.setBusinessId(MVEUtils.convertLongToBigInteger(bussinessId));
			user.setCreateUser(MVEUtils.convertLongToBigInteger(-1l));
			rsUser = userRepository.save(user);
			long documentUser = MVEUtils.convertIDValueToLong(rsUser.getId());
			BaseResponse rsUploadDcument = uploadRegisAccountFile(documentUser, documentUser, bussinessId, regisForm,
					bussinessLicense, personalCard);
			if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
				userRepository.delete(rsUser);
				deleteEnterprise(-1l, bussinessId);
				return rsUploadDcument;
			}
			rsUser.setCreateUser(rsUser.getId());
			rsUser.setIsSendApproveEmail(1);
			userRepository.save(rsUser);
			saveUserRole(RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId(), rsUser);
			CreateAccountResponse reponse = new CreateAccountResponse();
			//reponse.setDefaultPassword(configValue.getDefaultPassword());
			reponse.setUserName(rsUser.getUserName());
			reponse.setEnterpriseId(bussinessId);
			reponse.setErrorCode(ErrorDefine.OK);
			sendNotifyNewRegisEmail(accountInforRequest.getEnterpriseInfor(), accountInforRequest.getAccountInfor());
			return reponse;
		} catch (Exception e) {
			try {
				if (rsUser != null) {
					userRepository.delete(rsUser);
				}
				if (bussinessId != 0) {
					deleteEnterprise(-1l, bussinessId);
				}

			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	}**/

	@Override
	public void setLastModifyDate(long userId, Date lastModify) {
		String key = REDIS_LAST_MODIFY_KEY + userId;
		if (redisCaching.getObject(key) != null) {
			redisCaching.deleteObject(key);
		}
		redisCaching.saveObject(key, lastModify, 60 * 60 /* second */);
	}

	@Override
	public BaseResponse checkRequireLogin(CheckRequireLoginRequest request) {
		CheckRequireLoginResp response = new CheckRequireLoginResp();
		response.setErrorCode(ErrorDefine.OK);
		String key = REDIS_LAST_MODIFY_KEY + request.getUserId();
		Date lastModifyDate = redisCaching.getObject(key);
		if (lastModifyDate == null) {
			response.setRequireLogin(false);
		} else {
			response.setRequireLogin(lastModifyDate.after(request.getTokenCreateTime()));
		}
		return response;
	}
	
	@Override
	public BaseResponse registerAccountV2(RegisterAccountRequest accountInforRequest, MultipartFile[] regisForms,
			MultipartFile[] bussinessLicenses, MultipartFile[] personalCards, MultipartFile[] other) {
		MVEUser rsUser = null;
		long bussinessId = 0;
		try {
			ValidateRegisterRequest registerInfo = new ValidateRegisterRequest();
			registerInfo.setEmail(accountInforRequest.getAccountInfor().getEmail());
			registerInfo.setBusCode(accountInforRequest.getEnterpriseInfor().getBussinessCode());
			registerInfo.setTaxCode(accountInforRequest.getEnterpriseInfor().getEnterpriseTaxCode());
			BaseResponse validateRegisterInfoRs = validateEmailInfo(registerInfo, null);
			if(validateRegisterInfoRs.getErrorCode() != ErrorDefine.OK){
				return validateRegisterInfoRs;
			}
			String userName = accountInforRequest.getAccountInfor().getUsername();
			accountInforRequest.getAccountInfor().setUsername(userName.toUpperCase());
			BaseResponse rsValidate = validateUsername(accountInforRequest.getAccountInfor().getUsername());
			if (rsValidate != null) {
				return rsValidate;
			}
			BaseResponse rs = createEnterprise(accountInforRequest.getEnterpriseInfor());
			if (rs.getErrorCode() != ErrorDefine.OK) {
				return ResponseDefine.responseError(rs.getErrorCode(), rs.getMessage());
			}
			bussinessId = ((ModifyEnterpriseResp) rs).getEnterpriseId();
			MVEUser user = createUserFromRegisterRequest(accountInforRequest.getAccountInfor());
			user.setBusinessId(MVEUtils.convertLongToBigInteger(bussinessId));
			user.setCreateUser(MVEUtils.convertLongToBigInteger(-1l));
			rsUser = userRepository.save(user);
			long documentUser = MVEUtils.convertIDValueToLong(rsUser.getId());
			BaseResponse rsUploadDcument = uploadRegisAccountFileV2(documentUser, documentUser,
					bussinessId, regisForms, bussinessLicenses, personalCards, other);
			if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
				userRepository.delete(rsUser);
				deleteEnterprise(-1l, bussinessId);
				return rsUploadDcument;
			}
			rsUser.setCreateUser(rsUser.getId());
			rsUser.setIsSendApproveEmail(1);
			userRepository.save(rsUser);
			saveUserRole(RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId(), rsUser);
			CreateAccountResponse reponse = new CreateAccountResponse();
			//reponse.setDefaultPassword(configValue.getDefaultPassword());
			reponse.setUserName(rsUser.getUserName());
			reponse.setEnterpriseId(bussinessId);
			reponse.setErrorCode(ErrorDefine.OK);
			sendNotifyNewRegisEmail(accountInforRequest.getEnterpriseInfor(), accountInforRequest.getAccountInfor());
			return reponse;
		} catch (Exception e) {
			try {
				if (rsUser != null) {
					userRepository.delete(rsUser);
				}
				if (bussinessId != 0) {
					deleteEnterprise(-1l, bussinessId);
				}

			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public BaseResponse validateRegisterInfo(ValidateRegisterRequest request) {
		ValidateRegisterResponse response = new ValidateRegisterResponse();
		if(!StringUtility.isNullOrEmpty(request.getUserName())){
			BaseResponse validateUserRs = validateUsername(request.getUserName());
			response.setExistUserName(validateUserRs != null);
		}else{
			response.setExistUserName(false);
		}
		BaseResponse validateEmailRs = validateEmailInfo(request, null);
		response.setExistAccountEmail(validateEmailRs.getErrorCode() != ErrorDefine.OK);
		if(response.isExistAccountEmail() || response.isExistUserName()){
			response.setErrorCode(ErrorDefine.INVALID);
			response.setMessage("");
		}else{
			response.setErrorCode(ErrorDefine.OK);
		}
		return response;
	}

}
