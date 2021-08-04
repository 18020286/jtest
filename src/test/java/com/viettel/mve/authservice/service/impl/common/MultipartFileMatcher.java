package com.viettel.mve.authservice.service.impl.common;

import java.util.Map;

import org.mockito.ArgumentMatcher;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileMatcher implements ArgumentMatcher<Map<String, MultipartFile>> {
	private Map<String, MultipartFile> left;
	
	public MultipartFileMatcher(Map<String, MultipartFile> left) {
		this.left = left;
	}
	@Override
	public boolean matches(Map<String, MultipartFile> right) {
		for(Map.Entry<String, MultipartFile> entry : right.entrySet()) {
		    String key = entry.getKey();
		    //MultipartFile value = entry.getValue();
		    if(!left.containsKey(key)) {
		    	return false;
		    }
		    // do what you have to do here
		    // In your case, another loop.
		}
		return true;
	}

}
