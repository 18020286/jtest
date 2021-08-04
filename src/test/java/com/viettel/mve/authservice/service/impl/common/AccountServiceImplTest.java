package com.viettel.mve.authservice.service.impl.common;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.client.request.auth.object.ModifyEnterpriseInfor;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.common.logging.MVELoggingUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @Mock
    Logger logger;

    @Mock
    ConfigValue configValue;

    @InjectMocks
    AccountServiceImpl service = new AccountServiceImpl();

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
        MVELoggingUtils.setLogger(logger);
    }

    @Test
    public void sendNotifyImportAccountEmailTest_T6() {
        configValue = Mockito.mock(ConfigValue.class);
        Mockito.when(configValue.getRegisNotiEmails()).thenReturn("");
        ModifyEnterpriseInfor enterpriseInfor = new ModifyEnterpriseInfor();
        RegisterAccountInfor accountInfor = new RegisterAccountInfor();
        service.sendNotifyImportAccountEmail(enterpriseInfor,accountInfor,"");
    }
}