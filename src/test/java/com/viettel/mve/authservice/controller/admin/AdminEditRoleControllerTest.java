package com.viettel.mve.authservice.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.viettel.mve.authservice.service.admin.AdminEditRoleService;
import com.viettel.mve.client.request.auth.AdminCreateRoleRequest;
import com.viettel.mve.client.request.auth.AdminDeleteRoleRequest;
import com.viettel.mve.client.request.auth.AdminUpdateRoleRequest;

@SpringBootTest
public class AdminEditRoleControllerTest {
	
	@InjectMocks
	private AdminEditRoleController adminEditRoleController;
	
	@Mock
	private AdminEditRoleService adminEditRoleService;
	
	public AdminEditRoleControllerTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void setupMock() {
      
	}
	
	
	
	@Test
    public void createRoleTest() {
		AdminCreateRoleRequest request = new AdminCreateRoleRequest();
		adminEditRoleController.createRole(0, 0, request);
		//
		request.setRoleName("123");
		adminEditRoleController.createRole(0, 0, request);
		//
		List<Long> permissions = new ArrayList<Long>();
		permissions.add(1L);
		request.setPermissions(permissions);
		adminEditRoleController.createRole(0, 0, request);
	}
	
	@Test
    public void updateRoleTest() {
		//
		AdminUpdateRoleRequest request = new AdminUpdateRoleRequest();
		adminEditRoleController.updateRole(0, 0, request);
		//
		List<Long> permissions = new ArrayList<Long>();
		permissions.add(1L);		
		request.setRoleId(1);
		adminEditRoleController.updateRole(0, 0, request);
		request.setRoleName("123");
		request.setPermissions(permissions);
		adminEditRoleController.updateRole(0, 0, request);
	}
	
	@Test
    public void deleteRoleTest() {
		AdminDeleteRoleRequest request = new AdminDeleteRoleRequest();
		adminEditRoleController.deleteRole(0, 0, request);
		request.setRoleId(1);
		adminEditRoleController.deleteRole(0, 0, request);
	}
}
