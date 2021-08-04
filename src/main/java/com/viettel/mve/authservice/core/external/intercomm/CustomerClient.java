package com.viettel.mve.authservice.core.external.intercomm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.mve.client.request.auth.sysadmin.SysSearchEnterpriseRequest;
import com.viettel.mve.client.request.customer.ModifyEnterpriseRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.constant.JwtDefine;
import com.viettel.mve.common.intercomm.request.DeleteEnterpriseByIDReq;
import com.viettel.mve.common.intercomm.request.EnterpriseCacheRequest;
import com.viettel.mve.common.intercomm.request.GetEnterpriseByIDReq;
import com.viettel.mve.common.intercomm.request.GetServiceByEnterpriseIdRequest;
import com.viettel.mve.common.intercomm.request.SearchEnterpriseByIDSReq;
import com.viettel.mve.common.intercomm.response.GetActiveServiceByEnterpriseResponse;
import com.viettel.mve.common.intercomm.response.GetAllServiceByEnterpriseResponse;
import com.viettel.mve.common.intercomm.response.GetEnterpriseByIDResp;
import com.viettel.mve.common.intercomm.response.ModifyEnterpriseResp;
import com.viettel.mve.common.intercomm.response.SearchEnterpriseResp;

@FeignClient(name = "customer-service")
public interface CustomerClient {
	@RequestMapping(value = "/intercomm/getEnterprise", method = RequestMethod.POST)
	public GetEnterpriseByIDResp getEnterpriseById(@RequestBody GetEnterpriseByIDReq request);

	@RequestMapping(value = "/intercomm/getEnterpriseCache", method = RequestMethod.POST)
	public ModifyEnterpriseResp getEnterpriseCache(@RequestBody EnterpriseCacheRequest request);

	@RequestMapping(value = "/intercomm/getListServiceByEnterprise", method = RequestMethod.POST)
	public GetAllServiceByEnterpriseResponse getListServiceByEnterprise(
			@RequestBody GetServiceByEnterpriseIdRequest request);

	@RequestMapping(value = "/intercomm/getListActiveServiceByEnterprise", method = RequestMethod.POST)
	public GetActiveServiceByEnterpriseResponse getListActiveServiceByEnterprise(
			@RequestBody GetServiceByEnterpriseIdRequest request);

	@RequestMapping(value = "/intercomm/deleteEnterpriseById", method = RequestMethod.POST)
	public BaseResponse deleteEnterpriseById(@RequestBody DeleteEnterpriseByIDReq request);

	@RequestMapping(value = "/intercomm/deleteEnterpriseCache", method = RequestMethod.POST)
	public BaseResponse deleteEnterpriseCache(@RequestBody EnterpriseCacheRequest request);

	@RequestMapping(value = "/intercomm/createEnterprise", method = RequestMethod.POST)
	public ModifyEnterpriseResp createEnterprise(@RequestBody ModifyEnterpriseRequest request);

	@RequestMapping(value = "/intercomm/updateApproveEnterprise", method = RequestMethod.POST)
	public ModifyEnterpriseResp updateApproveEnterprise(@RequestBody ModifyEnterpriseRequest request);

	@RequestMapping(value = "/intercomm/searchEnterprise", method = RequestMethod.POST)
	public SearchEnterpriseResp searchEnterprise(@RequestBody SysSearchEnterpriseRequest request);
	
	@RequestMapping(value = "/intercomm/searchEnterpriseByIds", method = RequestMethod.POST)
	public SearchEnterpriseResp searchEnterpriseByIds(@RequestBody SearchEnterpriseByIDSReq request);
	
	@RequestMapping(value = "/updateBussinessType", method = RequestMethod.POST)
	public BaseResponse updateBussinessType(@RequestHeader(name = JwtDefine.KEY_USER_ID) long uid,
			@RequestHeader(name = JwtDefine.KEY_ENTERPRISE_ID) long cid,
			@RequestBody ModifyEnterpriseRequest request);
	
	@RequestMapping(value = "/intercomm/updateRejectEnterprise", method = RequestMethod.POST)
	public ModifyEnterpriseResp updateRejectEnterprise(@RequestBody ModifyEnterpriseRequest request);

	@RequestMapping(value = "/intercomm/getListActiveEnterpriseByIds", method = RequestMethod.POST)
	public SearchEnterpriseResp getListActiveEnterpriseByIds(@RequestBody SearchEnterpriseByIDSReq request);

}
