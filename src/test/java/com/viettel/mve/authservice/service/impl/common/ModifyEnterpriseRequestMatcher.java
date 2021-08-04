package com.viettel.mve.authservice.service.impl.common;

import org.mockito.ArgumentMatcher;

import com.viettel.mve.client.request.customer.ModifyEnterpriseRequest;

public class ModifyEnterpriseRequestMatcher implements ArgumentMatcher<ModifyEnterpriseRequest> {
	private ModifyEnterpriseRequest left;
	
	public ModifyEnterpriseRequestMatcher(ModifyEnterpriseRequest left) {
		this.left = left;
	}
	@Override
	public boolean matches(ModifyEnterpriseRequest right) {
		return left.getModifyUser()==right.getModifyUser();
	}

}
