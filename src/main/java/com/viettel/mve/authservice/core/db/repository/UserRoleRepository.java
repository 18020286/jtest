package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, BigInteger> {
	@Query("SELECT ur FROM USER_ROLES ur WHERE ur.userId = :userId and ur.isDelete = 0")
	List<UserRole> findByUserId(@Param("userId") BigInteger userId);

	@Query("SELECT ur FROM USER_ROLES ur WHERE ur.roleId = :roleId and ur.isDelete = 0")
	List<UserRole> findByRoleId(@Param("roleId") BigInteger roleId);

	@Query("SELECT ur FROM USER_ROLES ur WHERE ur.userId = :userId and ur.roleId = :roleId and ur.isDelete = 0")
	List<UserRole> findByUserAndRole(@Param("userId") BigInteger userId, @Param("roleId") BigInteger roleId);
}
