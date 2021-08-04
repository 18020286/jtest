package com.viettel.mve.authservice.service.impl.admin;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.core.db.entities.Permission;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.PermissionRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.service.admin.AdminEditRoleService;
import com.viettel.mve.authservice.service.impl.base.BaseEditRoleServiceImpl;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("AdminEditRoleService")
public class AdminEditRoleServiceImpl extends BaseEditRoleServiceImpl implements AdminEditRoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	private List<Role> findRolesByName(long excludeId, long enterpriseId, AdminCreateRoleRequest request) {
		String roleName = request.getRoleName().toUpperCase().trim();
		BigInteger eId = MVEUtils.convertLongToBigInteger(enterpriseId);
		if (excludeId != 0) {
			BigInteger eRoleId = MVEUtils.convertLongToBigInteger(excludeId);
			return roleRepository.findRolesByNameAndEnterpriseExcludeId(roleName, eId, eRoleId);
		}
		return roleRepository.findRolesByNameAndEnterprise(roleName, eId);
	}

	private BaseResponse validatePermissions(List<Long> permissions) {
		if (permissions == null || permissions.size() == 0) {
			return null;
		}
		List<BigInteger> perIds = permissions.stream().map(e -> MVEUtils.convertLongToBigInteger(e))
				.collect(Collectors.toList());
		Iterable<Permission> lsPermission = permissionRepository.findAllById(perIds);
		if (lsPermission == null) {
			return ResponseDefine.responseInvalidError("permissions is invalid");
		}
		for (Permission permissionItem : lsPermission) {
			if (permissionItem.getIsDelete() == 1
					|| permissionItem.getPermissionType() == Permission.PERMISSION_TYPE_ADMIN) {
				return ResponseDefine.responseInvalidError("permissions is invalid");
			}
		}

		return null;

	}

	@Override
	public BaseResponse createRole(long createUser, long enterpriseId, AdminCreateRoleRequest request) {
		List<Role> lstRoles = findRolesByName(0, enterpriseId, request);
		if (lstRoles != null && !lstRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.name.exist"));
		}
		BaseResponse rs = validatePermissions(request.getPermissions());
		if (rs != null) {
			return rs;
		}
		excuteCreateRoleTransaction(createUser, enterpriseId, Role.ROLE_TYPE_ENTERPRISE, 0, request);
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse updateRole(long updateUser, long enterpriseId, AdminUpdateRoleRequest request) {
		Optional<Role> rs = roleRepository.findById(MVEUtils.convertLongToBigInteger(request.getRoleId()));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role role = rs.get();
		long roleEnterpriseId = MVEUtils.convertIDValueToLong(role.getBusinessId());
		if (roleEnterpriseId == -1 || (roleEnterpriseId != 0 && roleEnterpriseId != enterpriseId)) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (role.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		long roleId = MVEUtils.convertIDValueToLong(role.getId());
		List<Role> lstRoles = findRolesByName(roleId, enterpriseId, request);
		if (lstRoles != null && !lstRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.name.exist"));
		}
		BaseResponse rsResponse = validatePermissions(request.getPermissions());
		if (rsResponse != null) {
			return rsResponse;
		}
		excuteUpdateRoleTransaction(updateUser, role, request);
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse deleteRole(long updateUser, long enterpriseId, AdminDeleteRoleRequest request) {
		Optional<Role> rs = roleRepository.findById(MVEUtils.convertLongToBigInteger(request.getRoleId()));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role role = rs.get();
		long roleEnterpriseId = MVEUtils.convertIDValueToLong(role.getBusinessId());
		if (roleEnterpriseId != enterpriseId) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (role.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		List<UserRole> userRoles = userRoleRepository.findByRoleId(role.getId());
		if (userRoles != null && !userRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.role.using"));
		}
		excuteDeleteRoleTransaction(updateUser, role);
		return ResponseDefine.responseOK();
	}

}
