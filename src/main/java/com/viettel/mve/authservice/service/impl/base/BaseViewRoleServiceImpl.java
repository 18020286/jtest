package com.viettel.mve.authservice.service.impl.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.repository.PermissionRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.RolePermissionRepository;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.auth.AdminGetListRoleDetailResponse;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.common.utils.MVEUtils;

public class BaseViewRoleServiceImpl {
	@Autowired
	protected PermissionRepositoryCustom permissionRepository;

	@Autowired
	protected RolePermissionRepository rolePermissionRepository;
	
	public BaseResponse getListRoleDetail(String langKey, long enterpriseId, AdminGetListRoleDetailRequest request) {
		List<BigInteger> roleIds = new ArrayList<BigInteger>();
		List<BigInteger> perIds = null;
		for (long rId : request.getRoleIds()) {
			roleIds.add(MVEUtils.convertLongToBigInteger(rId));
		}
		List<RolePermission> rolePers = rolePermissionRepository.findRolePermissionByRoles(roleIds);
		if (rolePers != null && !rolePers.isEmpty()) {
			perIds = rolePers.stream().map(e -> e.getPermissionId()).collect(Collectors.toList());
		}
		List<PermissionItem> lstPers = permissionRepository.getPermissionByIds(langKey, perIds);
		AdminGetListRoleDetailResponse response = new AdminGetListRoleDetailResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setPermissions(lstPers);
		return response;
	}
}
