package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.Attach;

public interface AttachRepository extends CrudRepository<Attach, BigInteger> {
	@Query("SELECT a FROM ATTACH a WHERE a.userId = :userId and a.isDelete = 0")
	List<Attach> findByUserId(@Param("userId") BigInteger userId);

	@Query("SELECT a FROM ATTACH a WHERE a.userId = :userId and a.attachType = :type and a.isDelete = 0")
	Attach findByUserIdAndType(@Param("userId") BigInteger userId, @Param("type") int type);
	
	@Query("SELECT a FROM ATTACH a WHERE a.mediaId in (:mediaIds) and a.isDelete = 0")
	List<Attach> findByMediaIds(@Param("mediaIds") List<BigInteger> mediaIds);
}
