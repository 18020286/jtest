package com.viettel.mve.authservice.service.impl.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.service.admin.AdminViewAccountService;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.AdminGetListRoleByUserResponse;
import com.viettel.mve.client.response.auth.AdminListAccountResponse;
import com.viettel.mve.client.response.auth.AdminViewAccountResponse;
import com.viettel.mve.client.response.auth.object.AccountInformation;
import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("AdminViewAccountService")
public class AdminViewAccountServiceImpl extends BaseAccountServiceImpl implements AdminViewAccountService {

	private AccountInformation getAccountInforFromMVEUser(MVEUser user) {
		AccountInformation rs = new AccountInformation();
		rs.setAvartar(user.getAvartar());
		rs.setFullName(user.getFullName());
		rs.setPersonalId(user.getPersonalId());
		rs.setPhone(user.getPhone());
		rs.setSex(user.getSex());
		rs.setUserId(MVEUtils.convertIDValueToLong(user.getId()));
		rs.setUsername(user.getUserName());
		rs.setPosition(user.getPosition());
		return rs;
	}

	@Override
	public BaseResponse searchListAccount(String token, long enterpriseId, long currentUser, String currentUserName,
			AdminGetListAccountRequest request) {
		PagingObject<AdminListAccountItem> rs = userRepositoryCustom.searchListAccountForAdmin(enterpriseId,
				currentUser, request);
		AdminListAccountResponse response = new AdminListAccountResponse();
		response.setListData(rs.getListData());
		response.setCurrentPage(rs.getCurrentPage());
		response.setTotalRow(rs.getTotalRow());
		response.setErrorCode(ErrorDefine.OK);
		return response;
	}

	@Override
	public BaseResponse getAccountInfor(String token, long enterpriseId, String currentUserName,
			AdminViewAccountRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			String messageError = MessagesUtils.getMessage("message.error.deletedorinvalid");
			BaseResponse response = ResponseDefine.responseInvalidError(messageError);
			return response;
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		AdminViewAccountResponse response = new AdminViewAccountResponse();
		AccountInformation accountInfor = getAccountInforFromMVEUser(user);
		accountInfor.setRoles(getRoleCodesByUser(user.getId()));
		response.setAccountInfor(accountInfor);
		response.setErrorCode(ErrorDefine.OK);
		return response;
	}

	@Override
	public BaseResponse getListRoleByUser(long enterpriseId, AdminGetListRoleByUserRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		BaseResponse rsValidate = checkIsValidEnterpriseId(user.getBusinessId(), enterpriseId);
		if (rsValidate != null) {
			return rsValidate;
		}
		List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
		List<Long> roleIds = new ArrayList<Long>();
		if (userRoles != null && !userRoles.isEmpty()) {
			for (UserRole userRole : userRoles) {
				roleIds.add(MVEUtils.convertIDValueToLong(userRole.getRoleId()));
			}
		}
		AdminGetListRoleByUserResponse response = new AdminGetListRoleByUserResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setRoles(roleIds);
		return response;
	}

}
