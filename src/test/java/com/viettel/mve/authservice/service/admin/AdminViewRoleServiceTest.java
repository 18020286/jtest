package com.viettel.mve.authservice.service.admin;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.viettel.mve.authservice.core.db.entities.Permission;
import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.repository.PermissionRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.RolePermissionRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.service.impl.admin.AdminViewRoleServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.request.auth.AdminGetListRoleDetailRequest;
import com.viettel.mve.client.response.auth.AdminGetAllPermissionResponse;
import com.viettel.mve.client.response.auth.AdminGetListRoleDetailResponse;
import com.viettel.mve.client.response.auth.AdminListRoleResponse;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.client.response.auth.object.RoleItem;
import com.viettel.mve.common.utils.MVEUtils;

@SpringBootTest
public class AdminViewRoleServiceTest{
	public AdminViewRoleServiceTest() {
		MockitoAnnotations.initMocks(this);
		roleIds.add(1L);
		roleDetailRequest.setRoleIds(roleIds);
	}
	private List<Long> roleIds = new ArrayList<Long>();
	private AdminGetListRoleDetailRequest roleDetailRequest = new AdminGetListRoleDetailRequest();

	@Mock
	private RolePermissionRepository rolePermissionRepository;
	
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private PermissionRepositoryCustom permissionRepository;
	
	
	@InjectMocks
	private AdminViewRoleService service = new AdminViewRoleServiceImpl();
	
	@Before
    public void getListRoleDetailMock() {		
		List<BigInteger> roleIdBigs = new ArrayList<BigInteger>();
		for (long rId : roleIds) {
			roleIdBigs.add(MVEUtils.convertLongToBigInteger(rId));
		}
    	List<RolePermission> rolePers = null;    	
    	when(rolePermissionRepository.findRolePermissionByRoles(roleIdBigs)).thenReturn(rolePers);
    	
    	List<PermissionItem> list = null;
    	when(permissionRepository.getPermissionByIds(null,null)).thenReturn(list);
    	
    	list = new ArrayList<PermissionItem>();
    	when(permissionRepository.getPermissionByIds("",null)).thenReturn(list);
    	
    	list = new ArrayList<PermissionItem>();
    	for(int i =0;i<10;i++) {
    		PermissionItem roleItem = new PermissionItem();
    		roleItem.setPermissionId(i);
    		roleItem.setPermissionName("Name"+i);
    		list.add(roleItem);
    	}
    	when(permissionRepository.getPermissionByIds("vi",null)).thenReturn(list);    	
    }
	
	
	
	@Before
    public void getListRolesMock() {		
    	List<RoleItem> list = null;
    	when(roleRepository.getRolesByEnterprise(MVEUtils.convertLongToBigInteger(-1L))).thenReturn(list);
    	
    	list = new ArrayList<RoleItem>();
    	when(roleRepository.getRolesByEnterprise(MVEUtils.convertLongToBigInteger(0L))).thenReturn(list);
    	
    	list = new ArrayList<RoleItem>();
    	for(int i =0;i<10;i++) {
    		RoleItem roleItem = new RoleItem();
    		roleItem.setRoleId(i);
    		roleItem.setRoleName("Name:"+i);
    		roleItem.setDescription("des");
    		roleItem.setRoleName("roleName");
    		list.add(roleItem);
    	}
    	
    	when(roleRepository.getRolesByEnterprise(MVEUtils.convertLongToBigInteger(10L))).thenReturn(list);
    }
	
	@Before
    public void getAllPermissionMock() {
		List<PermissionItem> lstPermissions = null;
		//
    	when(permissionRepository.getListPermissionByType(null,Permission.PERMISSION_TYPE_ENTERPRISE)).thenReturn(lstPermissions);
    	//    	
    	lstPermissions = new ArrayList<PermissionItem>();
    	when(permissionRepository.getListPermissionByType("",Permission.PERMISSION_TYPE_ENTERPRISE)).thenReturn(lstPermissions);
    	
    	lstPermissions = new ArrayList<PermissionItem>();
    	for(int i =0;i<10;i++) {
    		PermissionItem item = new PermissionItem();
    		item.setPermissionId(i);;
    		item.setPermissionName("Name:"+i);
    		lstPermissions.add(item);
    	}
    	
    	when(permissionRepository.getListPermissionByType("vi",Permission.PERMISSION_TYPE_ENTERPRISE)).thenReturn(lstPermissions);
    }
	
	
	@Test
    public void getAllPermissionTest() {	
		
		AdminGetAllPermissionResponse resNull = (AdminGetAllPermissionResponse)service.getAllPermission(null);
		assertEquals(ErrorDefine.OK, resNull.getErrorCode());
		assertEquals(null, resNull.getPermissions());
				
		AdminGetAllPermissionResponse resEmpty = (AdminGetAllPermissionResponse)service.getAllPermission("");
		assertEquals(ErrorDefine.OK, resEmpty.getErrorCode());
		assertEquals(0, resEmpty.getPermissions().size());
		
		
		AdminGetAllPermissionResponse resOne = (AdminGetAllPermissionResponse)service.getAllPermission("vi");
		assertEquals(ErrorDefine.OK, resOne.getErrorCode());
		assertEquals(10, resOne.getPermissions().size());	
    }
	
	
	@Test
    public void getListRolesTest() {	
		AdminListRoleResponse resNull = (AdminListRoleResponse)service.getListRoles(-1);
		assertEquals(ErrorDefine.OK, resNull.getErrorCode());
		assertEquals(null, resNull.getLstRoles());
				
		AdminListRoleResponse resEmpty = (AdminListRoleResponse)service.getListRoles(0);
		assertEquals(ErrorDefine.OK, resEmpty.getErrorCode());
		assertEquals(0, resEmpty.getLstRoles().size());
		
		AdminListRoleResponse resOne = (AdminListRoleResponse)service.getListRoles(10);
		assertEquals(ErrorDefine.OK, resOne.getErrorCode());
		assertEquals(10, resOne.getLstRoles().size());	
    }
	
	@Test
    public void getListRoleDetailTest() {	
		AdminGetListRoleDetailResponse resNull = (AdminGetListRoleDetailResponse)service.getListRoleDetail(null,0,roleDetailRequest);
		assertEquals(ErrorDefine.OK, resNull.getErrorCode());
		assertEquals(null, resNull.getPermissions());
				
		AdminGetListRoleDetailResponse resEmpty = (AdminGetListRoleDetailResponse)service.getListRoleDetail("",0,roleDetailRequest);
		assertEquals(ErrorDefine.OK, resEmpty.getErrorCode());
		assertEquals(0, resEmpty.getPermissions().size());
		
		
		AdminGetListRoleDetailResponse resOne = (AdminGetListRoleDetailResponse)service.getListRoleDetail("vi",0,roleDetailRequest);
		assertEquals(ErrorDefine.OK, resOne.getErrorCode());
		assertEquals(10, resOne.getPermissions().size());	
    }

}
