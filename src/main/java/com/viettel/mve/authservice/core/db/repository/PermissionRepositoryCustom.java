package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import com.viettel.mve.client.response.auth.object.PermissionItem;

public interface PermissionRepositoryCustom {
	List<PermissionItem> getListPermissionByType(String langKey, int permissionType);

	List<PermissionItem> getPermissionByIds(String langKey, List<BigInteger> ids);
}
