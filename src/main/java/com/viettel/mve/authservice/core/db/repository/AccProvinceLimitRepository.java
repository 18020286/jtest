package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.AccountProvinceLimit;

public interface AccProvinceLimitRepository extends CrudRepository<AccountProvinceLimit, BigInteger> {
	@Query("SELECT ap FROM ACCOUNT_PROVINCE_LIMIT ap WHERE ap.userId = :userId and ap.isDelete = 0")
	List<AccountProvinceLimit> findProvinceByAccount(@Param("userId") BigInteger userId);
}
