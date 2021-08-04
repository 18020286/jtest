package com.viettel.mve.authservice.service.common;

import com.viettel.mve.client.request.auth.ResetPassRequest;
import com.viettel.mve.client.response.BaseResponse;

public interface ResetPassService {
	BaseResponse getResetPassCode(String username, String email);
	
	BaseResponse resetPassword(ResetPassRequest request);
}
