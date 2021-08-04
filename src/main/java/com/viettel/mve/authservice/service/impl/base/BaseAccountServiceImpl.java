package com.viettel.mve.authservice.service.impl.base;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import com.viettel.mve.client.request.auth.ValidateRegisterRequest;
import com.viettel.mve.client.response.customer.object.EnterpriseBasicInfor;
import com.viettel.mve.common.intercomm.request.*;
import com.viettel.mve.common.intercomm.response.SearchEnterpriseResp;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.Attach;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.AttachRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepository;
import com.viettel.mve.authservice.core.db.repository.UserRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepository;
import com.viettel.mve.authservice.core.db.repository.UserRoleRepositoryCustom;
import com.viettel.mve.authservice.core.external.intercomm.CustomerClient;
import com.viettel.mve.authservice.core.external.intercomm.KPILogClient;
import com.viettel.mve.authservice.core.external.intercomm.MediaClient;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.GlobalConstant;
import com.viettel.mve.client.constant.GlobalConstant.AccountFileType;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.request.problem.object.Attachment;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.auth.UpdateAvartarResponse;
import com.viettel.mve.client.response.customer.object.Enterprise;
import com.viettel.mve.client.response.media.UploadMediaResponse;
import com.viettel.mve.client.response.media.UploadMediaResponse.MediaItem;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.constant.GlobalKey;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.intercomm.response.GetEnterpriseByIDResp;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

public abstract class BaseAccountServiceImpl {
	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected MediaClient mediaClient;

	@Autowired
	protected UserRoleRepository userRoleRepository;

	@Autowired
	protected UserRoleRepositoryCustom userRoleRepositoryCustom;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Autowired
	protected CustomerClient customerClient;

	@Autowired
	protected AttachRepository attachRepository;

	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private KPILogClient kpiLogClient;
	
	@Autowired
	private ConfigValue configValue;

	private int getAttachTypeFromFileTag(String fileTag) {
		if (GlobalKey.RegisTagFile.REGIS_FORM.getTag().equals(fileTag)) {
			return GlobalConstant.AttachType.REGIS_FORM;
		} else if (GlobalKey.RegisTagFile.BUSSINESS_LICENSE.getTag().equals(fileTag)) {
			return GlobalConstant.AttachType.BUSSINESS_LICENSE;
		} else if (GlobalKey.RegisTagFile.PERSONAL_CARD.getTag().equals(fileTag)) {
			return GlobalConstant.AttachType.PERSONAL_CARD;
		} else {
			return GlobalConstant.AttachType.OTHER;
		}
	}

	private List<EnterpriseBasicInfor> getListActiveEnterpriseByIds(List<BigInteger> enterpriseIds) {
		SearchEnterpriseByIDSReq request = new SearchEnterpriseByIDSReq();
		request.setEnterpriseIds(enterpriseIds.stream().map(item->item.longValue()).collect(Collectors.toList()));
		SearchEnterpriseResp response = customerClient.getListActiveEnterpriseByIds(request);
		if (response.getErrorCode() == ErrorDefine.OK) {
			return response.getLstEnterprise();
		}else{
			throw new RuntimeException(response.getMessage());
		}
	}

	protected void deleteUserRoles(long updateUser, BigInteger userId) {
		List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
		if (userRoles != null && !userRoles.isEmpty()) {
			for (UserRole userRole : userRoles) {
				userRole.setUpdateDate(new Date());
				userRole.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
				userRole.setIsDelete(1);
			}
			userRoleRepository.saveAll(userRoles);
		}
	}

	protected BaseResponse deleteEnterprise(long uploadUser, long enterpriseId) {
		if (enterpriseId > 0) {
			DeleteEnterpriseByIDReq request = new DeleteEnterpriseByIDReq();
			request.setEnterpriseId(enterpriseId);
			request.setUpdateUser(uploadUser);
			return customerClient.deleteEnterpriseById(request);
		} else {
			return ResponseDefine.responseOK();
		}
	}

