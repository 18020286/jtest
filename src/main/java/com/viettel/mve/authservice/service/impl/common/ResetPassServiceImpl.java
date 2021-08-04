package com.viettel.mve.authservice.service.impl.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.common.Utils;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.ResetPassTransaction;
import com.viettel.mve.authservice.core.db.repository.ResetPassTransactionRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.service.common.ResetPassService;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.auth.ResetPassRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.auth.GetResetPassCodeResponse;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("ResetPassService")
public class ResetPassServiceImpl extends BaseAccountServiceImpl implements ResetPassService {

	@Autowired
	private ResetPassTransactionRepository resetPassRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private ConfigValue configValue;

	private BaseResponse checkValidStatus(MVEUser user) {
		BaseResponse validateResponse = validateStatusAccount(user);
		if (validateResponse != null) {
			return validateResponse;
		}
		if (user.getStatus() == StatusDefine.AccountStatus.STATUS_LOCKED.getValue()) {
			String message = MessagesUtils.getMessage("message.account.locked");
			message = String.format(message, user.getUserName());
			return ResponseDefine.responseError(ErrorDefine.INVALID, message);
		}
		return null;
	}

	@Override
	public BaseResponse getResetPassCode(String username, String email) {
		MVEUser user = userRepository.findByUserNameAndEmail(username, email);
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.account.notexist"));
		}
		BaseResponse validateResponse = checkValidStatus(user);
		if (validateResponse != null) {
			return validateResponse;
		}
		String code = Utils.generationSceretCode(user.getUserName());
		ResetPassTransaction rpTransaction = new ResetPassTransaction();
		Date createDate = new Date();
		rpTransaction.setCreateDate(createDate);
		rpTransaction.setExpireDate(DateUtility.addMinute(createDate, configValue.getCodeTimeoutInMinute()));
		rpTransaction.setResetPassCode(code);
		rpTransaction.setUser(user);
		rpTransaction.setStatus(StatusDefine.OTPCodeStatus.STATUS_ACTIVE.getValue());
		resetPassRepository.save(rpTransaction);
		GetResetPassCodeResponse response = new GetResetPassCodeResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setCode(code);
		response.setEmail(user.getEmail());
		return response;
	}

	@Override
	public BaseResponse resetPassword(ResetPassRequest request) {
		ResetPassTransaction rpTransaction = resetPassRepository.findByResetPassCode(request.getCode());
		Date current = new Date();
		if (rpTransaction == null || current.after(rpTransaction.getExpireDate())
				|| rpTransaction.getStatus() != StatusDefine.OTPCodeStatus.STATUS_ACTIVE.getValue()) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.otpcode.invalid"));
		}
		MVEUser user = rpTransaction.getUser();
		BaseResponse validateResponse = checkValidStatus(user);
		if (validateResponse != null) {
			return validateResponse;
		}
		user.setPassword(encoder.encode(request.getNewPassword()));
		if (user.getStatus() == StatusDefine.AccountStatus.STATUS_NEW.getValue()) {
			user.setStatus(StatusDefine.AccountStatus.STATUS_ACTIVE.getValue());
		}
		userRepository.save(user);
		rpTransaction.setStatus(StatusDefine.OTPCodeStatus.STATUS_INACTIVE.getValue());
		rpTransaction.setUpdateDate(new Date());
		resetPassRepository.save(rpTransaction);
		return ResponseDefine.responseOK();
	}

}
