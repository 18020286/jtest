package com.viettel.mve.authservice.core.db.repository;

import java.math.BigInteger;
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

import com.viettel.mve.authservice.core.db.repository.impl.PermissionRepositoryCustomImpl;
import com.viettel.mve.client.response.auth.object.PermissionItem;

@SpringBootTest
public class PermissionRepositoryCustomTest {
	
	@InjectMocks
	private PermissionRepositoryCustom permissionRepository = new PermissionRepositoryCustomImpl();
	
	@Mock
	protected EntityManager em;
	
	public PermissionRepositoryCustomTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void getListPermissionByTypeMock() {
		String sql1 ="SELECT P.ID, PN.PERMISSION_NAME from permission P LEFT JOIN permission_name PN on P.id = PN.permission_id WHERE P.IS_DEL = 0 AND P.IS_VISIBILITY = 1 AND P.PERMISSION_TYPE = :permissionType AND PN.IS_DEL = 0 AND PN.LANG_KEY = :langKey ORDER BY PERMISSION_NAME ASC ";
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNativeQuery(sql1, "PermissionItemMapping")).thenReturn(query);
		String sql2 ="SELECT P.ID, PN.PERMISSION_NAME from permission P LEFT JOIN permission_name PN on P.id = PN.permission_id WHERE P.IS_DEL = 0 AND P.IS_VISIBILITY = 1 AND PN.IS_DEL = 0 AND PN.LANG_KEY = :langKey AND P.ID IN (:idsParams) ORDER BY PERMISSION_NAME ASC ";
		Mockito.when(em.createNativeQuery(sql2, "PermissionItemMapping")).thenReturn(query);
		List<PermissionItem> expected = new ArrayList<PermissionItem>();
		PermissionItem item = new PermissionItem();
		expected.add(item);		
        Mockito.when(query.getResultList()).thenReturn(expected);       
	}
	

	
	
	
	
	@Test
    public void getListPermissionByTypeTest() {
		permissionRepository.getListPermissionByType("",1);
		permissionRepository.getListPermissionByType("vi",1);
		
	}
	
	@Test
    public void getPermissionByIdsTest() {
		List<BigInteger> ids = new ArrayList<BigInteger>();
		ids.add(new BigInteger("1"));
		ids.add(new BigInteger("2"));
		permissionRepository.getPermissionByIds("",ids);
		permissionRepository.getPermissionByIds("vi",ids);
	}
}
