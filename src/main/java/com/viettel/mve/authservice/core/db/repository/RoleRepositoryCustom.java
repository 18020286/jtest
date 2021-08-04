package com.viettel.mve.authservice.core.db.repository;

import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;

public interface RoleRepositoryCustom {
	PagingObject<SysAdminRoleItem> searchListRoleForSysAdmin(SysAdminSearchRoleRequest request, String langKey);
}
