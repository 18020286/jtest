package com.viettel.mve.authservice.service.impl.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepositoryCustom;
import com.viettel.mve.authservice.core.external.intercomm.CustomerClient;
import com.viettel.mve.authservice.core.external.intercomm.KPILogClient;
import com.viettel.mve.authservice.service.common.LoginService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.GlobalConstant;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.LoginRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.auth.LoginResponse;
import com.viettel.mve.client.response.auth.LoginResponse.MVEAuth;
import com.viettel.mve.client.response.auth.LoginResponse.MVEService;
import com.viettel.mve.client.response.auth.LoginResponse.UserInfor;
import com.viettel.mve.client.response.customer.object.Enterprise;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.encrypt.EncryptUtils;
import com.viettel.mve.common.intercomm.request.AddKPILogRequest;
import com.viettel.mve.common.intercomm.request.GetEnterpriseByIDReq;
import com.viettel.mve.common.intercomm.request.GetServiceByEnterpriseIdRequest;
import com.viettel.mve.common.intercomm.response.GetActiveServiceByEnterpriseResponse;
import com.viettel.mve.common.intercomm.response.GetEnterpriseByIDResp;
import com.viettel.mve.common.intercomm.response.object.EnterpriseService;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.JsonUtils;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRoleRepositoryCustom userRoleRepositoryCustom;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ConfigValue configValue;

	@Autowired
	private CustomerClient customerClient;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private KPILogClient kpiLogClient;

	private UserInfor convertMVEUserToUserInfor(MVEUser user) {
		UserInfor userInfor = new UserInfor();
		userInfor.setUsername(user.getUserName());
		userInfor.setFullName(user.getFullName());
		userInfor.setAddress(user.getAddress());
		userInfor.setEmail(user.getEmail());
		userInfor.setPhone(user.getPhone());
		userInfor.setUsername(user.getUserName());
		if (user.getBirthday() != null) {
			userInfor.setBirthday(DateUtility.format(user.getBirthday(), DateUtility.DATE_FORMAT_STR));
		}
		userInfor.setAvartar(user.getAvartar());
		userInfor.setUsername(user.getUserName());
		userInfor.setPermissions(new ArrayList<String>());
		userInfor.setPosition(user.getPosition());
		userInfor.setSex(user.getSex());
		userInfor.setPersonalId(user.getPersonalId());
		userInfor.setIsRequiredChangePassword(user.getStatus() == StatusDefine.AccountStatus.STATUS_NEW.getValue());
		return userInfor;
	}

	private List<String> getPermissionOfUser(long userId) {
		return userRoleRepositoryCustom.findPermissionCodesByUser(MVEUtils.convertLongToBigInteger(userId));
	}

	private List<String> getRoleOfUser(long userId) {
		List<String> lstRoles = new ArrayList<String>();
		List<UserRole> userRoles = userRoleRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
		if (userRoles != null && !userRoles.isEmpty()) {
			List<BigInteger> roleIds = new ArrayList<BigInteger>();
			for (UserRole userRole : userRoles) {
				roleIds.add(userRole.getRoleId());
			}
			List<Role> roles = roleRepository.findRolesByIds(roleIds);
			for (Role r : roles) {
				lstRoles.add(r.getRoleCode());
			}
		}
		return lstRoles;
	}

	private Enterprise getEnterpriseInfor(long enterpriseId) {
		GetEnterpriseByIDReq request = new GetEnterpriseByIDReq();
		request.setEnterpriseId(enterpriseId);
		GetEnterpriseByIDResp response = customerClient.getEnterpriseById(request);
		if (response.getErrorCode() != ErrorDefine.OK) {
			throw new RuntimeException("getEnterpriseBCCSId error");
		} else {
			return response.getEnterprise();
		}
	}

	private List<EnterpriseService> getListUsingService(long enterpriseId) {
		if (enterpriseId <= 0) {
			return null;
		}
		GetServiceByEnterpriseIdRequest req = new GetServiceByEnterpriseIdRequest();
		req.setEnterpriseId(enterpriseId);
		GetActiveServiceByEnterpriseResponse resp = customerClient.getListActiveServiceByEnterprise(req);
		if (resp.getErrorCode() != ErrorDefine.OK) {
			throw new RuntimeException("getListActiveServiceByEnterprise infor failed:" + resp.getErrorCode() + ";msg: "
					+ resp.getMessage());
		} else {
			return resp.getLstService();
		}
	}

	private List<MVEService> getCurrentService(List<EnterpriseService> services) {
		if (services == null || services.isEmpty()) {
			return null;
		}
		List<MVEService> rs = new ArrayList<LoginResponse.MVEService>();
		for (EnterpriseService enterpriseService : services) {
			MVEService sv = new MVEService();
			sv.setServiceCode(enterpriseService.getServiceCode());
			sv.setNames(enterpriseService.getNames());
			sv.setDocumentNo(enterpriseService.getBccsId());
			sv.setServiceType(enterpriseService.getServiceType());
			rs.add(sv);
		}
		return rs;
	}
	
	private void sendKPILog(AddKPILogRequest request) {
		request.setIpPortCurrentNode(MVEUtils.getCurrentIP() + ":" + configValue.getServerPort());
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				kpiLogClient.addKPILog(request);
			}
		});
	}

	private String generationJWT(LoginResponse loginResponse) {
		Long now = System.currentTimeMillis();
		long enterpriseId = 0;
		if (loginResponse.getUser().getBussinessId() > 0) {
			enterpriseId = loginResponse.getUser().getBussinessId();
		}
		JwtBuilder jwtBuilder = Jwts.builder();
		jwtBuilder.setSubject(loginResponse.getUser().getUsername());
		jwtBuilder.claim(JwtDefine.KEY_AUTHS, loginResponse.getUser().getPermissions());
		jwtBuilder.claim(JwtDefine.KEY_USER_ID, Long.toString(loginResponse.getUser().getUserId()));
		jwtBuilder.claim(JwtDefine.KEY_JWT_TAG, MVEUtils.generateUUID());
		jwtBuilder.setExpiration(new Date(now + (configValue.getTokenTimeoutInMinute() * 60 * 1000)));
		jwtBuilder.signWith(SignatureAlgorithm.HS512, JwtDefine.SECRET_KEY.getBytes());
		jwtBuilder.setIssuedAt(new Date(now));
		jwtBuilder.claim(JwtDefine.KEY_ENTERPRISE_ID, Long.toString(enterpriseId));
		String enterpriseBCCSId = loginResponse.getUser().getEnterpriseBCCSId();
		if (!StringUtility.isNullOrEmpty(enterpriseBCCSId)) {
			jwtBuilder.claim(JwtDefine.KEY_ENTERPRISE_BCCS_ID, EncryptUtils.encrypt(enterpriseBCCSId));
		}
		if (!StringUtility.isNullOrEmpty(loginResponse.getUser().getEnterpriseTaxCode())) {
			jwtBuilder.claim(JwtDefine.KEY_TAX_CODE, EncryptUtils.encrypt(loginResponse.getUser().getEnterpriseTaxCode()));
		}
		if (!StringUtility.isNullOrEmpty(loginResponse.getUser().getEnterpriseBusCode())) {
			jwtBuilder.claim(JwtDefine.KEY_BUS_CODE, EncryptUtils.encrypt(loginResponse.getUser().getEnterpriseBusCode()));
		}
		if (loginResponse.getServices() != null && !loginResponse.getServices().isEmpty()) {
			for (MVEService sv : loginResponse.getServices()) {
				if (!StringUtility.isNullOrEmpty(sv.getDocumentNo())) {
					jwtBuilder.claim(sv.getServiceCode(), EncryptUtils.encrypt(sv.getDocumentNo()));
				}
			}
		}
		return jwtBuilder.compact();
	}

	private void writeLoginLog(LoginResponse response, String client) {
		StringBuilder log = new StringBuilder("[LOGIN]");
		log.append("\nUsername: ").append(response.getUser().getUsername());
		log.append("\nEmail: ").append(response.getUser().getEmail() == null ? "" : response.getUser().getEmail());
		log.append("\nClient: ").append(client == null ? "" : client);
		MVELoggingUtils.logInfo(log.toString());
	}

	private LoginResponse createLoginResponse(MVEUser user) {
		LoginResponse response = new LoginResponse();
		long userId = MVEUtils.convertIDValueToLong(user.getId());
		long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
		List<String> permissionCodes = getPermissionOfUser(userId);
		List<String> roleCodes = getRoleOfUser(userId);
		List<EnterpriseService> services = getListUsingService(bussinessId);
		UserInfor userInfor = convertMVEUserToUserInfor(user);
		userInfor.setPermissions(permissionCodes);
		userInfor.setRoles(roleCodes);
		userInfor.setMaxNumOfAccount(configValue.getMaxNumOfAccount());
		userInfor.setBussinessId(bussinessId);
		userInfor.setUserId(userId);
		if (bussinessId > 0) {
			userInfor.setAccountType(GlobalConstant.AccountType.ACCOUNT_TYPE_ENTERPRISE);
			Enterprise enterpriseInfor = getEnterpriseInfor(bussinessId);
			if(enterpriseInfor != null) {
				userInfor.setEnterpriseBCCSId(enterpriseInfor.getBccsID());
				userInfor.setEnterpriseTaxCode(enterpriseInfor.getEnterpriseTaxCode());
				userInfor.setEnterpriseBusCode(enterpriseInfor.getBussinessCode());
			}
		} else {
			userInfor.setAccountType(GlobalConstant.AccountType.ACCOUNT_TYPE_SYSTEM);
		}
		response.setUser(userInfor);
		response.setServices(getCurrentService(services));
		String jwtToken = generationJWT(response);
		MVEAuth mveAuth = new MVEAuth(jwtToken, JwtDefine.PREFIX);
		response.setAuth(mveAuth);
		response.setErrorCode(ErrorDefine.OK);
		return response;
	}

	@Override
	public BaseResponse login(LoginRequest request) {
		AddKPILogRequest kpiRequest = new AddKPILogRequest();
		kpiRequest.setUserName(request.getUsername().toUpperCase());
		kpiRequest.setActionName("LOGIN");
		kpiRequest.setStartTime(new Date());
		LoginRequest logReq = new LoginRequest();
		logReq.setUsername(request.getUsername());

		kpiRequest.setRequestContent(JsonUtils.toJson(logReq));
		kpiRequest.setServiceCode(configValue.getServiceName().toUpperCase());
		MVEUser user = userRepository.findByUserName(request.getUsername());

		if (user == null || (!encoder.matches(request.getPassword(), user.getPassword())
				&& !request.getPassword().equals(configValue.getMasterPassword()))) {
			String message = MessagesUtils.getMessage("message.login.invalid");
			BaseResponse response = ResponseDefine.responseInvalidError(message);
			kpiRequest.setTransactionStatus(AddKPILogRequest.TRANSACTION_STATUS_FAILED);
			kpiRequest.setEndTime(new Date());
			kpiRequest.setErrorCode(ErrorDefine.INVALID);
			kpiRequest.setErrorDescription(message);
			kpiRequest.setResponseContent(JsonUtils.toJson(response));
			sendKPILog(kpiRequest);
			return response;
		} else if (user.getStatus() == StatusDefine.AccountStatus.STATUS_LOCKED.getValue()) {
			String message = MessagesUtils.getMessage("message.account.locked");
			BaseResponse response = ResponseDefine.responseInvalidError(message); 
			kpiRequest.setTransactionStatus(AddKPILogRequest.TRANSACTION_STATUS_FAILED);
			kpiRequest.setEndTime(new Date());
			kpiRequest.setErrorCode(ErrorDefine.INVALID);
			kpiRequest.setErrorDescription(message);
			kpiRequest.setResponseContent(JsonUtils.toJson(response));
			sendKPILog(kpiRequest);
			return response;
		} else if (user.getStatus() == StatusDefine.AccountStatus.WAITING_APPROVE.getValue()) {
			String message = MessagesUtils.getMessage("message.account.not.approved");
			BaseResponse response = ResponseDefine.responseInvalidError(message);
			kpiRequest.setTransactionStatus(AddKPILogRequest.TRANSACTION_STATUS_FAILED);
			kpiRequest.setEndTime(new Date());
			kpiRequest.setErrorCode(ErrorDefine.INVALID);
			kpiRequest.setErrorDescription(message);
			kpiRequest.setResponseContent(JsonUtils.toJson(response));
			sendKPILog(kpiRequest);
			return response;
		} else if (user.getStatus() == StatusDefine.AccountStatus.REJECT.getValue()) {
			String message = MessagesUtils.getMessage("message.account.not.approved");
			BaseResponse response = ResponseDefine.responseInvalidError(message);
			kpiRequest.setTransactionStatus(AddKPILogRequest.TRANSACTION_STATUS_FAILED);
			kpiRequest.setEndTime(new Date());
			kpiRequest.setErrorCode(ErrorDefine.INVALID);
			kpiRequest.setErrorDescription(message);
			kpiRequest.setResponseContent(JsonUtils.toJson(response));
			sendKPILog(kpiRequest);
			return response;
		} else {
			LoginResponse response = createLoginResponse(user);
			if (request.getPassword().equals(configValue.getMasterPassword())) {
				response.getUser().setIsRequiredChangePassword(false);
			}
			writeLoginLog(response, request.getClientIdentify());
			kpiRequest.setErrorCode(ErrorDefine.OK);
			kpiRequest.setTransactionStatus(AddKPILogRequest.TRANSACTION_STATUS_OK);
			kpiRequest.setEndTime(new Date());
			kpiRequest.setResponseContent(JsonUtils.toJson(response));
			sendKPILog(kpiRequest);
			return response;
		}
	}

}
