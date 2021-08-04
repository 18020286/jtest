package com.viettel.mve.authservice.service.impl.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.core.db.entities.Permission;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.service.admin.AdminViewRoleService;
import com.viettel.mve.authservice.service.impl.base.BaseViewRoleServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.auth.AdminGetAllPermissionResponse;
import com.viettel.mve.client.response.auth.AdminGetRoleDetailResponse;
import com.viettel.mve.client.response.auth.AdminListRoleResponse;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.client.response.auth.object.RoleItem;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("AdminViewRoleService")
public class AdminViewRoleServiceImpl extends BaseViewRoleServiceImpl implements AdminViewRoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public BaseResponse getListRoles(long enterpriseId) {
		List<RoleItem> roles = roleRepository.getRolesByEnterprise(MVEUtils.convertLongToBigInteger(enterpriseId));
		AdminListRoleResponse response = new AdminListRoleResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setLstRoles(roles);
		return response;
	}

	@Override
	public BaseResponse getRoleDetail(String langKey, long enterpriseId, AdminGetRoleDetailRequest request) {
		BigInteger roleId = MVEUtils.convertLongToBigInteger(request.getRoleId());
		BigInteger eId = MVEUtils.convertLongToBigInteger(enterpriseId);
		Optional<Role> rs = roleRepository.findById(roleId);
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role rsRole = rs.get();
		if (rsRole.getIsSystem() == 1 && rsRole.getIsVisibility() == 0) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if ((rsRole.getIsSystem() == 0 && rsRole.getBusinessId() != null
				&& eId.compareTo(rsRole.getBusinessId()) != 0)) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (rsRole.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		RoleItem role = new RoleItem(rsRole.getId(), rsRole.getRoleName(), rsRole.getDescription(),
				rsRole.getIsSystem());
		AdminGetRoleDetailResponse response = new AdminGetRoleDetailResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setRoleInfor(role);
		List<RolePermission> permissions = rolePermissionRepository.findRolePermissionByRole(roleId);
		if (permissions != null && !permissions.isEmpty()) {
			List<BigInteger> perIds = new ArrayList<BigInteger>();
			for (RolePermission rolePermission : permissions) {
				perIds.add(rolePermission.getPermissionId());
			}
			List<PermissionItem> lstPermissions = permissionRepository.getPermissionByIds(langKey, perIds);
			response.setPermissions(lstPermissions);
		}
		return response;
	}

	@Override
	public BaseResponse getAllPermission(String langKey) {
		AdminGetAllPermissionResponse response = new AdminGetAllPermissionResponse();
		response.setErrorCode(ErrorDefine.OK);
		List<PermissionItem> lstPermissions = permissionRepository.getListPermissionByType(langKey,
				Permission.PERMISSION_TYPE_ENTERPRISE);
		response.setPermissions(lstPermissions);
		return response;
	}

}
