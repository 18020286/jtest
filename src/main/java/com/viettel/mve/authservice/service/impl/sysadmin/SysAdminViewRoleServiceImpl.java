package com.viettel.mve.authservice.service.impl.sysadmin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.service.impl.base.BaseViewRoleServiceImpl;
import com.viettel.mve.authservice.service.sysadmin.SysAdminViewRoleService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.request.auth.AdminGetRoleDetailRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminGetListPermissionRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.AdminGetAllPermissionResponse;
import com.viettel.mve.client.response.auth.AdminListRoleResponse;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.client.response.auth.object.RoleItem;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;
import com.viettel.mve.client.response.auth.sysadmin.SysAdminGetRoleDetailResponse;
import com.viettel.mve.client.response.auth.sysadmin.SysAdminSearchRoleResponse;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("SysAdminViewRoleService")
public class SysAdminViewRoleServiceImpl extends BaseViewRoleServiceImpl implements SysAdminViewRoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoleRepositoryCustom roleRepositoryCustom;

	@Autowired
	private UserRepository userRepository;

	private RoleItem generationEnterpriseAdminRole() {
		RoleItem role = new RoleItem();
		role.setRoleId(RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId());
		role.setIsSystem(1);
		role.setRoleName(MessagesUtils.getMessage("label.role.enterpriseadmin"));
		return role;
	}

	private RoleItem generationEnterpriseMemberRole() {
		RoleItem role = new RoleItem();
		role.setRoleId(RoleDefine.SystemRole.ENTERPRISE_MEMBER.getId());
		role.setIsSystem(1);
		role.setRoleName(MessagesUtils.getMessage("label.role.enterprisemember"));
		return role;
	}

	private List<RoleItem> loadDynamicSysManagerRole(MVEUser user) {
		List<RoleItem> dynamicRole = null;
		if (user.getIsSystem() == 1) {
			// Root user => Get list role
			dynamicRole = roleRepository.getListSysAdminRole();
		}
		if (dynamicRole == null) {
			dynamicRole = new ArrayList<RoleItem>();
		}
		return dynamicRole;
	}

	@Override
	public BaseResponse searchListRole(SysAdminSearchRoleRequest request, String langKey) {
		PagingObject<SysAdminRoleItem> rs = roleRepositoryCustom.searchListRoleForSysAdmin(request, langKey);
		SysAdminSearchRoleResponse reponse = new SysAdminSearchRoleResponse();
		reponse.setListData(rs.getListData());
		reponse.setCurrentPage(rs.getCurrentPage());
		reponse.setTotalRow(rs.getTotalRow());
		reponse.setErrorCode(ErrorDefine.OK);
		return reponse;
	}

	@Override
	public BaseResponse getRoleDetail(String langKey, AdminGetRoleDetailRequest request) {
		BigInteger roleId = MVEUtils.convertLongToBigInteger(request.getRoleId());
		Optional<Role> rs = roleRepository.findById(roleId);
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		Role rsRole = rs.get();
		if (rsRole.getBusinessId() != null && MVEUtils.convertIDValueToLong(rsRole.getBusinessId()) > 0) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (rsRole.getIsSystem() == 1 && rsRole.getIsVisibility() == 0) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		if (rsRole.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("error.role.deleted"));
		}
		SysAdminRoleItem role = new SysAdminRoleItem(rsRole.getId(), rsRole.getRoleName(), rsRole.getDescription(),
				rsRole.getIsSystem());
		role.setRoleType(rsRole.getRoleType());
		SysAdminGetRoleDetailResponse response = new SysAdminGetRoleDetailResponse();
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
	public BaseResponse getListPermission(String langKey, SysAdminGetListPermissionRequest request) {
		AdminGetAllPermissionResponse response = new AdminGetAllPermissionResponse();
		response.setErrorCode(ErrorDefine.OK);
		List<PermissionItem> lstPermissions = permissionRepository.getListPermissionByType(langKey,
				request.getPermissionType());
		response.setPermissions(lstPermissions);
		return response;
	}

	@Override
	public BaseResponse getListRoleForCreateAccount(long currentUser) {
		Optional<MVEUser> rs = userRepository.findById(MVEUtils.convertLongToBigInteger(currentUser));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		MVEUser user = rs.get();
		if (user.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		AdminListRoleResponse response = new AdminListRoleResponse();
		response.setErrorCode(ErrorDefine.OK);
		List<RoleItem> lstRoles = new ArrayList<RoleItem>();
		//lstRoles.add(generationEnterpriseAdminRole());
		lstRoles.addAll(loadDynamicSysManagerRole(user));
		response.setLstRoles(lstRoles);
		return response;
	}

	@Override
	public BaseResponse getListRoleForViewAccount(long currentUser) {
		Optional<MVEUser> rs = userRepository.findById(MVEUtils.convertLongToBigInteger(currentUser));
		if (!rs.isPresent()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		MVEUser user = rs.get();
		if (user.getIsDelete() == 1) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		AdminListRoleResponse response = new AdminListRoleResponse();
		response.setErrorCode(ErrorDefine.OK);
		List<RoleItem> lstRoles = new ArrayList<RoleItem>();
		lstRoles.add(generationEnterpriseAdminRole());
		lstRoles.add(generationEnterpriseMemberRole());
		lstRoles.addAll(loadDynamicSysManagerRole(user));
		response.setLstRoles(lstRoles);
		return response;
	}

	@Override
	public BaseResponse getListRoles() {
		List<RoleItem> roles = roleRepository.getListSysAdminRole();
		AdminListRoleResponse response = new AdminListRoleResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setLstRoles(roles);
		return response;
	}

}
