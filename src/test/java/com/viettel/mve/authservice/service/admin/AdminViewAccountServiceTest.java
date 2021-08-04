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

import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.repository.PermissionRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepositoryCustom;
import com.viettel.mve.authservice.service.impl.admin.AdminViewAccountServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.AdminListAccountResponse;
import com.viettel.mve.client.response.auth.AdminViewAccountResponse;
import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.common.number.NumberUtils;
import com.viettel.mve.common.utils.MVEUtils;

@SpringBootTest
public class AdminViewAccountServiceTest{
	
	public AdminViewAccountServiceTest() {
		MockitoAnnotations.initMocks(this);
		
		
	}
	@Mock
	protected UserRepository userRepository;

	@Mock
	private UserRoleRepositoryCustom userRoleRepositoryCustom;
	
	@Mock
	protected UserRepositoryCustom userRepositoryCustom;
	
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private PermissionRepositoryCustom permissionRepository;
	
	
	@InjectMocks
	private AdminViewAccountService service = new AdminViewAccountServiceImpl();
	
	private AdminGetListAccountRequest request = new AdminGetListAccountRequest();
	private AdminViewAccountRequest request2 = new AdminViewAccountRequest();
	
	
	
	@Before
    public void getAccountInforMock() {
		MVEUser user = null;
		request2.setUserId(-1);
    	when(userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request2.getUserId()))).thenReturn(user);
    	//  
    	request2.setUserId(0);
    	user = new MVEUser();
    	when(userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request2.getUserId()))).thenReturn(user);
    	
    	request2.setUserId(10);
    	user = new MVEUser();
    	user.setBusinessId(new BigInteger("10"));
    	when(userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request2.getUserId()))).thenReturn(user);
    	
    	when(userRoleRepositoryCustom.findRoleCodesByUser(new BigInteger("10"))).thenReturn("role");
    	
    }
	
	@Before
    public void searchListAccountMock() {
		List<AdminListAccountItem> lstAdminListAccountItems = null;
		//
		PagingObject<AdminListAccountItem> rs = new PagingObject<AdminListAccountItem>();
		rs.setListData(lstAdminListAccountItems);
		rs.setTotalRow(NumberUtils.getSizeList(lstAdminListAccountItems));
    	when(userRepositoryCustom.searchListAccountForAdmin(-1,0, request)).thenReturn(rs);
    	//    
    	lstAdminListAccountItems = new ArrayList<AdminListAccountItem>();
    	rs = new PagingObject<AdminListAccountItem>();
		rs.setListData(lstAdminListAccountItems);
		rs.setTotalRow(NumberUtils.getSizeList(lstAdminListAccountItems));
		when(userRepositoryCustom.searchListAccountForAdmin(0,0, request)).thenReturn(rs);
    	
    	
    	lstAdminListAccountItems = new ArrayList<AdminListAccountItem>();
    	for(int i =0;i<10;i++) {
    		AdminListAccountItem item = new AdminListAccountItem();
    		item.setAccountId(i);
    		item.setUsername("username"+i);
    		lstAdminListAccountItems.add(item);
    	}
    	rs = new PagingObject<AdminListAccountItem>();
		rs.setListData(lstAdminListAccountItems);
		rs.setTotalRow(NumberUtils.getSizeList(lstAdminListAccountItems));
    	when(userRepositoryCustom.searchListAccountForAdmin(10,0, request)).thenReturn(rs);
    }
	

	
	@Test
    public void searchListAccountTest() {
		AdminListAccountResponse resNull = (AdminListAccountResponse)service.searchListAccount(null,-1, 0, null, request);
		assertEquals(ErrorDefine.OK, resNull.getErrorCode());
		assertEquals(0, resNull.getListData().size());
				
		AdminListAccountResponse resEmpty = (AdminListAccountResponse)service.searchListAccount(null, 0, 0, null, request);
		assertEquals(ErrorDefine.OK, resEmpty.getErrorCode());
		assertEquals(0, resEmpty.getListData().size());
		
		AdminListAccountResponse resOne = (AdminListAccountResponse)service.searchListAccount(null, 10, 0, null, request);
		assertEquals(ErrorDefine.OK, resOne.getErrorCode());
		assertEquals(10, resOne.getListData().size());	
    }
	
	@Test
    public void getAccountInforTest() {
		request2.setUserId(-1);
		BaseResponse resNull = (BaseResponse)service.getAccountInfor(null,-1,  null, request2);
		assertEquals(ErrorDefine.INVALID, resNull.getErrorCode());
				
		request2.setUserId(0);
		BaseResponse resEmpty = (BaseResponse)service.getAccountInfor(null, 0, null, request2);
		assertEquals(ErrorDefine.INVALID, resEmpty.getErrorCode());
		
		request2.setUserId(10);
		AdminViewAccountResponse resOne = (AdminViewAccountResponse)service.getAccountInfor(null, 10, null, request2);
		assertEquals(ErrorDefine.OK, resOne.getErrorCode());
		assertEquals(-1, resOne.getAccountInfor().getUserId());
    }
	

}
