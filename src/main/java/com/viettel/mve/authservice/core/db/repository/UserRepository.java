package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.MVEUser;

public interface UserRepository extends CrudRepository<MVEUser, BigInteger> {
	@Query("SELECT u FROM USER u WHERE u.userName = UPPER(:username) and u.isDelete = 0")
	MVEUser findByUserName(@Param("username") String username);

	@Query("SELECT u FROM USER u WHERE u.id = :id and u.isDelete = 0")
	MVEUser findByUserId(@Param("id") BigInteger id);

	@Query("SELECT u FROM USER u WHERE u.userName = UPPER(:username) and u.email = :email and u.isDelete = 0")
	MVEUser findByUserNameAndEmail(@Param("username") String username, @Param("email") String email);

	@Query("SELECT u FROM USER u WHERE u.email = :email and u.businessId is not null and u.isDelete = 0")
	List<MVEUser> findEnterpriseAccountByEmail(@Param("email") String email);

	@Query("SELECT u FROM USER u WHERE u.businessId = :bid and u.isDelete = 0")
	List<MVEUser> findByBusinessId(@Param("bid") BigInteger bid);
	
	@Query("SELECT u FROM USER u WHERE u.email = :email and u.status in (1, 2) and u.isDelete = 0")
	List<MVEUser> findActiveAccountByEmail(@Param("email") String email);
}
