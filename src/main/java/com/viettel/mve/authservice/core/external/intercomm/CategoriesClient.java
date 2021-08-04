package com.viettel.mve.authservice.core.external.intercomm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.mve.client.request.categories.GetDetailAddressRequest;
import com.viettel.mve.client.request.categories.GetDetailProvinceRequest;
import com.viettel.mve.client.request.categories.GetListCategoriesRequest;
import com.viettel.mve.client.response.categories.GetDetailAddressResponse;
import com.viettel.mve.client.response.categories.GetDetailProvinceResponse;
import com.viettel.mve.client.response.categories.GetListCategoriesResponse;
import com.viettel.mve.client.response.categories.GetListProvinceResponse;

@FeignClient(name = "categories-service")
public interface CategoriesClient {
	@RequestMapping(path = "/public/getListCategories", method = RequestMethod.POST)
	public GetListCategoriesResponse getListCategories(@RequestBody GetListCategoriesRequest request);
	
	@RequestMapping(path = "/public/getDetailProvince", method = RequestMethod.POST)
	public GetDetailProvinceResponse getDetailProvince(@RequestBody GetDetailProvinceRequest request);
	
	@RequestMapping(path = "/public/getListOnlyProvince", method = RequestMethod.GET)
	public GetListProvinceResponse getListOnlyProvince();
	
	@RequestMapping(path = "/public/getDetailAddress", method = RequestMethod.POST)
	public GetDetailAddressResponse getDetailAddress(@RequestBody GetDetailAddressRequest request);
}
