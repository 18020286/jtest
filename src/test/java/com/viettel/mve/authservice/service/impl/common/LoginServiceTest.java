package com.viettel.mve.authservice.service.impl.common;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.ResetPassTransaction;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.ResetPassTransactionRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepositoryCustom;
import com.viettel.mve.authservice.core.external.intercomm.CustomerClient;
import com.viettel.mve.authservice.service.common.LoginService;
import com.viettel.mve.authservice.service.common.ResetPassService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.LoginRequest;
import com.viettel.mve.client.request.auth.ResetPassRequest;
import com.viettel.mve.client.response.customer.object.Enterprise;
import com.viettel.mve.common.caching.RedisCaching;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.intercomm.request.GetEnterpriseByIDReq;
import com.viettel.mve.common.intercomm.request.GetServiceByEnterpriseIdRequest;
import com.viettel.mve.common.intercomm.response.GetActiveServiceByEnterpriseResponse;
import com.viettel.mve.common.intercomm.response.GetEnterpriseByIDResp;
import com.viettel.mve.common.intercomm.response.object.EnterpriseService;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.utils.MVEUtils;

@SpringBootTest
public class LoginServiceTest{
	
	public LoginServiceTest() {
		MockitoAnnotations.initMocks(this);
		MVELoggingUtils.setLogger(logger);
		
	}
	@Mock
	private ResetPassTransactionRepository resetPassRepository;
	
	@Mock
	protected UserRepository userRepository;
	
	@Mock
	private Logger logger;
	
	@Mock
	private BCryptPasswordEncoder encoder;
	
	@Mock
	private RedisCaching redisCaching;
	@Mock
	private ConfigValue configValue;
	
	@Mock
	private TaskExecutor taskExecutor;
	@Mock
	private UserRoleRepositoryCustom userRoleRepositoryCustom;
	@Mock
	private CustomerClient customerClient;
	@Mock
	private UserRoleRepository userRoleRepository;
	@Mock
	private RoleRepository roleRepository;
	
