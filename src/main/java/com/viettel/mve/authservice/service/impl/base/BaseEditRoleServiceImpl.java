package com.viettel.mve.authservice.service.impl.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import com.viettel.mve.authservice.core.db.entities.Permission;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.PermissionRepository;
import com.viettel.mve.authservice.core.db.repository.RolePermissionRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;

public class BaseEditRoleServiceImpl {
	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Autowired
	protected PermissionRepository permissionRepository;

	@Autowired
	protected RolePermissionRepository rolePermissionRepository;

	@Autowired
	protected RoleRepository roleRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	protected UserRoleRepository userRoleRepository;

	private Role saveRole(long createUser, long enterpriseId, int roleType, int isSystemRole,
			AdminCreateRoleRequest request) {
		Role role = new Role();
		role.setBusinessId(MVEUtils.convertLongToBigInteger(enterpriseId));
		role.setCreateDate(new Date());
		role.setRoleCode(MVEUtils.generationRoleValue(enterpriseId));
		role.setCreateUser(MVEUtils.convertLongToBigInteger(createUser));
		role.setDescription(request.getDesctiption());
		role.setRoleType(roleType);
		role.setIsSystem(isSystemRole);
		if (isSystemRole == 1) {
			role.setIsVisibility(1);
		}
		role.setIsDelete(0);
		role.setRoleName(request.getRoleName());
		String searchName = StringUtility.convertVietNameseToEnglish(request.getRoleName());
		role.setRoleNameSearch(searchName.toUpperCase());
		return roleRepository.save(role);
	}

	private void saveRolePermission(long createUser, Role role, AdminCreateRoleRequest request) {
		List<RolePermission> lstPermission = new ArrayList<RolePermission>();
		for (long permissionId : request.getPermissions()) {
			Optional<Permission> permission = permissionRepository
					.findById(MVEUtils.convertLongToBigInteger(permissionId));
			if (permission.isPresent()) {
				RolePermission rolePermission = new RolePermission();
				rolePermission.setRoleId(role.getId());
				rolePermission.setCreateDate(new Date());
				rolePermission.setCreateUser(MVEUtils.convertLongToBigInteger(createUser));
				rolePermission.setPermissionId(permission.get().getId());
				rolePermission.setIsDelete(0);
				lstPermission.add(rolePermission);
			}
		}
		if (!lstPermission.isEmpty()) {
			rolePermissionRepository.saveAll(lstPermission);
		}
	}

	private void saveUpdateRole(long updateUser, Role role, AdminUpdateRoleRequest request) {
		role.setUpdateDate(new Date());
		role.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
		role.setDescription(request.getDesctiption());
		role.setRoleName(request.getRoleName());
		String searchName = StringUtility.convertVietNameseToEnglish(request.getRoleName());
		role.setRoleNameSearch(searchName.toUpperCase());
		roleRepository.save(role);
	}

	private void setLastModifyDateUserByRole(Role role) {
		List<UserRole> userRoles = userRoleRepository.findByRoleId(role.getId());
		if (userRoles == null || userRoles.isEmpty()) {
			return;
		}
		Date updateTime = new Date();
		long userId;
		for (UserRole userRole : userRoles) {
			userId = MVEUtils.convertIDValueToLong(userRole.getUserId());
			setLastModifyDate(userId, updateTime);
		}
	}

	protected void deletePermissionsByRole(long updateUser, Role role) {
		List<RolePermission> lstPermission = rolePermissionRepository.findRolePermissionByRole(role.getId());
		if (lstPermission != null && !lstPermission.isEmpty()) {
			for (RolePermission per : lstPermission) {
				per.setUpdateDate(new Date());
				per.setIsDelete(1);
				per.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
			}
			rolePermissionRepository.saveAll(lstPermission);
		}
	}

	protected void deleteRole(long updateUser, Role role) {
		role.setUpdateDate(new Date());
		role.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
		role.setRoleCode(MVEUtils.generationDelValue(role.getRoleCode()));
		role.setIsDelete(1);
		roleRepository.save(role);
	}

	protected void excuteDeleteRoleTransaction(long updateUser, Role role) throws TransactionException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					deletePermissionsByRole(updateUser, role);
					deleteRole(updateUser, role);
					setLastModifyDateUserByRole(role);
				} catch (Exception e) {
					MVELoggingUtils.logMVEException(e);
					throw new TransactionException("delete role error " + e.getMessage());
				}
			}
		});

	}

	protected void excuteUpdateRoleTransaction(long updateUser, Role role, AdminUpdateRoleRequest request)
			throws TransactionException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					saveUpdateRole(updateUser, role, request);
					deletePermissionsByRole(updateUser, role);
					saveRolePermission(updateUser, role, request);
					setLastModifyDateUserByRole(role);
				} catch (Exception e) {
					MVELoggingUtils.logMVEException(e);
					throw new TransactionException("Update role error " + e.getMessage());
				}
			}
		});
	}

	protected void excuteCreateRoleTransaction(long createUser, long enterpriseId, int roleType, int isSystemRole,
			AdminCreateRoleRequest request) throws TransactionException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					Role rsRole = saveRole(createUser, enterpriseId, roleType, isSystemRole, request);
					saveRolePermission(createUser, rsRole, request);
				} catch (Exception e) {
					MVELoggingUtils.logMVEException(e);
					throw new TransactionException("create role error " + e.getMessage());
				}
			}
		});
	}

	protected void setLastModifyDate(long userId, Date updateTime) {
		accountService.setLastModifyDate(userId, updateTime);
	}
}
