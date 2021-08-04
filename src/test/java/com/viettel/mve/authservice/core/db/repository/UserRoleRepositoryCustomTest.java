package com.viettel.mve.authservice.core.db.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.viettel.mve.authservice.core.db.repository.impl.RoleRepositoryCustomImpl;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;

@SpringBootTest
public class UserRoleRepositoryCustomTest {
	
	@InjectMocks
	private RoleRepositoryCustom roleRepository = new RoleRepositoryCustomImpl();
	
	@Mock
	protected EntityManager em;
	
	public UserRoleRepositoryCustomTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void searchListProblemMock() {
		String sql ="SELECT R.ID, R.ROLE_NAME, U.USER_NAME, RPN.PERMISSIONS_NAME, R.DESCRIPTION, R.IS_SYSTEM FROM role R LEFT JOIN  (SELECT RP.ROLE_ID, GROUP_CONCAT(RPN.PERMISSION_NAME SEPARATOR ';') PERMISSIONS_NAME  FROM role_permissions RP  LEFT JOIN permission_name RPN ON RPN.PERMISSION_ID = RP.PERMISSION_ID  WHERE RPN.LANG_KEY = :langKey  AND RP.IS_DEL = 0  AND RPN.IS_DEL = 0  GROUP BY RP.ROLE_ID) RPN ON RPN.ROLE_ID = R.ID LEFT JOIN user U ON U.ID = R.CREATED_USER WHERE R.IS_DEL = 0 AND (R.IS_SYSTEM = 0 || (R.IS_SYSTEM = 1 AND R.IS_VISIBILITY = 1)) AND (U.IS_DEL is null OR U.IS_DEL = 0) AND R.ROLE_NAME_SEARCH like :searchRoleName AND USER_NAME like :searchCreateAccount ORDER BY ID DESC ";
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNativeQuery(sql, "SysAdminRoleItemMapping")).thenReturn(query);
		List<SysAdminRoleItem> expected = new ArrayList<SysAdminRoleItem>();
		SysAdminRoleItem item = new SysAdminRoleItem();
		expected.add(item);		
        Mockito.when(query.getResultList()).thenReturn(expected);
        Mockito.when(query.setMaxResults(10)).thenReturn(query);
        Mockito.when(query.setFirstResult(0)).thenReturn(query);
        Mockito.when(query.setMaxResults(10).setFirstResult(0).getResultList()).thenReturn(expected);
        
	}
	
	
	
	
	@Test
    public void searchListProblemTest() {	
		SysAdminSearchRoleRequest request = new SysAdminSearchRoleRequest();
		request.setSearchRoleName("name");
		request.setSearchCreateAccount("account");
		request.setPage(1);
		request.setPageSize(10);
		roleRepository.searchListRoleForSysAdmin(request,"1");
		
	}

}
