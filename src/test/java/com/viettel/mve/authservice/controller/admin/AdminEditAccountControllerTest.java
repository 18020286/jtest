package com.viettel.mve.authservice.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.service.admin.AdminEditAccountService;
import com.viettel.mve.client.request.auth.AdminDeleteAccountRequest;
import com.viettel.mve.client.request.auth.AdminResetPassRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateUserStatusRequest;
import com.viettel.mve.client.request.auth.UpdateAccountRequest;
import com.viettel.mve.client.request.auth.object.UserStatus;

@SpringBootTest
public class AdminEditAccountControllerTest {
	
	@InjectMocks
	private AdminEditAccountController adminEditRoleController;
	
	@Mock
	private AdminEditAccountService editAccountService;
	
	public AdminEditAccountControllerTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void setupMock() {
      
	}
	
	
	
	@Test
    public void createRoleTest() {
		String accountInfor="{\"username\": \"\",  \"fullName\": \"\",  \"phone\": \"\",  \"personalId\": \"123\",  \"position\": \"dev\",  \"sex\": 1}";
		adminEditRoleController.createAccount(0,0,  null, accountInfor);
		
		accountInfor="{\"username\": \"hung\",  \"fullName\": \"\",  \"phone\": \"\",  \"personalId\": \"123\",  \"position\": \"dev\",  \"sex\": 1}";
		adminEditRoleController.createAccount(0,0,  null, accountInfor);
		
		accountInfor="{\"username\": \"hung\",  \"fullName\": \"vo hung\",  \"phone\": \"\",  \"personalId\": \"123\",  \"position\": \"dev\",  \"sex\": 1}";
		adminEditRoleController.createAccount(0,0,  null, accountInfor);
		
		accountInfor="{\"username\": \"hung\",  \"fullName\": \"vo hung\",  \"phone\": \"0968151947\",  \"personalId\": \"123\",  \"position\": \"dev\",  \"sex\": 1}";
		adminEditRoleController.createAccount(0,0,  null, accountInfor);
		
	}
	
	@Test
    public void updateAccountTest() {
		UpdateAccountRequest request = new UpdateAccountRequest();
		adminEditRoleController.updateAccountInformation(0,0, request);
		request.setFullName("full name");
		adminEditRoleController.updateAccountInformation(0,0, request);
		request.setPhone("0968");
		adminEditRoleController.updateAccountInformation(0,0, request);		
	}
	
	@Test
    public void updateAvartarTest() {
		MultipartFile avatar = null;
		adminEditRoleController.updateAvartar(0,0, avatar,0);
		avatar = Mockito.mock(MultipartFile.class);
		adminEditRoleController.updateAvartar(0,0, avatar,0);
	}
	
	@Test
    public void resetPasswordTest() {
		AdminResetPassRequest request = new AdminResetPassRequest();
		adminEditRoleController.resetPassword(0,0, request);
		request.setUserId(1);
		adminEditRoleController.resetPassword(0,0, request);
	}
	
	@Test
    public void updateStatusTest() {
		//
		AdminUpdateUserStatusRequest request = new AdminUpdateUserStatusRequest();
		adminEditRoleController.updateStatus(0,0, request);
		//
		List<UserStatus> lstUserStatus = new ArrayList<UserStatus>();
		lstUserStatus.add(new UserStatus());
		request.setLstUserStatus(lstUserStatus);
		adminEditRoleController.updateStatus(0,0, request);
		//
		lstUserStatus = new ArrayList<UserStatus>();
		for(int i=1;i<3;i++) {
			UserStatus item = new UserStatus();
			item.setUserId(i);
			item.setStatus(1);
			lstUserStatus.add(item);
		}
		request.setLstUserStatus(lstUserStatus);
		adminEditRoleController.updateStatus(0,0, request);
	}
	
	@Test
    public void updateUserRoleTest() {
		AdminUpdateUserRoleRequest request = new AdminUpdateUserRoleRequest();
		adminEditRoleController.updateUserRole(0,0, request);
		request.setUserId(1);
		adminEditRoleController.updateUserRole(0,0, request);
	}
	
	@Test
    public void deleteAccountTest() {
		AdminDeleteAccountRequest request = new AdminDeleteAccountRequest();
		adminEditRoleController.deleteAccount(0,0, request);
		request.setUserId(1);
		adminEditRoleController.deleteAccount(0,0, request);
	}
}
