package com.viettel.mve.authservice.service.impl.common;

import org.mockito.ArgumentMatcher;

import com.viettel.mve.common.intercomm.request.GetEnterpriseByIDReq;

public class GetEnterpriseByIDReqMatcher implements ArgumentMatcher<GetEnterpriseByIDReq> {
	private GetEnterpriseByIDReq left;
	
	public GetEnterpriseByIDReqMatcher(GetEnterpriseByIDReq left) {
		this.left = left;
	}
	@Override
	public boolean matches(GetEnterpriseByIDReq right) {
		return left.getEnterpriseId()==right.getEnterpriseId();
	}

}
