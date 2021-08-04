package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.client.response.auth.object.ListAccountItem;

public interface UserRepositoryCustom {
	PagingObject<ListAccountItem> searchListAllAccountForSysAdmin(SysSearchListAccountRequest request,
			List<BigInteger> eIds, boolean isEnterpriseOnly);

	PagingObject<ListAccountItem> searchListAccountForSysAdmin(SysSearchListAccountRequest request,
			List<BigInteger> eIds, List<BigInteger> rIds);

	PagingObject<ListAccountItem> searchListAllEnterpriseMember(SysSearchListAccountRequest request,
			List<BigInteger> eIds);

	PagingObject<AdminListAccountItem> searchListAccountForAdmin(long enterpriseId, long currentUser,
			AdminGetListAccountRequest request);
}
