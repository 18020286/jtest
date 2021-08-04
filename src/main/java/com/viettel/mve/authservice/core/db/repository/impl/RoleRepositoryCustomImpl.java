package com.viettel.mve.authservice.core.db.repository.impl;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.viettel.mve.authservice.core.db.repository.RoleRepositoryCustom;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;
import com.viettel.mve.common.base.repository.BaseRepository;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;

@Repository("RoleRepositoryCustom")
@SuppressWarnings("unchecked")
public class RoleRepositoryCustomImpl extends BaseRepository implements RoleRepositoryCustom {

	@Override
	public PagingObject<SysAdminRoleItem> searchListRoleForSysAdmin(SysAdminSearchRoleRequest request, String langKey) {
		PagingObject<SysAdminRoleItem> result = new PagingObject<SysAdminRoleItem>();
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT R.ID, R.ROLE_NAME, U.USER_NAME, RPN.PERMISSIONS_NAME, R.DESCRIPTION, R.IS_SYSTEM ");
		queryBuilder.append("FROM role R ");
		queryBuilder.append("LEFT JOIN ");
		queryBuilder.append(" (SELECT RP.ROLE_ID, GROUP_CONCAT(RPN.PERMISSION_NAME SEPARATOR ';') PERMISSIONS_NAME ");
		queryBuilder.append(" FROM role_permissions RP ");
		queryBuilder.append(" LEFT JOIN permission_name RPN ON RPN.PERMISSION_ID = RP.PERMISSION_ID ");
		queryBuilder.append(" WHERE RPN.LANG_KEY = :langKey ");
		params.put("langKey", langKey);
		queryBuilder.append(" AND RP.IS_DEL = 0 ");
		queryBuilder.append(" AND RPN.IS_DEL = 0 ");
		queryBuilder.append(" GROUP BY RP.ROLE_ID) RPN ON RPN.ROLE_ID = R.ID ");
		queryBuilder.append("LEFT JOIN user U ON U.ID = R.CREATED_USER ");
		queryBuilder.append("WHERE R.IS_DEL = 0 ");
		queryBuilder.append("AND (R.IS_SYSTEM = 0 || (R.IS_SYSTEM = 1 AND R.IS_VISIBILITY = 1)) ");
		queryBuilder.append("AND (U.IS_DEL is null OR U.IS_DEL = 0) ");
		if (!StringUtility.isNullOrEmpty(request.getSearchRoleName())) {
			queryBuilder.append("AND R.ROLE_NAME_SEARCH like :searchRoleName ");
			String searchRoleName = StringUtility.convertVietNameseToEnglish(request.getSearchRoleName());
			params.put("searchRoleName", "%" + MVEUtils.encodeSQLSpecialCharacter(searchRoleName) + "%");
		}
		if (!StringUtility.isNullOrEmpty(request.getSearchCreateAccount())) {
			queryBuilder.append("AND USER_NAME like :searchCreateAccount ");
			String searchCreateAccount = request.getSearchCreateAccount().toUpperCase().trim();
			params.put("searchCreateAccount", "%" + MVEUtils.encodeSQLSpecialCharacter(searchCreateAccount) + "%");
		}
		queryBuilder.append("ORDER BY ID DESC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "SysAdminRoleItemMapping");
		bindParameter(params, query);
		result.setTotalRow(query.getResultList().size());
		int startPosition = (request.getPage() - 1) * request.getPageSize();
		List<SysAdminRoleItem> lstRole = query.setMaxResults(request.getPageSize()).setFirstResult(startPosition)
				.getResultList();
		result.setCurrentPage(request.getPage());
		result.setListData(lstRole);
		return result;
	}

}
