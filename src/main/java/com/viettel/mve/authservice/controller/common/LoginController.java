package com.viettel.mve.authservice.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.common.LoginService;
import com.viettel.mve.client.request.auth.LoginRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.spring.BaseController;

@RestController
public class LoginController extends BaseController {

	@Autowired
	private LoginService loginService;

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest request) {
		BaseResponse response = AuthValidateUtils.validateLoginRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			return responseOkStatus(loginService.login(request));
		}
	}
}
