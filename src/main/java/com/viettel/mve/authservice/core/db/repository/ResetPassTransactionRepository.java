package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.viettel.mve.authservice.core.db.entities.ResetPassTransaction;

public interface ResetPassTransactionRepository extends CrudRepository<ResetPassTransaction, BigInteger> {
	@Query("SELECT rpt FROM RESET_PASS_TRANSACTION rpt WHERE rpt.resetPassCode = (:code) and rpt.isDelete = 0")
	ResetPassTransaction findByResetPassCode(@Param("code") String code);
}
