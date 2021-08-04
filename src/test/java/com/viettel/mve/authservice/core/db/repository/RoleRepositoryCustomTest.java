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

import com.viettel.mve.authservice.core.db.object.WrapObjectString;
import com.viettel.mve.authservice.core.db.repository.impl.UserRoleRepositoryCustomImpl;

@SpringBootTest
public class RoleRepositoryCustomTest {
	
	@InjectMocks
	private UserRoleRepositoryCustom userRoleRepository = new UserRoleRepositoryCustomImpl();
	
	@Mock
	protected EntityManager em;
	
	public RoleRepositoryCustomTest() {
		MockitoAnnotations.initMocks(this);
		//MVELoggingUtils.setLogger(logger);
	}
	
	@Before
    public void searchListProblemMock() {
		String sql ="SELECT DISTINCT P.PERMISSION_CODE FROM user_roles UR LEFT JOIN role_permissions RP ON RP.ROLE_ID = UR.ROLE_ID LEFT JOIN permission P ON P.ID = RP.PERMISSION_ID where UR.USER_ID = :userId AND UR.IS_DEL = 0  AND RP.IS_DEL = 0 AND P.PERMISSION_CODE is not null ";
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNativeQuery(sql, "PermissionStringMapping")).thenReturn(query);
		List<WrapObjectString> expected = new ArrayList<WrapObjectString>();
		WrapObjectString problem = new WrapObjectString();
		expected.add(problem);		
        Mockito.when(query.getResultList()).thenReturn(expected);
	}
	
	@Before
    public void findRoleCodesByUserMock() {
		String sql ="SELECT GROUP_CONCAT(R.ROLE_NAME SEPARATOR '; ') as ROLES FROM user_roles UR LEFT JOIN role R on UR.role_id = R.id where UR.USER_ID = :userId AND UR.IS_DEL = 0  ";
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNativeQuery(sql, "RoleStringMapping")).thenReturn(query);
		WrapObjectString item = new WrapObjectString();
        Mockito.when(query.getSingleResult()).thenReturn(item);
	}
	
	
	
	@Test
    public void searchListProblemTest() {		
		userRoleRepository.findPermissionCodesByUser(new BigInteger("1"));
	}
	
	@Test
    public void findRoleCodesByUserTest() {		
		userRoleRepository.findRoleCodesByUser(new BigInteger("1"));
	}

}