	@InjectMocks
	private LoginService service = new LoginServiceImpl();
	
	
	
	
	@Before
	public void setupMock() {
		//
		MVEUser user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ5");
		user.setPassword("123456");
		user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		when(userRepository.findByUserName("hungvq5")).thenReturn(user);
		
		user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ");
		user.setPassword("123456");
		user.setStatus(StatusDefine.AccountStatus.REJECT.getValue());
		when(userRepository.findByUserName("hungvq")).thenReturn(user);
		
		user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ1");
		user.setPassword("123456");
		user.setStatus(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		when(userRepository.findByUserName("hungvq1")).thenReturn(user);
		
		user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ2");
		user.setPassword("123456");
		user.setStatus(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		when(userRepository.findByUserName("hungvq2")).thenReturn(user);
		
		user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ3");
		user.setPassword("123456");
		user.setStatus(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		when(userRepository.findByUserName("hungvq3")).thenReturn(user);
		
		when(configValue.getServiceName()).thenReturn("Mobile");
		when(configValue.getMasterPassword()).thenReturn("Master");
		when(configValue.getMaxNumOfAccount()).thenReturn(2);
		
		when(encoder.matches("hungvq5","123456")).thenReturn(false);		
		when(encoder.matches("","123456")).thenReturn(false);
		when(encoder.matches("123456","123456")).thenReturn(true);
		
		
		List<String> lstRoles =new ArrayList<String>();
		lstRoles.add("1");
		lstRoles.add("2");
		
		when(userRoleRepositoryCustom.findPermissionCodesByUser(new BigInteger("1"))).thenReturn(lstRoles);
		
		List<UserRole> userRoles  = new ArrayList<UserRole>();
		UserRole item = new UserRole();
		item.setRoleId(new BigInteger("1"));
		item.setUserId(new BigInteger("1"));
		userRoles.add(item);
		
		when(userRoleRepository.findByUserId(new BigInteger("1"))).thenReturn(userRoles);
		
		List<BigInteger> roleIds = new ArrayList<BigInteger>();
		for (UserRole userRole : userRoles) {
			roleIds.add(userRole.getRoleId());
		}
		
		List<Role> roles = new ArrayList<Role>();
		Role role = new Role();
		role.setId(new BigInteger("1"));
		role.setRoleCode("1");		
		roles.add(role);
		
		
		
		when(roleRepository.findRolesByIds(roleIds)).thenReturn(roles);
		
		GetActiveServiceByEnterpriseResponse res = new GetActiveServiceByEnterpriseResponse();
		List<EnterpriseService> lstService = new ArrayList<EnterpriseService>();
		EnterpriseService enterpriseService = new EnterpriseService();
		enterpriseService.setId(1);
		enterpriseService.setServiceCode("1");
		enterpriseService.setBccsId("1");
		lstService.add(enterpriseService);
		res.setLstService(lstService);
		res.setErrorCode(ErrorDefine.OK);
		
		//GetActiveServiceByEnterpriseResponse resp = customerClient.getListActiveServiceByEnterprise(req);
		
		GetServiceByEnterpriseIdRequest req = new GetServiceByEnterpriseIdRequest();
		req.setEnterpriseId(1);
		
		GetServiceByEnterpriseIdRequestMatcher mathcher = new GetServiceByEnterpriseIdRequestMatcher(req);
		when(customerClient.getListActiveServiceByEnterprise(argThat(mathcher))).thenReturn(res);
		
		GetEnterpriseByIDResp getEnterpriseByIDResp = new GetEnterpriseByIDResp();
		getEnterpriseByIDResp.setErrorCode(ErrorDefine.OK);
		getEnterpriseByIDResp.setEnterprise(new Enterprise());
		
		GetEnterpriseByIDReq req1 = new GetEnterpriseByIDReq();
		req1.setEnterpriseId(1l);
		GetEnterpriseByIDReqMatcher mathcher1 = new GetEnterpriseByIDReqMatcher(req1);
		when(customerClient.getEnterpriseById(argThat(mathcher1))).thenReturn(getEnterpriseByIDResp);
		
		//GetEnterpriseByIDResp response = customerClient.getEnterpriseById(request);
		
		//return userRoleRepositoryCustom.findPermissionCodesByUser(MVEUtils.convertLongToBigInteger(userId));
		
		//MVEUser user = userRepository.findByUserName(request.getUsername());
		
//		user.setStatus(StatusDefine.AccountStatus.REJECT.getValue());
//		service.getResetPassCode("hungvq1","hungvq1@gmail");
//		user.setStatus(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
//		service.getResetPassCode("hungvq2","hungvq21@gmail");
		
		//user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		//when(userRepository.findByUserNameAndEmail("hungvq5", "hungvq5@gmail")).thenReturn(user);
		
	}
	
	
//	@Before
//    public void setupMock2() {
//    	//
//		
//		MVEUser user = new MVEUser();
//		user.setId(new BigInteger("1"));
//		user.setBusinessId(new BigInteger("1"));
//		user.setUserName("HUNGVQ5");
//		user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
//		
//		ResetPassTransaction rpTransaction =  new ResetPassTransaction();
//		rpTransaction.setExpireDate(DateUtility.addDate(new Date(), 1));
//		rpTransaction.setStatus(StatusDefine.OTPCodeStatus.STATUS_ACTIVE.getValue());
//		rpTransaction.setUser(user);
//		when(resetPassRepository.findByResetPassCode("code")).thenReturn(rpTransaction);
//		
//		when(encoder.encode("123")).thenReturn("1234");
//		
//		when(configValue.getCodeTimeoutInMinute()).thenReturn(3);
//		
//		
//	}	

	
	@Test
    public void loginTest() {
		LoginRequest request = new LoginRequest();
		request.setUsername("hungvq5");
		request.setPassword("hungvq5");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq");
		request.setPassword("123456");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq1");
		request.setPassword("123456");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq2");
		request.setPassword("123456");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq3");
		request.setPassword("123456");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq5");
		request.setPassword("123456");
		service.login(request);
		
		request = new LoginRequest();
		request.setUsername("hungvq5");
		request.setPassword("Master");
		service.login(request);
		
		//request.set
//		service.getResetPassCode("hungvq","hungvq@gmail");
//		service.getResetPassCode("hungvq1","hungvq1@gmail"); 
//		service.getResetPassCode("hungvq2","hungvq21@gmail");
//		service.getResetPassCode("hungvq5","hungvq5@gmail");
//		service.getResetPassCode("hungvq5","hungvq5@gmail");
    }
	
	

}
