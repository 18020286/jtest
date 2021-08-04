package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

public interface UserRoleRepositoryCustom {
	List<String> findPermissionCodesByUser(BigInteger userId);

	String findRoleCodesByUser(BigInteger userId);
}
