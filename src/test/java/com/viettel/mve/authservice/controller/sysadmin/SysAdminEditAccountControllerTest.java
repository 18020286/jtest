package com.viettel.mve.authservice.controller.sysadmin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.service.sysadmin.SysAdminEditAccountService;
import com.viettel.mve.authservice.service.sysadmin.SysAdminViewAccountService;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;

@SpringBootTest
public class SysAdminEditAccountControllerTest {
	
	@InjectMocks
	private SysAdminEditAccountController sysAdminViewAccountController;
	
	@Mock
	private SysAdminEditAccountService editAccountService;
	
	public SysAdminEditAccountControllerTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void setupMock() {
      
	}
	
	
	
	@Test
    public void createAccountTest() {
		String accountInfor="{  \"roleId\": 0,  \"enterpriseIdentify\": \"\",  \"username\": \"\",  \"fullName\": \"\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"\",  \"email\": \"\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 0,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"\",  \"email\": \"\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 0,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"\",  \"email\": \"\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 1,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"\",  \"email\": \"\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 1,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 1,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"0968151947\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		//int status = RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId();
		accountInfor="{  \"roleId\": 4,  \"enterpriseIdentify\": \"\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"0968151947\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		accountInfor="{  \"roleId\": 1,  \"enterpriseIdentify\": \"123\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"22-02-2011\",  \"phone\": \"0968151947\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, null, accountInfor);
		
		
		MultipartFile[] files = null;
		
		accountInfor="{  \"roleId\": 4,  \"enterpriseIdentify\": \"123\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"\",  \"phone\": \"0968151947\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, files, accountInfor);
		
		MultipartFile file = Mockito.mock(MultipartFile.class);
		files = new MultipartFile []{file};
		
		
		accountInfor="{  \"roleId\": 1,  \"enterpriseIdentify\": \"123\",  \"username\": \"hungvq5\",  \"fullName\": \"vo quoc hung\",  \"address\": \"123\",  \"birthday\": \"20/10/2011\",  \"phone\": \"0968151947\",  \"email\": \"hungvq5@gmail.com\",  \"personalId\": \"123\",  \"sex\": 1}";
		sysAdminViewAccountController.createAccount(0, null, files, accountInfor);
	}
	
	@Test
	public void importAccontTest() throws IOException, Exception {
		long updateUser = 0;
		MultipartFile file = Mockito.mock(MultipartFile.class);
		sysAdminViewAccountController.importAccount(updateUser, file);
	}
	
	@Test
	public void exportTemplateTest() throws IOException, Exception {
		HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
		sysAdminViewAccountController.downloadTemplate(mockRequest);
	}
	
	@Test
    public void updateAccountInformationTest() {
		//sysAdminViewAccountController.updateAccountInformation(0, null, files, accountInfor);
	}

	@Test
	public void importAccontTest_T6() throws IOException, Exception {
		long updateUser = 0;
		MultipartFile file = Mockito.mock(MultipartFile.class);
		sysAdminViewAccountController.importAccount(updateUser, file);
	}

	@Test
	public void exportTemplateTest_T6() throws IOException, Exception {
		HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
		sysAdminViewAccountController.downloadTemplate(mockRequest);
	}
	
}
