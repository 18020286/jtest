package com.viettel.mve.authservice.service.impl.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.common.Utils;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.service.admin.AdminEditAccountService;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.constant.StatusDefine.AccountStatus;
import com.viettel.mve.client.request.auth.AdminCreateAccountRequest;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.UpdateAccountRequest;
import com.viettel.mve.client.request.auth.object.UserStatus;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.CreateAccountResponse;
import com.viettel.mve.client.response.auth.AdminResetPassResponse;
import com.viettel.mve.client.response.media.UploadMediaResponse;
import com.viettel.mve.client.response.media.UploadMediaResponse.MediaItem;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("AdminEditAccountService")
public class AdminEditAccountServiceImpl extends BaseAccountServiceImpl implements AdminEditAccountService {
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ConfigValue configValue;

	private MVEUser createUserFromRequest(AdminCreateAccountRequest request, String newPassword) {
		MVEUser user = new MVEUser();
		user.setUserName(request.getUsername().toUpperCase());
		user.setFullName(request.getFullName());
		user.setCreateDate(new Date());
		user.setPhone(request.getPhone());
		user.setEmail(request.getUsername());
		user.setSex(request.getSex());
		user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		user.setPassword(encoder.encode(newPassword));
		user.setPersonalId(request.getPersonalId());
		user.setPosition(request.getPosition());
		return user;
	}
	
	private void excuteDeleteUserTransaction(long updateUser, MVEUser user)
			throws TransactionException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					Date updateTime = new Date();
					user.setUserName(user.getUserName() + "_DEL_" + System.currentTimeMillis());
					user.setIsDelete(1);
					user.setUpdateDate(updateTime);
					user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
					userRepository.save(user);
					deleteUserRoles(updateUser, user.getId());
				} catch (Exception e) {
					MVELoggingUtils.logMVEException(e);
					throw new TransactionException("Update user role error " + e.getMessage());
				}
			}
		});
	}

	@Override
	public BaseResponse createAccount(long updateUser, long enterpriseId, AdminCreateAccountRequest request,
			MultipartFile avartar) {
		String newPassword = Utils.generationPassword(configValue.getLengthPassword());
		BaseResponse rsValidate = validateUsername(request.getUsername());
		if (rsValidate != null) {
			return rsValidate;
		}
		BigInteger bussinessId = MVEUtils.convertLongToBigInteger(enterpriseId);
		List<MVEUser> lstUser = userRepository.findByBusinessId(bussinessId);
		if (lstUser != null && lstUser.size() - 1 /* Tru tai khoan admin */ >= configValue.getMaxNumOfAccount()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.account.error.maxaccount"));
		}

		MVEUser user = createUserFromRequest(request, newPassword);
		user.setCreateUser(MVEUtils.convertLongToBigInteger(updateUser));
		user.setBusinessId(bussinessId);
		MVEUser rsUser = userRepository.save(user);
		BaseResponse rsUploadAvartar = uploadAvatar(avartar, enterpriseId,
				MVEUtils.convertIDValueToLong(rsUser.getId()));
		if (rsUploadAvartar != null) {
			if (rsUploadAvartar.getErrorCode() != ErrorDefine.OK) {
				userRepository.delete(rsUser);
				return rsUploadAvartar;
			} else {
				List<MediaItem> rsItems = ((UploadMediaResponse) rsUploadAvartar).getLstMedia();
				rsUser.setAvartar(rsItems.get(0).getMediaPath());
			}
		}
		userRepository.save(rsUser);
		CreateAccountResponse reponse = new CreateAccountResponse();
		//reponse.setDefaultPassword(configValue.getDefaultPassword());
		reponse.setDefaultPassword(newPassword);
		reponse.setErrorCode(ErrorDefine.OK);
		reponse.setUserName(rsUser.getUserName());
		return reponse;
	}

	@Override
	public BaseResponse resetPassword(long updateUser, long enterpriseId, AdminResetPassRequest request) {
		String newPassword = Utils.generationPassword(configValue.getLengthPassword());
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		user.setPassword(encoder.encode(newPassword));
		user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		user.setUpdateDate(new Date());
		user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
		userRepository.save(user);
		AdminResetPassResponse response = new AdminResetPassResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setDefaultPass(newPassword);
		response.setUsername(user.getUserName());
		response.setEmail(user.getEmail());
		return response;
	}

	@Override
	public BaseResponse updateStatus(long updateUser, long enterpriseId, AdminUpdateUserStatusRequest request) {
		List<MVEUser> updateUsers = new ArrayList<MVEUser>();
		Date updateTime;
		for (UserStatus item : request.getLstUserStatus()) {
			MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(item.getUserId()));
			if (user == null) {
				return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
			}
			BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
			if (rsValidate != null) {
				return rsValidate;
			}
			updateTime = new Date();
			user.setStatus(item.getStatus());
			user.setUpdateDate(updateTime);
			user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
			updateUsers.add(user);
			if (AccountStatus.STATUS_LOCKED.getValue() == item.getStatus()) {
				setLastModifyDate(item.getUserId(), updateTime);
			}
		}
		if (!updateUsers.isEmpty()) {
			userRepository.saveAll(updateUsers);
		}
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse updateAccount(long updateUser, long enterpriseId, UpdateAccountRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		user.setFullName(request.getFullName());
		user.setPhone(request.getPhone());
		user.setSex(request.getSex());
		user.setPersonalId(request.getPersonalId());
		user.setPosition(request.getPosition());
		user.setUpdateDate(new Date());
		user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
		userRepository.save(user);
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse updateAvartar(long updateUser, long userId, long enterpriseId, MultipartFile avartar) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		return excuteUpdateAvartar(user, avartar, enterpriseId, updateUser);
	}

	@Override
	public BaseResponse updateUserRole(long updateUser, long enterpriseId, AdminUpdateUserRoleRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		List<UserRole> userRoles = new ArrayList<UserRole>();
		if (request.getRoles() != null && !request.getRoles().isEmpty()) {
			List<BigInteger> roleIds = new ArrayList<BigInteger>();
			for (long roleId : request.getRoles()) {
				roleIds.add(MVEUtils.convertLongToBigInteger(roleId));
			}
			List<Role> newRoles = roleRepository.findRolesByIds(roleIds);
			long roleEnterpriseId;
			for (Role rsRole : newRoles) {
				roleEnterpriseId = MVEUtils.convertIDValueToLong(rsRole.getBusinessId());
				if ((roleEnterpriseId == -1 || (roleEnterpriseId != 0 && roleEnterpriseId != enterpriseId))
						|| (rsRole.getIsSystem() == 1 && rsRole.getIsVisibility() == 0)) {
					return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
				}
				if (rsRole.getIsDelete() == 1) {
					return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("error.role.deleted"));
				}
				UserRole userRole = new UserRole();
				userRole.setCreateDate(new Date());
				userRole.setCreateUser(MVEUtils.convertLongToBigInteger(updateUser));
				userRole.setIsDelete(0);
				userRole.setRoleId(rsRole.getId());
				userRole.setUserId(user.getId());
				userRoles.add(userRole);
			}
		}
		excuteUpdateUserRoleTransaction(updateUser, user.getId(), userRoles);
		setLastModifyDate(request.getUserId(), new Date());
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse deleteAccount(long updateUser, long enterpriseId, AdminDeleteAccountRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		excuteDeleteUserTransaction(updateUser, user);
		setLastModifyDate(request.getUserId(), new Date());
		BaseResponse response = new BaseResponse();
		response.setErrorCode(ErrorDefine.OK);
		return response;
	}

}
