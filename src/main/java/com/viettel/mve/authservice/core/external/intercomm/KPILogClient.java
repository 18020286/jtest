package com.viettel.mve.authservice.core.external.intercomm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.intercomm.request.AddKPILogRequest;

@FeignClient(name = "kpilog-service")
public interface KPILogClient {
	@RequestMapping(path = "/intercomm/addKPILog", method = RequestMethod.POST)
	public BaseResponse addKPILog(@RequestBody AddKPILogRequest request);

}
