package com.viettel.mve.authservice.service.impl.sysadmin;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.service.impl.base.BaseEditRoleServiceImpl;
import com.viettel.mve.authservice.service.sysadmin.SysAdminEditRoleService;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminCreateRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("SysAdminEditRoleService")
public class SysAdminEditRoleServiceImpl extends BaseEditRoleServiceImpl implements SysAdminEditRoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	private List<Role> findRolesByName(long excludeId, AdminCreateRoleRequest request) {
		String roleName = request.getRoleName().toUpperCase().trim();
		if (excludeId != 0) {
			BigInteger eRoleId = MVEUtils.convertLongToBigInteger(excludeId);
			return roleRepository.findRolesByNameExcludeId(roleName, eRoleId);
		}
		return roleRepository.findRolesByName(roleName);
	}

	@Override
	public BaseResponse createRole(long createUser, SysAdminCreateRoleRequest request) {
		List<Role> lstRoles = findRolesByName(0, request);
		if (lstRoles != null && !lstRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.name.exist"));
		}
		excuteCreateRoleTransaction(createUser, 0, request.getRoleType(), 1, request);
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse updateRole(long updateUser, AdminUpdateRoleRequest request) {
		Optional<Role> rs = roleRepository.findById(MVEUtils.convertLongToBigInteger(request.getRoleId()));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role role = rs.get();
		if (role.getIsSystem() != 1 || role.getIsVisibility() != 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (role.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("error.role.deleted"));
		}
		long roleId = MVEUtils.convertIDValueToLong(role.getId());
		List<Role> lstRoles = findRolesByName(roleId, request);
		if (lstRoles != null && !lstRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.name.exist"));
		}
		excuteUpdateRoleTransaction(updateUser, role, request);
		return ResponseDefine.responseOK();
	}

	@Override
	public BaseResponse deleteRole(long updateUser, AdminDeleteRoleRequest request) {
		Optional<Role> rs = roleRepository.findById(MVEUtils.convertLongToBigInteger(request.getRoleId()));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role role = rs.get();
		if (role.getIsSystem() != 1 || role.getIsVisibility() != 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (role.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("error.role.deleted"));
		}
		List<UserRole> userRoles = userRoleRepository.findByRoleId(role.getId());
		if (userRoles != null && !userRoles.isEmpty()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.role.using"));
		}
		excuteDeleteRoleTransaction(updateUser, role);
		return ResponseDefine.responseOK();
	}

}
