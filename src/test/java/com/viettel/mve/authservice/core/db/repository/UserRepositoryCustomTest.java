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

import com.viettel.mve.authservice.core.db.repository.impl.RoleRepositoryCustomImpl;
import com.viettel.mve.authservice.core.db.repository.impl.UserRepositoryCustomImpl;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.AdminGetListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminSearchRoleRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.object.AdminListAccountItem;
import com.viettel.mve.client.response.auth.object.ListAccountItem;
import com.viettel.mve.client.response.auth.object.sysadmin.SysAdminRoleItem;

@SpringBootTest
public class UserRepositoryCustomTest {
	
	@InjectMocks
	private UserRepositoryCustom roleRepository = new UserRepositoryCustomImpl();
	
	@Mock
	protected EntityManager em;
	
	public UserRepositoryCustomTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
    public void searchListAllAccountForSysAdminMock() {
		String sql ="SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.STATUS in (:queryStatus) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) ORDER BY ID DESC ";
		String sql2 ="SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) ORDER BY ID DESC ";
		String sql3= "SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) AND (U.BUSINESS_ID IS NOT NULL AND U.BUSINESS_ID > 0 ) ORDER BY ID DESC ";
		String sql4 ="SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, CASE WHEN U.BUSINESS_ID is null OR U.BUSINESS_ID = 0 THEN 1 ELSE 2 END AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID LEFT JOIN  (SELECT USER_ID, GROUP_CONCAT(ROLE_ID SEPARATOR ';') ROLE_IDS   FROM user_roles   WHERE IS_DEL = 0   AND ROLE_ID IN (:roleIds)   GROUP BY USER_ID) AS R ON U.ID = R.USER_ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) AND R.ROLE_IDS IS NOT NULL ORDER BY ID DESC ";
		String sql5= "SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, :typeEnterprise AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID LEFT JOIN  (SELECT USER_ID, ROLE_ID AS ADMIN_ROLE_ID   FROM user_roles   WHERE IS_DEL = 0   AND ROLE_ID = :adminRole   ) AS R ON U.ID = R.USER_ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND U.BUSINESS_ID > 0 AND U.BUSINESS_ID IS NOT NULL AND R.ADMIN_ROLE_ID IS NULL AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.STATUS in (:queryStatus) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) ORDER BY ID DESC ";
		String sql6="SELECT U.ID,U.BUSINESS_ID, U.USER_NAME, U.FULL_NAME, U.CREATED_DATE, U.UPDATED_DATE, U.APPROVED_DATE,U.REJECT_REASON, U.STATUS, :typeEnterprise AS ACCOUNT_TYPE, U1.USER_NAME AS CREATED_USER_NAME FROM user U LEFT JOIN user U1 ON U.CREATED_USER = U1.ID LEFT JOIN  (SELECT USER_ID, ROLE_ID AS ADMIN_ROLE_ID   FROM user_roles   WHERE IS_DEL = 0   AND ROLE_ID = :adminRole   ) AS R ON U.ID = R.USER_ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND U.BUSINESS_ID > 0 AND U.BUSINESS_ID IS NOT NULL AND R.ADMIN_ROLE_ID IS NULL AND ( U.USER_NAME like :username OR U.EMAIL like :email OR U.PHONE like :phone ) AND U.CREATED_DATE >= :fromDate AND U.CREATED_DATE <= :toDate AND U.BUSINESS_ID IN (:idsParams) ORDER BY ID DESC ";
		String sql7="SELECT U.ID,U.USER_NAME, U.FULL_NAME, U.PERSIONAL_ID, U.CREATED_DATE, U.STATUS, U.PHONE FROM user U LEFT JOIN  (SELECT USER_ID, ROLE_ID AS ADMIN_ROLE_ID   FROM user_roles   WHERE IS_DEL = 0   AND ROLE_ID = :adminRole   ) AS R ON U.ID = R.USER_ID WHERE U.IS_SYSTEM = 0 AND U.IS_DEL = 0 AND U.BUSINESS_ID = :bussinessId AND R.ADMIN_ROLE_ID IS NULL AND U.ID != :currentUser ORDER BY ID DESC ";
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNativeQuery(sql, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql2, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql3, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql4, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql5, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql6, "ListAccountItemMapping")).thenReturn(query);
		Mockito.when(em.createNativeQuery(sql7, "AdminListAccountItemMapping")).thenReturn(query);
		//
		List<ListAccountItem> expected = new ArrayList<ListAccountItem>();
		ListAccountItem item = new ListAccountItem();
		expected.add(item);		
        Mockito.when(query.getResultList()).thenReturn(expected);
        Mockito.when(query.setMaxResults(10)).thenReturn(query);
        Mockito.when(query.setFirstResult(0)).thenReturn(query);
        Mockito.when(query.setMaxResults(10).setFirstResult(0).getResultList()).thenReturn(expected);
	}
	
	
	
	@Test
    public void searchListAllAccountForSysAdminTest() {	
		List<BigInteger> ids = new ArrayList<BigInteger>();
		SysSearchListAccountRequest request = new SysSearchListAccountRequest();
		request.setUsername("name");
		request.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		request.setFromDate("01/10/2019");
		request.setToDate("31/10/2019");
		request.setPage(1);
		request.setPageSize(10);
		//
		roleRepository.searchListAllAccountForSysAdmin(request,ids,false);
		//
		ids.add(new BigInteger("1"));
		ids.add(new BigInteger("2"));
		request.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		roleRepository.searchListAllAccountForSysAdmin(request,ids,false);
		//
		roleRepository.searchListAllAccountForSysAdmin(request,ids,true);
		
	}
	
	@Test
    public void searchListAccountForSysAdminTest() {	
		List<BigInteger> ids = new ArrayList<BigInteger>();
		List<BigInteger> rIds = new ArrayList<BigInteger>();
		SysSearchListAccountRequest request = new SysSearchListAccountRequest();
		request.setUsername("name");
		request.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		request.setFromDate("01/10/2019");
		request.setToDate("31/10/2019");
		request.setPage(1);
		request.setPageSize(10);
		//
		roleRepository.searchListAccountForSysAdmin(request,ids,rIds);
		//
		ids.add(new BigInteger("1"));
		ids.add(new BigInteger("2"));
		
		rIds.add(new BigInteger("1"));
		rIds.add(new BigInteger("2"));
		
		request.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		roleRepository.searchListAccountForSysAdmin(request,ids,rIds);
		
	}
	
//	PagingObject<ListAccountItem> searchListAllEnterpriseMember(SysSearchListAccountRequest request,
//	List<BigInteger> eIds);
//
//PagingObject<AdminListAccountItem> searchListAccountForAdmin(long enterpriseId, long currentUser,
//	AdminGetListAccountRequest request);
	
	@Test
    public void searchListAllEnterpriseMemberTest() {	
		List<BigInteger> ids = new ArrayList<BigInteger>();
		SysSearchListAccountRequest request = new SysSearchListAccountRequest();
		request.setUsername("name");
		request.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		request.setFromDate("01/10/2019");
		request.setToDate("31/10/2019");
		request.setPage(1);
		request.setPageSize(10);
		//
		roleRepository.searchListAllEnterpriseMember(request,ids);
		//
		ids.add(new BigInteger("1"));
		ids.add(new BigInteger("2"));
		
		request.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		roleRepository.searchListAllEnterpriseMember(request,ids);
		
	}
	
	@Test
    public void searchListAccountForAdminTest() {	
		AdminGetListAccountRequest request = new AdminGetListAccountRequest();
		request.setPage(1);
		request.setPageSize(10);
		roleRepository.searchListAccountForAdmin(0,0,request);
		
	}

}
