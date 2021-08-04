package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.RolePermission;

public interface RolePermissionRepository extends CrudRepository<RolePermission, BigInteger> {
	@Query("SELECT rp FROM ROLE_PERMISSIONS rp WHERE rp.roleId = :roleId and rp.isDelete = 0")
	List<RolePermission> findRolePermissionByRole(@Param("roleId") BigInteger roleId);

	@Query("SELECT rp FROM ROLE_PERMISSIONS rp WHERE rp.roleId in (:roleIds) and rp.isDelete = 0")
	List<RolePermission> findRolePermissionByRoles(@Param("roleIds") List<BigInteger> roleIds);

}
