package com.viettel.mve.authservice.service.admin;


import java.io.IOException;

import javax.ws.rs.core.Application;

import com.viettel.mve.authservice.common.importUtil.DynamicExport;
import com.viettel.mve.authservice.common.importUtil.TemplateResouces;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.service.impl.sysadmin.SysAdminEditAccountServiceImpl;

@SpringBootTest
public class AdminEditAccountServiceTest{
	public AdminEditAccountServiceTest() {
		MockitoAnnotations.initMocks(this);
	}
	@InjectMocks
	private SysAdminEditAccountServiceImpl service;


	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
    public void downloadTemplateImportAccountTest() throws IOException, Exception {	
		ConfigValue configValue = new ConfigValue();
		configValue.setPathTmp((new ClassPathResource("tmp")).toString());
		service.downloadTemplateImportAccount(configValue);
    }
	
	@Test
	public void actionImportAccountTest() throws IOException, Exception {	
		long updateUser = 0;
//		MultipartFile file = Mockito.mock(MultipartFile.class);
		ConfigValue configValue = new ConfigValue();
		configValue.setPathTmp((new ClassPathResource("tmp")).toString());
		ResponseEntity<InputStreamResource> fileImport =  service.downloadTemplateImportAccount(configValue);
		MultipartFile file = new MockMultipartFile("temp.xls","temp.xls","", fileImport.getBody().getInputStream());
		service.actionImportAccount(updateUser, file, configValue);
	}
}
