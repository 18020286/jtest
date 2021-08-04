package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.client.response.auth.object.RoleItem;

public interface RoleRepository extends CrudRepository<Role, BigInteger> {
	@Query("SELECT r FROM ROLE r WHERE r.isDelete = 0")
	List<Role> findAllRole() throws Exception;

	@Query("SELECT r FROM ROLE r WHERE r.id IN :ids AND r.isDelete = 0 ")
	List<Role> findRolesByIds(@Param("ids") List<BigInteger> ids);

	@Query("SELECT r FROM ROLE r WHERE UPPER(r.roleName) = :roleName AND r.businessId = :businessId AND r.isDelete = 0 ")
	List<Role> findRolesByNameAndEnterprise(@Param("roleName") String roleName,
			@Param("businessId") BigInteger businessId);

	@Query("SELECT r FROM ROLE r WHERE UPPER(r.roleName) = :roleName AND r.businessId = :businessId AND r.id != :excludeId AND r.isDelete = 0 ")
	List<Role> findRolesByNameAndEnterpriseExcludeId(@Param("roleName") String roleName,
			@Param("businessId") BigInteger businessId, @Param("excludeId") BigInteger excludeId);

	@Query("SELECT r FROM ROLE r WHERE UPPER(r.roleName) = :roleName AND r.id != :excludeId AND r.isDelete = 0 ")
	List<Role> findRolesByNameExcludeId(@Param("roleName") String roleName, @Param("excludeId") BigInteger excludeId);

	@Query("SELECT r FROM ROLE r WHERE UPPER(r.roleName) = :roleName AND r.isDelete = 0 ")
	List<Role> findRolesByName(@Param("roleName") String roleName);

	@Query("SELECT r FROM ROLE r WHERE r.businessId is not null AND r.businessId > 0 AND r.isDelete = 0 ")
	List<Role> findAllEnterpriseRoles();

	@Query(nativeQuery = true)
	List<RoleItem> getRolesByEnterprise(BigInteger enterpriseId);

	@Query(nativeQuery = true)
	RoleItem getRoleByID(BigInteger roleId) throws Exception;

	@Query(nativeQuery = true)
	List<RoleItem> getListSysAdminRole();

	@Query(nativeQuery = true)
	List<RoleItem> getRoleByIDS(List<BigInteger> ids);
	
	Role findByRoleCode(String roleCode);
}
