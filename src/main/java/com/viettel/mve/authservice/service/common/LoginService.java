package com.viettel.mve.authservice.service.common;

import com.viettel.mve.client.request.auth.LoginRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface LoginService {
	public BaseResponse login(LoginRequest request);
}
