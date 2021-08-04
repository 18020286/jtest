package com.viettel.mve.authservice.service.impl.common;


import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.ResetPassTransaction;
import com.viettel.mve.authservice.core.db.repository.ResetPassTransactionRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.service.common.ResetPassService;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.ResetPassRequest;
import com.viettel.mve.common.caching.RedisCaching;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.logging.MVELoggingUtils;

@SpringBootTest
public class ResetPassServiceTest{
	
	public ResetPassServiceTest() {
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
	
	@InjectMocks
	private ResetPassService service = new ResetPassServiceImpl();
	
	
	
	
	
	@Before
	public void setupMock() {
		//
		MVEUser user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ5");
		user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		when(userRepository.findByUserNameAndEmail("hungvq5", "hungvq5@gmail")).thenReturn(user);
		
		user.setStatus(StatusDefine.AccountStatus.REJECT.getValue());
		service.getResetPassCode("hungvq1","hungvq1@gmail");
		user.setStatus(StatusDefine.AccountStatus.STATUS_LOCKED.getValue());
		service.getResetPassCode("hungvq2","hungvq21@gmail");
		
		user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		when(userRepository.findByUserNameAndEmail("hungvq5", "hungvq5@gmail")).thenReturn(user);
		
	}
	
	
	@Before
    public void setupMock2() {
    	//
		
		MVEUser user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ5");
		user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		
		ResetPassTransaction rpTransaction =  new ResetPassTransaction();
		rpTransaction.setExpireDate(DateUtility.addDate(new Date(), 1));
		rpTransaction.setStatus(StatusDefine.OTPCodeStatus.STATUS_ACTIVE.getValue());
		rpTransaction.setUser(user);
		when(resetPassRepository.findByResetPassCode("code")).thenReturn(rpTransaction);
		
		when(encoder.encode("123")).thenReturn("1234");
		
		when(configValue.getCodeTimeoutInMinute()).thenReturn(3);
		
		
	}	

	
	@Test
    public void getResetPassCodeTest() {
		service.getResetPassCode("","");
		//request.set
		service.getResetPassCode("hungvq","hungvq@gmail");
		service.getResetPassCode("hungvq1","hungvq1@gmail"); 
		service.getResetPassCode("hungvq2","hungvq21@gmail");
		service.getResetPassCode("hungvq5","hungvq5@gmail");
		service.getResetPassCode("hungvq5","hungvq5@gmail");
    }
	
	@Test
    public void resetPasswordTest() {
		ResetPassRequest request = new ResetPassRequest();
		service.resetPassword(request);
		request.setCode("code");
		request.setNewPassword("123");
		service.resetPassword(request);
    }
	
	

}
