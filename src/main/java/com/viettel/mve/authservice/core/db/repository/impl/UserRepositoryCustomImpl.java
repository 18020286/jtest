package com.viettel.mve.authservice.core.db.repository.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.viettel.mve.authservice.core.db.repository.UserRepositoryCustom;
import com.viettel.mve.client.constant.GlobalConstant;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.client.response.auth.object.ListAccountItem;
import com.viettel.mve.common.base.repository.BaseRepository;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;

@Repository("UserRepositoryCustom")
@SuppressWarnings("unchecked")
public class UserRepositoryCustomImpl extends BaseRepository implements UserRepositoryCustom {

	@Override
	public PagingObject<ListAccountItem> searchListAccountForSysAdmin(SysSearchListAccountRequest request,
			List<BigInteger> eIds, List<BigInteger> rIds) {
		List<Integer> accountStatus = new ArrayList<Integer>();
		boolean isSearchByRoles = rIds != null && !rIds.isEmpty();
		boolean isSearchByEnterprise = eIds != null;
		accountStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		accountStatus.add(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.REJECT.getValue());
		PagingObject<ListAccountItem> result = new PagingObject<ListAccountItem>();
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT ");
		queryBuilder.append("U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, ");
		queryBuilder.append("U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,");
		queryBuilder.append("U.REJECT_REASON, U.STATUS, ");
		queryBuilder.append("CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, ");
		queryBuilder.append("U1.USER_NAME AS CREATED_USER_NAME ");
		queryBuilder.append("FROM user U ");
		queryBuilder.append("LEFT JOIN user U1 ON U.CREATED_USER = U1.ID ");

		if (isSearchByRoles) {
			queryBuilder.append("LEFT JOIN ");
			queryBuilder.append(" (SELECT USER_ID, GROUP_CONCAT(ROLE_ID SEPARATOR ';') ROLE_IDS ");
			queryBuilder.append("  FROM user_roles ");
			queryBuilder.append("  WHERE IS_DEL = 0 ");
			queryBuilder.append("  AND ROLE_ID IN (:roleIds) ");
			params.put("roleIds", rIds);
			queryBuilder.append("  GROUP BY USER_ID) AS R ");
			queryBuilder.append("ON U.ID = R.USER_ID ");
		}

		queryBuilder.append("WHERE U.IS_SYSTEM = 0 ");
		queryBuilder.append("AND U.IS_DEL = 0 ");
		if (!StringUtility.isNullOrEmpty(request.getUsername())) {
			String searchUsername = request.getUsername().toUpperCase().trim();
			queryBuilder.append("AND ( ");
			queryBuilder.append("U.USER_NAME like :username ");
			params.put("username", "%" + searchUsername + "%");
			queryBuilder.append("OR U.EMAIL like :email ");
			params.put("email", "%" + searchUsername + "%");
			queryBuilder.append("OR U.PHONE like :phone ");
			params.put("phone", "%" + searchUsername + "%");
			queryBuilder.append(") ");
		}
		if (accountStatus.contains(request.getStatus())) {
			if (request.getStatus() == StatusDefine.AccountStatus.STATUS_ACTIVE.getValue()) {
				List<Integer> queryStatus = new ArrayList<Integer>();
				queryStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
				queryStatus.add(StatusDefine.AccountStatus.STATUS_NEW.getValue());
				queryBuilder.append("AND U.STATUS in (:queryStatus) ");
				params.put("queryStatus", queryStatus);
			} else {
				queryBuilder.append("AND U.STATUS = :status ");
				params.put("status", request.getStatus());
			}
		}
		if (!StringUtility.isNullOrEmpty(request.getFromDate())) {
			Date fromDate = DateUtility.convertAsFromDate(request.getFromDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE >= :fromDate ");
			params.put("fromDate", fromDate);
		}
		if (!StringUtility.isNullOrEmpty(request.getToDate())) {
			Date toDate = DateUtility.convertAsToDate(request.getToDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE <= :toDate ");
			params.put("toDate", toDate);
		}
		if (isSearchByEnterprise) {
			if(eIds.isEmpty()) {
				// Return empty list
				eIds.add(BigInteger.valueOf(-1000));
			}
			queryBuilder.append("AND U.BUSINESS_ID IN (:idsParams) ");
			params.put("idsParams", eIds);
		} 
		if (isSearchByRoles) {
			queryBuilder.append("AND R.ROLE_IDS IS NOT NULL ");
		}
		queryBuilder.append("ORDER BY ID DESC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "ListAccountItemMapping");
		bindParameter(params, query);
		result.setTotalRow(query.getResultList().size());
		int startPosition = (request.getPage() - 1) * request.getPageSize();
		List<ListAccountItem> lstAccount = query.setMaxResults(request.getPageSize()).setFirstResult(startPosition)
				.getResultList();
		result.setCurrentPage(request.getPage());
		result.setListData(lstAccount);
		return result;
	}

	@Override
	public PagingObject<AdminListAccountItem> searchListAccountForAdmin(long enterpriseId, long currentUser,
			AdminGetListAccountRequest request) {
		PagingObject<AdminListAccountItem> result = new PagingObject<AdminListAccountItem>();
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT ");
		queryBuilder.append("U.ID,U.USER_NAME, U.FULL_NAME, U.PERSIONAL_ID, ");
		queryBuilder.append("U.CREATED_DATE, U.STATUS, U.PHONE ");
		queryBuilder.append("FROM user U ");
		queryBuilder.append("LEFT JOIN ");
		queryBuilder.append(" (SELECT USER_ID, ROLE_ID AS ADMIN_ROLE_ID ");
		queryBuilder.append("  FROM user_roles ");
		queryBuilder.append("  WHERE IS_DEL = 0 ");
		queryBuilder.append("  AND ROLE_ID = :adminRole ");
		params.put("adminRole", RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId());
		queryBuilder.append("  ) AS R ");
		queryBuilder.append("ON U.ID = R.USER_ID ");
		queryBuilder.append("WHERE U.IS_SYSTEM = 0 ");
		queryBuilder.append("AND U.IS_DEL = 0 ");
		queryBuilder.append("AND U.BUSINESS_ID = :bussinessId ");
		queryBuilder.append("AND R.ADMIN_ROLE_ID IS NULL ");
		params.put("bussinessId", MVEUtils.convertLongToBigInteger(enterpriseId));
		queryBuilder.append("AND U.ID != :currentUser ");
		params.put("currentUser", MVEUtils.convertLongToBigInteger(currentUser));
		queryBuilder.append("ORDER BY ID DESC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "AdminListAccountItemMapping");
		bindParameter(params, query);
		result.setTotalRow(query.getResultList().size());
		int startPosition = (request.getPage() - 1) * request.getPageSize();
		List<AdminListAccountItem> lstAccount = query.setMaxResults(request.getPageSize()).setFirstResult(startPosition)
				.getResultList();
		result.setCurrentPage(request.getPage());
		result.setListData(lstAccount);
		return result;
	}

	@Override
	public PagingObject<ListAccountItem> searchListAllEnterpriseMember(SysSearchListAccountRequest request,
			List<BigInteger> eIds) {
		List<Integer> accountStatus = new ArrayList<Integer>();
		boolean isSearchByEnterprise = eIds != null;
		accountStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		accountStatus.add(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.REJECT.getValue());
		PagingObject<ListAccountItem> result = new PagingObject<ListAccountItem>();
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		queryBuilder.append("SELECT ");
		queryBuilder.append("U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, ");
		queryBuilder.append("U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,");
		queryBuilder.append("U.REJECT_REASON, U.STATUS, ");		
		queryBuilder.append(":typeEnterprise AS ACCOUNT_TYPE, ");
		params.put("typeEnterprise", GlobalConstant.AccountType.ACCOUNT_TYPE_ENTERPRISE);
		queryBuilder.append("U1.USER_NAME AS CREATED_USER_NAME ");
		queryBuilder.append("FROM user U ");
		queryBuilder.append("LEFT JOIN user U1 ON U.CREATED_USER = U1.ID ");
		queryBuilder.append("LEFT JOIN ");
		queryBuilder.append(" (SELECT USER_ID, ROLE_ID AS ADMIN_ROLE_ID ");
		queryBuilder.append("  FROM user_roles ");
		queryBuilder.append("  WHERE IS_DEL = 0 ");
		queryBuilder.append("  AND ROLE_ID = :adminRole ");
		params.put("adminRole", RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId());
		queryBuilder.append("  ) AS R ");
		queryBuilder.append("ON U.ID = R.USER_ID ");
		queryBuilder.append("WHERE U.IS_SYSTEM = 0 ");
		queryBuilder.append("AND U.IS_DEL = 0 ");
		queryBuilder.append("AND U.BUSINESS_ID > 0 ");
		queryBuilder.append("AND U.BUSINESS_ID IS NOT NULL ");
		queryBuilder.append("AND R.ADMIN_ROLE_ID IS NULL ");
		if (!StringUtility.isNullOrEmpty(request.getUsername())) {
			String searchText = request.getUsername().toUpperCase().trim();
			searchText = MVEUtils.encodeSQLSpecialCharacter(searchText);
			queryBuilder.append("AND ( ");
			queryBuilder.append("U.USER_NAME like :username ");
			params.put("username", "%" + searchText + "%");
			queryBuilder.append("OR U.EMAIL like :email ");
			params.put("email", "%" + searchText + "%");
			queryBuilder.append("OR U.PHONE like :phone ");
			params.put("phone", "%" + searchText + "%");
			queryBuilder.append(") ");
		}
		if (accountStatus.contains(request.getStatus())) {
			if (request.getStatus() == StatusDefine.AccountStatus.STATUS_ACTIVE.getValue()) {
				List<Integer> queryStatus = new ArrayList<Integer>();
				queryStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
				queryStatus.add(StatusDefine.AccountStatus.STATUS_NEW.getValue());
				queryBuilder.append("AND U.STATUS in (:queryStatus) ");
				params.put("queryStatus", queryStatus);
			} else {
				queryBuilder.append("AND U.STATUS = :status ");
				params.put("status", request.getStatus());
			}
		}
		if (!StringUtility.isNullOrEmpty(request.getFromDate())) {
			Date fromDate = DateUtility.convertAsFromDate(request.getFromDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE >= :fromDate ");
			params.put("fromDate", fromDate);
		}
		if (!StringUtility.isNullOrEmpty(request.getToDate())) {
			Date toDate = DateUtility.convertAsToDate(request.getToDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE <= :toDate ");
			params.put("toDate", toDate);
		}
		if (isSearchByEnterprise) {
			if(eIds.isEmpty()) {
				// Return empty list
				eIds.add(BigInteger.valueOf(-1000));
			}
			queryBuilder.append("AND U.BUSINESS_ID IN (:idsParams) ");
			params.put("idsParams", eIds);
		} 
		queryBuilder.append("ORDER BY ID DESC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "ListAccountItemMapping");
		bindParameter(params, query);
		result.setTotalRow(query.getResultList().size());
		int startPosition = (request.getPage() - 1) * request.getPageSize();
		List<ListAccountItem> lstAccount = query.setMaxResults(request.getPageSize()).setFirstResult(startPosition)
				.getResultList();
		result.setCurrentPage(request.getPage());
		result.setListData(lstAccount);
		return result;
	}
	
	@Override
	public PagingObject<ListAccountItem> searchListAllAccountForSysAdmin(SysSearchListAccountRequest request,
			List<BigInteger> eIds, boolean isEnterpriseOnly) {
		List<Integer> accountStatus = new ArrayList<Integer>();
		boolean isSearchByEnterprise = eIds != null;
		accountStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		accountStatus.add(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		accountStatus.add(StatusDefine.AccountStatus.REJECT.getValue());
		PagingObject<ListAccountItem> result = new PagingObject<ListAccountItem>();
		StringBuilder queryBuilder = new StringBuilder("");
		HashMap<String, Object> params = new HashMap<String, Object>();
		queryBuilder.append("SELECT ");
		queryBuilder.append("U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, ");
		queryBuilder.append("U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,");
		queryBuilder.append("U.REJECT_REASON, U.STATUS, ");
		queryBuilder.append("CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, ");
		queryBuilder.append("U1.USER_NAME AS CREATED_USER_NAME ");
		queryBuilder.append("FROM user U ");
		queryBuilder.append("LEFT JOIN user U1 ON U.CREATED_USER = U1.ID ");

		queryBuilder.append("WHERE U.IS_SYSTEM = 0 ");
		queryBuilder.append("AND U.IS_DEL = 0 ");
		if (!StringUtility.isNullOrEmpty(request.getUsername())) {
			String searchText = request.getUsername().toUpperCase().trim();
			searchText = MVEUtils.encodeSQLSpecialCharacter(searchText);
			queryBuilder.append("AND ( ");
			queryBuilder.append("U.USER_NAME like :username ");
			params.put("username", "%" + searchText + "%");
			queryBuilder.append("OR U.EMAIL like :email ");
			params.put("email", "%" + searchText + "%");
			queryBuilder.append("OR U.PHONE like :phone ");
			params.put("phone", "%" + searchText + "%");
			queryBuilder.append(") ");
		}
		if (accountStatus.contains(request.getStatus())) {
			if (request.getStatus() == StatusDefine.AccountStatus.STATUS_ACTIVE.getValue()) {
				List<Integer> queryStatus = new ArrayList<Integer>();
				queryStatus.add(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
				queryStatus.add(StatusDefine.AccountStatus.STATUS_NEW.getValue());
				queryBuilder.append("AND U.STATUS in (:queryStatus) ");
				params.put("queryStatus", queryStatus);
			} else {
				queryBuilder.append("AND U.STATUS = :status ");
				params.put("status", request.getStatus());
			}
		}
		if (!StringUtility.isNullOrEmpty(request.getFromDate())) {
			Date fromDate = DateUtility.convertAsFromDate(request.getFromDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE >= :fromDate ");
			params.put("fromDate", fromDate);
		}
		if (!StringUtility.isNullOrEmpty(request.getToDate())) {
			Date toDate = DateUtility.convertAsToDate(request.getToDate(), DateUtility.DATE_FORMAT_STR);
			queryBuilder.append("AND U.CREATED_DATE <= :toDate ");
			params.put("toDate", toDate);
		}
		if (isSearchByEnterprise) {
			if(eIds.isEmpty()) {
				// Return empty list
				eIds.add(BigInteger.valueOf(-1000));
			}
			queryBuilder.append("AND U.BUSINESS_ID IN (:idsParams) ");
			params.put("idsParams", eIds);
		} 
		if (isEnterpriseOnly) {
			queryBuilder.append("AND (U.BUSINESS_ID IS NOT NULL AND U.BUSINESS_ID > 0 ) ");
		}
		queryBuilder.append("ORDER BY ID DESC ");
		Query query = em.createNativeQuery(queryBuilder.toString(), "ListAccountItemMapping");
		bindParameter(params, query);
		result.setTotalRow(query.getResultList().size());
		int startPosition = (request.getPage() - 1) * request.getPageSize();
		List<ListAccountItem> lstAccount = query.setMaxResults(request.getPageSize()).setFirstResult(startPosition)
				.getResultList();
		result.setCurrentPage(request.getPage());
		result.setListData(lstAccount);
		return result;
	}

}
