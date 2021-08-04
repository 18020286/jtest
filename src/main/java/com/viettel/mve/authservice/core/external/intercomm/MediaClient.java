package com.viettel.mve.authservice.core.external.intercomm;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.media.UploadMediaResponse;
import com.viettel.mve.common.intercomm.request.DeleteFileByPathRequest;
import com.viettel.mve.common.intercomm.request.DeleteListMediaRequest;

@FeignClient(name = "media-service")
public interface MediaClient {
	@RequestMapping(value = "/intercomm/uploadAccountAvartar", method = RequestMethod.POST)
	public UploadMediaResponse uploadAccountAvartar(Map<String, ?> file,
			@RequestHeader("enterpriseId") long enterpriseId, @RequestHeader("avartarUser") long avartarUser);

	@RequestMapping(value = "/intercomm/uploadAccountDocument", method = RequestMethod.POST)
	public UploadMediaResponse uploadAccountDocument(Map<String, ?> files,
			@RequestHeader("enterpriseId") long enterpriseId, @RequestHeader("uploadUser") long uploadUser,
			@RequestHeader("documentUser") long documentUser);

	@RequestMapping(value = "/intercomm/deletePublicFileByPath", method = RequestMethod.POST)
	public BaseResponse deletePublicFileByPath(@RequestBody DeleteFileByPathRequest request);

	@RequestMapping(value = "/intercomm/uploadRegisAccountDocument", method = RequestMethod.POST)
	public UploadMediaResponse uploadRegisAccountDocument(Map<String, ?> files,
			@RequestHeader("enterpriseId") long enterpriseId, @RequestHeader("uploadUser") long uploadUser,
			@RequestHeader("documentUser") long documentUser);
	
	@RequestMapping(value = "/intercomm/uploadRegisAccountDocumentV2", method = RequestMethod.POST)
	public UploadMediaResponse uploadRegisAccountDocumentV2(Map<String, ?> files,
			@RequestHeader("enterpriseId") long enterpriseId, @RequestHeader("uploadUser") long uploadUser,
			@RequestHeader("documentUser") long documentUser);
	
	@RequestMapping(value = "/intercomm/deleteListMedia", method = RequestMethod.POST)
	public BaseResponse deleteListMedia(@RequestBody DeleteListMediaRequest request);

}
