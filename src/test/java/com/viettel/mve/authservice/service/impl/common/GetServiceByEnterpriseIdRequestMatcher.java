package com.viettel.mve.authservice.service.impl.common;

import org.mockito.ArgumentMatcher;

import com.viettel.mve.common.intercomm.request.GetServiceByEnterpriseIdRequest;

public class GetServiceByEnterpriseIdRequestMatcher implements ArgumentMatcher<GetServiceByEnterpriseIdRequest> {
	private GetServiceByEnterpriseIdRequest left;
	
	public GetServiceByEnterpriseIdRequestMatcher(GetServiceByEnterpriseIdRequest left) {
		this.left = left;
	}
	@Override
	public boolean matches(GetServiceByEnterpriseIdRequest right) {
		return left.getEnterpriseId()==right.getEnterpriseId();
	}

}
