package com.viettel.mve.authservice.controller;

import com.viettel.mve.client.request.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.AuthValidateUtils;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.authservice.service.common.ResetPassService;
import com.viettel.mve.client.constant.GlobalConstant.AccountFileType;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.common.spring.BaseController;
import com.viettel.mve.common.utils.JsonUtils;

@RestController
@RequestMapping("/public")
public class OpenAPIController extends BaseController {

	@Autowired
	private ResetPassService resetPassService;

	@Autowired
	private AccountService accountService;

	@RequestMapping(path = "/getResetPassCode", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> getResetPassCode(@RequestBody GetResetPassCodeRequest request) {
		BaseResponse response = AuthValidateUtils.validateGetResetPassCodeRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		} else {
			response = resetPassService.getResetPassCode(request.getUsername(), request.getEmail());
			return responseOkStatus(response);
		}
	}

	@RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> resetPassword(@RequestBody ResetPassRequest request) {
		BaseResponse response = AuthValidateUtils.validateResetPassRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		}
		return responseOkStatus(resetPassService.resetPassword(request));
	}

	@RequestMapping(path = "/forgetAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> forgetAccount(@RequestBody ForgetAccountRequest request) {
		BaseResponse response = AuthValidateUtils.validateForgetAccountRequest(request);
		if (response != null) {
			return responseOkStatus(response);
		}
		response = accountService.forgetAccount(request.getEmail());
		return responseOkStatus(response);
	}

	/**
	@RequestMapping(path = "/registerAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> registerAccount(
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile regisForm,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile bussinessLicense,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile personalCard,
			@RequestParam("registerInfor") String registerInfor) {
		RegisterAccountRequest accountInforRequest = JsonUtils.fromJson(registerInfor, RegisterAccountRequest.class);
		MultipartFile[] files = new MultipartFile[] { regisForm };
		BaseResponse response = AuthValidateUtils.validateRegisterAccountRequest(accountInforRequest, files);
		if (response != null) {
			return responseOkStatus(response);
		}
		response = accountService.registerAccount(accountInforRequest, regisForm, bussinessLicense, personalCard);
		return responseOkStatus(response);
	}**/
	
	@RequestMapping(path = "/v2/registerAccount", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> registerAccountV2(
			@RequestParam(name = AccountFileType.REGIS_FORM_FILE, required = false) MultipartFile[] regisForms,
			@RequestParam(name = AccountFileType.BUSINESS_LICENSE_FILE, required = false) MultipartFile[] bussinessLicenses,
			@RequestParam(name = AccountFileType.PERSONAL_CARD_FILE, required = false) MultipartFile[] personalCards,
			@RequestParam(name = AccountFileType.OTHER_FILE, required = false) MultipartFile[] others,
			@RequestParam("registerInfor") String registerInfor) {
		RegisterAccountRequest accountInforRequest = JsonUtils.fromJson(registerInfor, RegisterAccountRequest.class);
		BaseResponse response = AuthValidateUtils.validateRegisterAccountRequestV2(accountInforRequest, 
				regisForms, bussinessLicenses, personalCards, others);
		if (response != null) {
			return responseOkStatus(response);
		}
		response = accountService.registerAccountV2(accountInforRequest, regisForms,
				bussinessLicenses, personalCards, others);
		return responseOkStatus(response);
	}

	@RequestMapping(path = "/validateRegisterInfo", method = RequestMethod.POST)
	public ResponseEntity<BaseResponse> validateRegisterInfo(@RequestBody ValidateRegisterRequest request) {
		return responseOkStatus(accountService.validateRegisterInfo(request));
	}

//	@Autowired
//	UserRepository UserRepository;
//	
//	@RequestMapping(path = "/test", method = RequestMethod.GET)
//	public ResponseEntity<BaseResponse> test() throws InterruptedException {
//		Iterable<MVEUser> users = UserRepository.findAll();
//		List<String> exclude = Arrays.asList("MVE_REVIEW","MVE_CSKH","MVE_ACC","BLUBESKYEDUCATION_ADMIN", "MVE_ADMIN","HANGKHONGVN_ADMIN","HANGKHONGVN","PHUONGNTT21@VIETTEL.COM.VN");
//		Iterator<MVEUser> ite = users.iterator();
//		StringBuilder sb = new StringBuilder("");
//		List<MVEUser> lstUser = new ArrayList<MVEUser>();
//		while(ite.hasNext()){
//			MVEUser user = ite.next();
//			if(!exclude.contains(user.getUserName()) && user.getIsDelete() == 0) {
//				System.out.println(user.getUserName());
//				if(user.getBusinessId() != null) {
//					sb.append(",").append(user.getBusinessId().longValue());
//				}
//				Date updateTime = new Date();
//				user.setUserName(user.getUserName() + "_DEL_" + System.currentTimeMillis());
//				user.setIsDelete(1);
//				user.setUpdateDate(updateTime);
//				user.setUpdateUser(MVEUtils.convertLongToBigInteger(0l));
//				lstUser.add(user);
//			}
//		}
////		System.out.println(sb.toString());
//		UserRepository.saveAll(lstUser);
//		return responseOkStatus(ResponseDefine.responseOK());
//	}
	
}
