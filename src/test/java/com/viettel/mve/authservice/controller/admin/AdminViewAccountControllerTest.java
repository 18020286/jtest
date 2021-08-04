package com.viettel.mve.authservice.controller.admin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.viettel.mve.authservice.service.admin.AdminViewAccountService;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;

@SpringBootTest
public class AdminViewAccountControllerTest {
	
	@InjectMocks
	private AdminViewAccountController adminViewAccountController;
	
	@Mock
	private AdminViewAccountService viewAccountService;
	
	public AdminViewAccountControllerTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void setupMock() {
      
	}
	
	
	
	@Test
    public void searchListAccountTest() {
		AdminGetListAccountRequest request = new AdminGetListAccountRequest();
		adminViewAccountController.searchListAccount(0, "", "", 0, request);
	}
	
	@Test
    public void viewAccountInforTest() {
		AdminViewAccountRequest request = new AdminViewAccountRequest();
		adminViewAccountController.viewAccountInfor(0, "", "",  request);
		request.setUserId(1);
		adminViewAccountController.viewAccountInfor(0, "", "",  request);
	}
	
	@Test
    public void getListRoleByUserTest() {
		AdminGetListRoleByUserRequest request = new AdminGetListRoleByUserRequest();
		adminViewAccountController.getListRoleByUser(0, request);
	}
	
}
