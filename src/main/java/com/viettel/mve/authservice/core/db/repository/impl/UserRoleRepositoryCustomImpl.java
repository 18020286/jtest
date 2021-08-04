package com.viettel.mve.authservice.core.db.repository.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.viettel.mve.authservice.core.db.object.WrapObjectString;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepositoryCustom;
import com.viettel.mve.common.base.repository.BaseRepository;

@Repository("UserRoleRepositoryCustom")
@SuppressWarnings("unchecked")
public class UserRoleRepositoryCustomImpl extends BaseRepository implements UserRoleRepositoryCustom {

	@Override
	public List<String> findPermissionCodesByUser(BigInteger userId) {
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT DISTINCT P.PERMISSION_CODE ");
		queryBuilder.append("FROM user_roles UR ");
		queryBuilder.append("LEFT JOIN role_permissions RP ON RP.ROLE_ID = UR.ROLE_ID ");
		queryBuilder.append("LEFT JOIN permission P ON P.ID = RP.PERMISSION_ID ");
		queryBuilder.append("where UR.USER_ID = :userId AND UR.IS_DEL = 0  ");
		params.put("userId", userId);
		queryBuilder.append("AND RP.IS_DEL = 0 AND P.PERMISSION_CODE is not null ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "PermissionStringMapping");
		bindParameter(params, query);
		List<WrapObjectString> rs = (List<WrapObjectString>) query.getResultList();
		List<String> lsPermission = rs.stream().map(e -> e.toString()).collect(Collectors.toList());
		return lsPermission;
	}

	@Override
	public String findRoleCodesByUser(BigInteger userId) {
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT GROUP_CONCAT(R.ROLE_NAME SEPARATOR '; ') as ROLES ");
		queryBuilder.append("FROM user_roles UR ");
		queryBuilder.append("LEFT JOIN role R on UR.role_id = R.id ");
		queryBuilder.append("where UR.USER_ID = :userId AND UR.IS_DEL = 0  ");
		params.put("userId", userId);
		Query query = em.createNativeQuery(queryBuilder.toString(), "RoleStringMapping");
		bindParameter(params, query);
		WrapObjectString rs = (WrapObjectString) query.getSingleResult();
		return rs.getText();
	}

}
