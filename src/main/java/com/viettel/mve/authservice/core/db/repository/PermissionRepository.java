package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.viettel.mve.authservice.core.db.entities.Permission;

public interface PermissionRepository extends CrudRepository<Permission, BigInteger> {
}
