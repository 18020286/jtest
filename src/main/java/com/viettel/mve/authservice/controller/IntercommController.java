package com.viettel.mve.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.intercomm.request.CheckRequireLoginRequest;
import com.viettel.mve.common.spring.BaseController;

@RestController
@RequestMapping("/intercomm")
public class IntercommController extends BaseController {
	@Autowired
	private AccountService accountService;

	@RequestMapping(path = "/checkRequireLogin", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> checkRequireLogin(@RequestBody CheckRequireLoginRequest request) {
		return responseOkStatus(accountService.checkRequireLogin(request));
	}
}
