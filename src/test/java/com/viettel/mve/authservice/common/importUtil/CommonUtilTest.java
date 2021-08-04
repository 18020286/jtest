package com.viettel.mve.authservice.common.importUtil;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class CommonUtilTest {

    @InjectMocks
    private CommonUtil commonUtil = new CommonUtil();

    @Test
    public void isValidPhoneNumberTest_T6() {
        Assert.assertEquals(true, commonUtil.isValidPhoneNumber("0912345678"));
        Assert.assertEquals(false, commonUtil.isValidPhoneNumber("091234567a"));
    }
}