	protected void saveUserRole(long roleId, MVEUser rsUser) {
		UserRole userRole = new UserRole();
		userRole.setUserId(rsUser.getId());
		userRole.setRoleId(MVEUtils.convertLongToBigInteger(roleId));
		userRole.setCreateDate(new Date());
		userRoleRepository.save(userRole);
	}

	protected String getRoleCodesByUser(BigInteger userId) {
		return userRoleRepositoryCustom.findRoleCodesByUser(userId);
	}

	protected BaseResponse validateUsername(String username) {
		if (userRepository.findByUserName(username.toUpperCase()) != null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.account.exist"));
		}
		return null;
	}

	protected BaseResponse uploadAvatar(MultipartFile file, long enterpriseId, long avartarUser) {
		if (file == null) {
			return null;
		}
		Map<String, MultipartFile> form = new HashMap<String, MultipartFile>();
		form.put("avartar", file);
		UploadMediaResponse rs = mediaClient.uploadAccountAvartar(form, enterpriseId, avartarUser);
		if (rs.getErrorCode() == ErrorDefine.OK) {
			return rs;
		} else {
			return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
		}
	}

	protected BaseResponse updateAvartar(String oldAvartar, MultipartFile newAvartar, long enterpriseId,
			long avartarUser) {
		if (newAvartar == null) {
			return null;
		}
		if (!StringUtility.isNullOrEmpty(oldAvartar)) {
			DeleteFileByPathRequest request = new DeleteFileByPathRequest();
			request.setPath(oldAvartar);
			BaseResponse rsDelete = mediaClient.deletePublicFileByPath(request);
			if (rsDelete.getErrorCode() != ErrorDefine.OK) {
				return ResponseDefine.responseBaseError(rsDelete.getErrorCode(), rsDelete.getMessage());
			}
		}
		return uploadAvatar(newAvartar, enterpriseId, avartarUser);
	}

