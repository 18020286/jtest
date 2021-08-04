package com.viettel.mve.authservice.core.external.intercomm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.mve.client.request.notification.SendMultipleEmailRequest;
import com.viettel.mve.client.response.BaseResponse;

@FeignClient(name = "notification-service")
public interface NotificationClient {
	@RequestMapping(value = "/public/sendMultipleEmail", method = RequestMethod.POST)
	public BaseResponse sendMultipleEmail(@RequestBody SendMultipleEmailRequest request);

}
