package com.viettel.mve.authservice.service.impl.common;

import org.mockito.ArgumentMatcher;

import com.viettel.mve.authservice.core.db.entities.MVEUser;

public class MVEUserMatcher implements ArgumentMatcher<MVEUser> {
	private MVEUser left;
	
	public MVEUserMatcher(MVEUser left) {
		this.left = left;
	}
	@Override
	public boolean matches(MVEUser right) {
		return left.getUserName().equals(right.getUserName());
	}

}