	protected BaseResponse checkIsValidEnterpriseId(BigInteger sourceEId, long inputEnterpriseId) {
		BigInteger eId = MVEUtils.convertLongToBigInteger(inputEnterpriseId);
		if (sourceEId == null || eId.compareTo(sourceEId) != 0) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
		}
		return null;
	}

	protected BaseResponse excuteUpdateAvartar(MVEUser user, MultipartFile avartar, long enterpriseId,
			long updateUser) {
		BaseResponse rsUploadAvartar = updateAvartar(user.getAvartar(), avartar, enterpriseId,
				MVEUtils.convertIDValueToLong(user.getId()));
		if (rsUploadAvartar.getErrorCode() != ErrorDefine.OK) {
			return rsUploadAvartar;
		} else {
			List<MediaItem> rsItems = ((UploadMediaResponse) rsUploadAvartar).getLstMedia();
			String newAvartarUrl = rsItems.get(0).getMediaPath();
			user.setAvartar(newAvartarUrl);
			user.setUpdateDate(new Date());
			user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
			userRepository.save(user);
			UpdateAvartarResponse response = new UpdateAvartarResponse();
			response.setErrorCode(ErrorDefine.OK);
			response.setNewAvartar(newAvartarUrl);
			return response;
		}
	}

	protected void excuteUpdateUserRoleTransaction(long updateUser, BigInteger userId, List<UserRole> userRoles)
			throws TransactionException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					deleteUserRoles(updateUser, userId);
					if (!userRoles.isEmpty()) {
						userRoleRepository.saveAll(userRoles);
					}
				} catch (Exception e) {
					MVELoggingUtils.logMVEException(e);
					throw new TransactionException("Update user role error " + e.getMessage());
				}
			}
		});
	}

	protected List<Attachment> getListAttachByUserId(String authToken, long userId) {
		List<Attachment> rsAttach = null;
		List<Attach> attachs = attachRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
		if (attachs != null && !attachs.isEmpty()) {
			rsAttach = new ArrayList<Attachment>();
			long mediaId;
			for (Attach att : attachs) {
				mediaId = MVEUtils.convertIDValueToLong(att.getMediaId());
				Attachment item = new Attachment();
				item.setFileName(att.getAttactFileName());
				item.setMediaId(mediaId);
				item.setFileType(att.getAttactFileType());
				item.setToken(MVEUtils.encryptMediaToken(authToken, mediaId));
				item.setCreateDate(DateUtility.format(att.getCreateDate(), DateUtility.DATE_FORMAT_STR));
				item.setType(att.getAttachType());
				rsAttach.add(item);
			}
		}
		return rsAttach;
	}

	protected Enterprise getEnterpriseById(long enterpriseId) {
		String currentLang = MVEUtils.getCurrentLocaleCode();
		GetEnterpriseByIDReq request = new GetEnterpriseByIDReq();
		request.setCurrentLang(currentLang);
		request.setEnterpriseId(enterpriseId);
		GetEnterpriseByIDResp response = customerClient.getEnterpriseById(request);
		if (response.getErrorCode() != ErrorDefine.OK) {
			throw new RuntimeException("getEnterpriseInfor error");
		} else {
			if (response.getEnterprise() == null) {
				throw new RuntimeException("getEnterpriseInfor error");
			}
			return response.getEnterprise();
		}
	}

	protected BaseResponse uploadRegisAccountFile(long uploadUser, long docUser, long enterpriseId,
			MultipartFile regisForm, MultipartFile bussinessLicense, MultipartFile personalCard) {
		if (regisForm == null && bussinessLicense == null && personalCard == null) {
			return null;
		}
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
		UploadMediaResponse rs = mediaClient.uploadRegisAccountDocument(form, enterpriseId, uploadUser, docUser);
		if (rs.getErrorCode() == ErrorDefine.OK) {
			BigInteger documentUser = MVEUtils.convertLongToBigInteger(docUser);
			List<Attach> attacts = new ArrayList<Attach>();
			int attachType = 0;
			for (MediaItem item : rs.getLstMedia()) {
				attachType = getAttachTypeFromFileTag(item.getTagFile());
				// Delete old attach
				Attach oldAttach = attachRepository.findByUserIdAndType(documentUser, attachType);
				if (oldAttach != null) {
					oldAttach.setIsDelete(1);
					oldAttach.setUpdateDate(new Date());
					oldAttach.setUpdateUser(MVEUtils.convertLongToBigInteger(uploadUser));
					attacts.add(oldAttach);
				}
				// End delete old attach
				Attach at = new Attach();
				at.setCreateDate(new Date());
				at.setUserId(MVEUtils.convertLongToBigInteger(docUser));
				at.setCreateUser(MVEUtils.convertLongToBigInteger(uploadUser));
				at.setAttactFileName(item.getMediaFileName());
				at.setMediaId(MVEUtils.convertLongToBigInteger(item.getMediaId()));
				at.setAttachType(getAttachTypeFromFileTag(item.getTagFile()));
				attacts.add(at);
			}
			attachRepository.saveAll(attacts);
			return ResponseDefine.responseOK();
		} else {
			return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
		}
	}

	protected BaseResponse validateStatusAccount(MVEUser user) {
		if (user.getStatus() == StatusDefine.AccountStatus.WAITING_APPROVE.getValue()
				|| user.getStatus() == StatusDefine.AccountStatus.REJECT.getValue()) {
			String message = MessagesUtils.getMessage("message.account.not.approved1");
			message = String.format(message, user.getUserName());
			return ResponseDefine.responseError(ErrorDefine.INVALID, message);
		}
		return null;
	}

	protected void setLastModifyDate(long userId, Date updateTime) {
		accountService.setLastModifyDate(userId, updateTime);
	}
	
	protected void sendKPILog(AddKPILogRequest request) {
		request.setServiceCode(configValue.getServiceName().toUpperCase());
		request.setIpPortCurrentNode(MVEUtils.getCurrentIP() + ":" + configValue.getServerPort());
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				kpiLogClient.addKPILog(request);
			}
		});
	}
	
	protected BaseResponse uploadRegisAccountFileV2(long uploadUser, long docUser, long enterpriseId,
			MultipartFile[] regisForms, MultipartFile[] bussinessLicenses, 
			MultipartFile[] personalCards, MultipartFile[] other) {
		if (regisForms == null && bussinessLicenses == null
				&& personalCards == null && other == null) {
			return null;
		}
		Map<String, MultipartFile[]> form = new HashMap<String, MultipartFile[]>();
		if (regisForms != null && regisForms.length > 0) {
			form.put(AccountFileType.REGIS_FORM_FILE, regisForms);
		}
		if (bussinessLicenses != null && bussinessLicenses.length > 0) {
			form.put(AccountFileType.BUSINESS_LICENSE_FILE, bussinessLicenses);
		}
		if (personalCards != null && personalCards.length > 0) {
			form.put(AccountFileType.PERSONAL_CARD_FILE, personalCards);
		}
		if (other != null && other.length > 0) {
			form.put(AccountFileType.OTHER_FILE, other);
		}
		UploadMediaResponse rs = mediaClient.uploadRegisAccountDocumentV2(form, enterpriseId, uploadUser, docUser);
		if (rs.getErrorCode() == ErrorDefine.OK) {
			List<Attach> attacts = new ArrayList<Attach>();
			for (MediaItem item : rs.getLstMedia()) {
				Attach at = new Attach();
				at.setCreateDate(new Date());
				at.setUserId(MVEUtils.convertLongToBigInteger(docUser));
				at.setCreateUser(MVEUtils.convertLongToBigInteger(uploadUser));
				at.setAttactFileName(item.getMediaFileName());
				at.setMediaId(MVEUtils.convertLongToBigInteger(item.getMediaId()));
				at.setAttactFileType(item.getTagFile());
				attacts.add(at);
			}
			attachRepository.saveAll(attacts);
			return ResponseDefine.responseOK();
		} else {
			return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
		}
	}

	protected BaseResponse validateEmailInfo(ValidateRegisterRequest request, BigInteger excludeEnterpriseId){
		if(StringUtility.isNullOrEmpty(request.getEmail())){
			return ResponseDefine.responseOK();
		}
		List<MVEUser> userList = userRepository.findEnterpriseAccountByEmail(request.getEmail());
		if(CollectionUtils.isEmpty(userList)){
			return ResponseDefine.responseOK();
		}
		List<BigInteger> enterpriseIdList = userList.stream().map(item->item.getBusinessId()).collect(Collectors.toList());
		if(excludeEnterpriseId != null){
			Iterator<BigInteger> idsIterator = enterpriseIdList.iterator();
			while (idsIterator.hasNext()){
				BigInteger id = idsIterator.next();
				if(id.equals(excludeEnterpriseId)){
					idsIterator.remove();
				}
			}
		}
		List<EnterpriseBasicInfor> enterpriseInfoList = getListActiveEnterpriseByIds(enterpriseIdList);
		if(CollectionUtils.isEmpty(enterpriseInfoList)){
			return ResponseDefine.responseOK();
		}
		for(EnterpriseBasicInfor enterpriseInfo : enterpriseInfoList){
			if((!StringUtility.isNullOrEmpty(request.getTaxCode()) && request.getTaxCode().equalsIgnoreCase(enterpriseInfo.getTaxNo()))
					|| (!StringUtility.isNullOrEmpty(request.getBusCode()) && request.getBusCode().equalsIgnoreCase(enterpriseInfo.getBussinessCode()))){
				return ResponseDefine.responseInvalidNoCodeError(MessagesUtils.getMessage("regis.error.dupplicate"));
			}
		}
		return ResponseDefine.responseOK();
	}

}
