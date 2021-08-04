package com.viettel.mve.authservice.controller.sysadmin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.viettel.mve.authservice.service.sysadmin.SysAdminViewAccountService;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;

@SpringBootTest
public class SysAdminViewAccountControllerTest {
	
	@InjectMocks
	private SysAdminViewAccountController sysAdminViewAccountController;
	
	@Mock
	private SysAdminViewAccountService viewAccountService;
	public SysAdminViewAccountControllerTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void setupMock() {
      
	}
	
	
	
	@Test
    public void searchListAccountTest() {
		SysSearchListAccountRequest request = new SysSearchListAccountRequest();
		
		sysAdminViewAccountController.searchListAccount(0,request);
		request.setFromDate("01/02/2019");
		sysAdminViewAccountController.searchListAccount(0,request);
		
		request.setToDate("28/02/2019");
		sysAdminViewAccountController.searchListAccount(0,request);
	}
	
	@Test
    public void exportExcelListAccountTest() {
		SysExportExcelListAccountRequest request = new SysExportExcelListAccountRequest();
		
		sysAdminViewAccountController.exportExcelListAccount(0,request,null);
		
	}
	
	@Test
    public void viewAccountInforTest() {
		AdminViewAccountRequest request = new AdminViewAccountRequest();
		sysAdminViewAccountController.viewAccountInfor("",request);
		
	}
	
	@Test
    public void getListRoleByUserTest() {
		AdminGetListRoleByUserRequest request = new AdminGetListRoleByUserRequest();
		sysAdminViewAccountController.getListRoleByUser(request);
		
	}
	
	@Test
    public void getDetailRolesByUserTest() {
		AdminGetListRoleByUserRequest request = new AdminGetListRoleByUserRequest();
		sysAdminViewAccountController.getDetailRolesByUser(request);
		
	}
	
	@Test
    public void getListProvinceByAccountManagementTest() {
		sysAdminViewAccountController.getListProvinceByAccountManagement(1);
	}
	
	
}
