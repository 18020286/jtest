package com.viettel.mve.authservice.service.impl.common;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.core.external.intercomm.CustomerClient;
import com.viettel.mve.authservice.core.external.intercomm.MediaClient;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.GlobalConstant.AccountFileType;
import com.viettel.mve.client.request.auth.RegisterAccountRequest;
import com.viettel.mve.client.request.auth.UpdateRequiredInforRequest;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.client.request.customer.ModifyEnterpriseRequest;
import com.viettel.mve.client.response.media.UploadMediaResponse;
import com.viettel.mve.common.caching.RedisCaching;
import com.viettel.mve.common.intercomm.request.CheckRequireLoginRequest;
import com.viettel.mve.common.intercomm.response.ModifyEnterpriseResp;
import com.viettel.mve.common.logging.MVELoggingUtils;

@SpringBootTest
public class AccountServiceTest{
	
	public AccountServiceTest() {
		MockitoAnnotations.initMocks(this);
		MVELoggingUtils.setLogger(logger);
		
	}
	@Mock
	private BCryptPasswordEncoder encoder;
	@Mock
	protected UserRepository userRepository;
	@Mock
	protected CustomerClient customerClient;
	
	@Mock
	protected MediaClient mediaClient;
	
	@Mock
	private Logger logger;
	@Mock
	private RedisCaching redisCaching;
	
	@InjectMocks
	private AccountService service = new AccountServiceImpl();
	
	
	
	
	
	@Before
    public void setupMock() {
    	//		
		MVEUser user = new MVEUser();
		user.setId(new BigInteger("1"));
		user.setBusinessId(new BigInteger("1"));
		user.setUserName("HUNGVQ5");
		when(userRepository.findByUserId(new BigInteger("1"))).thenReturn(user);
		//    
		
		//List<MVEUser> users = userRepository.findActiveAccountByEmail(email);
		List<MVEUser> users = new ArrayList<MVEUser>();
		users.add(user);
		users.add( new MVEUser());
		when(userRepository.findActiveAccountByEmail("hungvq5@gmail.com")).thenReturn(users);
		
		ModifyEnterpriseRequest request = new ModifyEnterpriseRequest();
		request.setModifyUser(-1);
		ModifyEnterpriseResp rs  = new ModifyEnterpriseResp();
		rs.setErrorCode(ErrorDefine.OK);
		rs.setEnterpriseId(1);		
		ModifyEnterpriseRequestMatcher matcher = new ModifyEnterpriseRequestMatcher(request);	
		when(customerClient.createEnterprise(argThat(matcher))).thenReturn(rs);
		
		MVEUserMatcher matcher2 = new MVEUserMatcher(user);	
		when(userRepository.save(argThat(matcher2))).thenReturn(user);
		
		MultipartFile regisForm = Mockito.mock(MultipartFile.class);
		MultipartFile bussinessLicense = Mockito.mock(MultipartFile.class);
		MultipartFile personalCard = Mockito.mock(MultipartFile.class);
		
		Map<String, MultipartFile> form = new HashMap<String, MultipartFile>();
		if (regisForm != null) {
			form.put(AccountFileType.REGIS_FORM_FILE, regisForm);
		}
		if (bussinessLicense != null) {
			form.put(AccountFileType.BUSINESS_LICENSE_FILE, bussinessLicense);
		}
		if (personalCard != null) {
			form.put(AccountFileType.PERSONAL_CARD_FILE, personalCard);
		}
		
		UploadMediaResponse rs1 = new UploadMediaResponse();
		rs1.setErrorCode(ErrorDefine.OK);		
		MultipartFileMatcher fileMatcher = new MultipartFileMatcher(form);
		when(mediaClient.uploadRegisAccountDocument(argThat(fileMatcher),eq(1),eq(1),eq(1))).thenReturn(rs1);
    }
	
	
	@Before
    public void setupMock2() {
    	//		
		String REDIS_PREFIX_KEY = "AUTH:";
		String REDIS_LAST_MODIFY_KEY = REDIS_PREFIX_KEY + "LAST_MODIFY:";
//		String key = REDIS_LAST_MODIFY_KEY + userId;
//		if (redisCaching.getObject(key) != null) {
		Object obj = new Object();
		when(redisCaching.getObject(REDIS_LAST_MODIFY_KEY + 1)).thenReturn(obj);
		when(redisCaching.getObject(REDIS_LAST_MODIFY_KEY + 2)).thenReturn(new Date());
	}	

	
	@Test
    public void searchListAccountTest() {
		UpdateRequiredInforRequest request = new UpdateRequiredInforRequest();
		service.updateRequiredInfor(0, request, 0);
		//request.set
		service.updateRequiredInfor(1, request, 1);
    }
	
	@Test
    public void forgetAccountTest() {
		service.forgetAccount("");
		service.forgetAccount("hungvq5@gmail.com");
    }

    /**
	@Test
    public void registerAccountTest() {
		MultipartFile regisForm = Mockito.mock(MultipartFile.class);
		MultipartFile bussinessLicense = Mockito.mock(MultipartFile.class);
		MultipartFile personalCard = Mockito.mock(MultipartFile.class);
		
		RegisterAccountRequest accountInforRequest = new RegisterAccountRequest();
		try {
			service.registerAccount(accountInforRequest, regisForm, bussinessLicense, personalCard);	
		}
		catch(Exception e) {
			MVELoggingUtils.noShowLog(e);
		}
		
		try {
			RegisterAccountInfor acc = new RegisterAccountInfor();
			acc.setUsername("hungvq5");
			accountInforRequest.setAccountInfor(acc);
			service.registerAccount(accountInforRequest, regisForm, bussinessLicense, personalCard);	
		}
		catch(Exception e) {
			MVELoggingUtils.noShowLog(e);
		}
		//
		
    }**/
	
	@Test
    public void setLastModifyDateTest() {
		service.setLastModifyDate(0, new Date());
		service.setLastModifyDate(1, new Date());
    }
	
	@Test
    public void checkRequireLoginTest() {
		CheckRequireLoginRequest request = new CheckRequireLoginRequest();
		service.checkRequireLogin(request);
		request.setUserId(2l);
		request.setTokenCreateTime(new Date());
		service.checkRequireLogin(request);
    }
	

}
