package com.viettel.mve.authservice.core.db.repository.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.viettel.mve.authservice.core.db.repository.PermissionRepositoryCustom;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.common.base.repository.BaseRepository;
import com.viettel.mve.common.stringutils.StringUtility;

@Repository("PermissionRepositoryCustom")
@SuppressWarnings("unchecked")
public class PermissionRepositoryCustomImpl extends BaseRepository implements PermissionRepositoryCustom {

	@Override
	public List<PermissionItem> getPermissionByIds(String langKey, List<BigInteger> ids) {
		if (StringUtility.isNullOrEmpty(langKey) || ids == null || ids.isEmpty()) {
			return new ArrayList<PermissionItem>();
		}
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT P.ID, PN.PERMISSION_NAME ");
		queryBuilder.append("from permission P ");
		queryBuilder.append("LEFT JOIN permission_name PN on P.id = PN.permission_id ");
		queryBuilder.append("WHERE P.IS_DEL = 0 ");
		queryBuilder.append("AND P.IS_VISIBILITY = 1 ");
		queryBuilder.append("AND PN.IS_DEL = 0 ");
		queryBuilder.append("AND PN.LANG_KEY = :langKey ");
		params.put("langKey", langKey.toLowerCase());
		StringBuilder idsParams = new StringBuilder(ids.get(0).toString());
		if (ids.size() > 1) {
			for (int i = 1; i < ids.size(); i++) {
				idsParams.append(", ").append((ids.get(i).toString()));
			}
		}
		queryBuilder.append("AND P.ID IN (:idsParams) ");
		queryBuilder.append("ORDER BY PERMISSION_NAME ASC ");
		params.put("idsParams", ids);
		Query query = em.createNativeQuery(queryBuilder.toString(), "PermissionItemMapping");
		bindParameter(params, query);
		List<PermissionItem> lstPermission = query.getResultList();
		return lstPermission;
	}

	@Override
	public List<PermissionItem> getListPermissionByType(String langKey, int permissionType) {
		if (StringUtility.isNullOrEmpty(langKey)) {
			return new ArrayList<PermissionItem>();
		}
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT P.ID, PN.PERMISSION_NAME ");
		queryBuilder.append("from permission P ");
		queryBuilder.append("LEFT JOIN permission_name PN on P.id = PN.permission_id ");
		queryBuilder.append("WHERE P.IS_DEL = 0 ");
		queryBuilder.append("AND P.IS_VISIBILITY = 1 ");
		queryBuilder.append("AND P.PERMISSION_TYPE = :permissionType ");
		params.put("permissionType", permissionType);
		queryBuilder.append("AND PN.IS_DEL = 0 ");
		queryBuilder.append("AND PN.LANG_KEY = :langKey ");
		params.put("langKey", langKey.toLowerCase());
		queryBuilder.append("ORDER BY PERMISSION_NAME ASC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "PermissionItemMapping");
		bindParameter(params, query);
		List<PermissionItem> lstPermission = query.getResultList();
		return lstPermission;
	}

}
