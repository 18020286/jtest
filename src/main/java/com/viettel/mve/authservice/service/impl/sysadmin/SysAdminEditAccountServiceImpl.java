package com.viettel.mve.authservice.service.impl.sysadmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.viettel.mve.client.request.auth.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.fabric.xmlrpc.base.Array;
import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.common.Utils;
import com.viettel.mve.authservice.common.importUtil.CommonUtil;
import com.viettel.mve.authservice.common.importUtil.DynamicExport;
import com.viettel.mve.authservice.common.importUtil.ImportErrorBean;
import com.viettel.mve.authservice.common.importUtil.ImportUtils;
import com.viettel.mve.authservice.common.importUtil.TemplateResouces;
import com.viettel.mve.authservice.core.db.entities.Attach;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.Role;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.AttachRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.external.intercomm.CategoriesClient;
import com.viettel.mve.authservice.service.common.AccountService;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.authservice.service.sysadmin.SysAdminEditAccountService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.constant.StatusDefine;
import com.viettel.mve.client.constant.StatusDefine.AccountStatus;
import com.viettel.mve.client.request.auth.object.ApproveAccountInfor;
import com.viettel.mve.client.request.auth.object.ModifyEnterpriseInfor;
import com.viettel.mve.client.request.auth.object.RegisterAccountInfor;
import com.viettel.mve.client.request.auth.object.UserStatus;
import com.viettel.mve.client.request.auth.sysadmin.SysAdminUpdateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysApproveAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysCreateAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysUpdateRegisUserRequest;
import com.viettel.mve.client.request.categories.GetListCategoriesRequest;
import com.viettel.mve.client.request.customer.ModifyEnterpriseRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.CreateAccountResponse;
import com.viettel.mve.client.response.auth.AdminResetPassResponse;
import com.viettel.mve.client.response.auth.ApproveAccountResponse;
import com.viettel.mve.client.response.auth.sysadmin.SysDetailApproveReponse;
import com.viettel.mve.client.response.categories.GetListCategoriesResponse;
import com.viettel.mve.client.response.categories.object.Category;
import com.viettel.mve.client.response.customer.object.Enterprise;
import com.viettel.mve.client.response.media.UploadMediaResponse;
import com.viettel.mve.client.response.media.UploadMediaResponse.MediaItem;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.intercomm.request.DeleteListMediaRequest;
import com.viettel.mve.common.intercomm.request.EnterpriseCacheRequest;
import com.viettel.mve.common.intercomm.response.ModifyEnterpriseResp;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;

@Service("SysAdminEditAccountService")
public class SysAdminEditAccountServiceImpl extends BaseAccountServiceImpl implements SysAdminEditAccountService {

    private static final Logger LOGGER = Logger.getLogger(SysAdminEditAccountServiceImpl.class);

    @Autowired
    private ConfigValue configValue;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    protected CategoriesClient categoriesClient;

    private MVEUser createUserFromRequest(SysCreateAccountRequest request, String newPassword) {
        MVEUser user = new MVEUser();
        user.setUserName(request.getUsername().toUpperCase());
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setCreateDate(new Date());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        if (request.getBirthday() != null) {
            user.setBirthday(DateUtility.convert(request.getBirthday(), DateUtility.DATE_FORMAT_STR));
        }
        user.setSex(request.getSex());
        user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
        user.setPassword(encoder.encode(newPassword));
        user.setPersonalId(request.getPersonalId());
        return user;
    }

    private ApproveAccountInfor createRegisInforFromEntity(MVEUser user) {
        ApproveAccountInfor infor = new ApproveAccountInfor();
        infor.setEmail(user.getEmail());
        infor.setFullName(user.getFullName());
        infor.setPersonalId(user.getPersonalId());
        infor.setPersonalIdArea(user.getPersonalArea());
        infor.setPersonalIdDate(user.getPersonalDate());
        infor.setPhone(user.getPhone());
        infor.setPosition(user.getPosition());
        infor.setUsername(user.getUserName());
        infor.setRejectReson(user.getRejectReason());
        infor.setStatus(user.getStatus());
        return infor;
    }

    private BaseResponse validateValidAccount(MVEUser user) {
        if (user == null || user.getIsSystem() == 1) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
        }
        return null;
    }

    private BaseResponse validateApproveAccount(MVEUser user) {
        if (user == null) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
        }
        if (user.getBusinessId() == null || MVEUtils.convertIDValueToLong(user.getBusinessId()) <= 0) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
        }
        if (user.getStatus() != StatusDefine.AccountStatus.WAITING_APPROVE.getValue()
                && user.getStatus() != StatusDefine.AccountStatus.REJECT.getValue()) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.account.active"));
        }

        return null;
    }

    private BaseResponse uploadDocumentFile(long uploadUser, long enterpriseId, MultipartFile[] files, long userId) {
        if (files == null || files.length == 0) {
            return null;
        }
        Map<String, MultipartFile[]> form = new HashMap<String, MultipartFile[]>();
        form.put("files", files);
        UploadMediaResponse rs = mediaClient.uploadAccountDocument(form, enterpriseId, uploadUser, userId);
        if (rs.getErrorCode() == ErrorDefine.OK) {
            List<Attach> attacts = new ArrayList<Attach>();
            for (MediaItem item : rs.getLstMedia()) {
                Attach at = new Attach();
                at.setCreateDate(new Date());
                at.setUserId(MVEUtils.convertLongToBigInteger(userId));
                at.setCreateUser(MVEUtils.convertLongToBigInteger(uploadUser));
                at.setAttactFileName(item.getMediaFileName());
                at.setMediaId(MVEUtils.convertLongToBigInteger(item.getMediaId()));
                attacts.add(at);
            }
            attachRepository.saveAll(attacts);
            return ResponseDefine.responseOK();
        } else {
            return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
        }
    }

    private BaseResponse getEnterpriseInforFromCache(String identify) {
        EnterpriseCacheRequest request = new EnterpriseCacheRequest();
        request.setIdentify(identify);
        ModifyEnterpriseResp rs = customerClient.getEnterpriseCache(request);
        if (rs.getErrorCode() == ErrorDefine.OK && rs.getEnterpriseId() == 0) {
            throw new RuntimeException("getEnterpriseInforFromCache null object");
        }
        return rs;
    }

    private void deleteEnterpriseFromCache(String identify) {
        EnterpriseCacheRequest request = new EnterpriseCacheRequest();
        request.setIdentify(identify);
        customerClient.deleteEnterpriseCache(request);
    }

    private ModifyEnterpriseInfor getEnterpriseInfor(BigInteger enterpriseId) {
        long lEnterpriseId = MVEUtils.convertIDValueToLong(enterpriseId);
        if (lEnterpriseId <= 0) {
            return null;
        }
        Enterprise e = getEnterpriseById(lEnterpriseId);
        ModifyEnterpriseInfor rs = new ModifyEnterpriseInfor();
        rs.setBussinessCode(e.getBussinessCode());
        rs.setEnterpriseAddress(e.getEnterpriseAddress());
        rs.setEnterpriseEmail(e.getEnterpriseEmail());
        rs.setEnterpriseName(e.getEnterpriseName());
        rs.setEnterprisePhone(e.getEnterprisePhone());
        rs.setEnterpriseTaxCode(e.getEnterpriseTaxCode());
        rs.setProvinceCode(e.getProvinceCode());
        rs.setTownCode(e.getTownCode());
        rs.setDistrictCode(e.getDistrictCode());
        rs.setStreetNumber(e.getStreetNumber());
        rs.setBccsId(e.getBccsID());
        rs.setReferrerEmail(e.getReferrerEmail());
        rs.setBussinessType(e.getBussinessType());
        return rs;
    }

    private BaseResponse updateEnterprise(long updateUser, ModifyEnterpriseInfor enterpriseInfor,
                                          boolean isApproved, String accountEmail) {
        // Validate duplicate info //
        ValidateRegisterRequest registerInfo = new ValidateRegisterRequest();
        registerInfo.setEmail(accountEmail);
        registerInfo.setBusCode(enterpriseInfor.getBussinessCode());
        registerInfo.setTaxCode(enterpriseInfor.getEnterpriseTaxCode());
        BaseResponse validateRegisterInfoRs = validateEmailInfo(registerInfo,
                MVEUtils.convertLongToBigInteger(enterpriseInfor.getEnterpriseId()));
        if (validateRegisterInfoRs.getErrorCode() != ErrorDefine.OK) {
            return validateRegisterInfoRs;
        }

        // Call update enterprise api //
        ModifyEnterpriseRequest request = new ModifyEnterpriseRequest();
        request.setModifyUser(updateUser);
        request.setEnterpriseInfor(enterpriseInfor);
        request.setApproved(isApproved);
        ModifyEnterpriseResp rs = customerClient.updateApproveEnterprise(request);
        if (rs.getErrorCode() != ErrorDefine.OK) {
            throw new RuntimeException("updateEnterprise error + " + rs.getMessage());
        }
        return rs;
    }

    private BaseResponse updateRejectEnterprise(MVEUser user, long updateUser) {
        ModifyEnterpriseRequest request = new ModifyEnterpriseRequest();
        long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
        request.setEnterpriseInfor(new ModifyEnterpriseInfor());
        request.getEnterpriseInfor().setEnterpriseId(bussinessId);
        request.setModifyUser(updateUser);
        return customerClient.updateRejectEnterprise(request);
    }

    private BaseResponse rejectAccount(long approveUser, MVEUser user, String reason, SysApproveAccountRequest request) {
        BaseResponse rs = updateRejectEnterprise(user, approveUser);
        if (rs.getErrorCode() != ErrorDefine.OK) {
            return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
        }
        BigInteger approUser = MVEUtils.convertLongToBigInteger(approveUser);
        user.setStatus(StatusDefine.AccountStatus.REJECT.getValue());
        Date rejectDate = new Date();
        StringBuilder sb;
        if (!StringUtility.isNullOrEmpty(user.getRejectReason())) {
            sb = new StringBuilder(user.getRejectReason());
            sb.append("<br>");
        } else {
            sb = new StringBuilder();
        }
        sb.append("<b>").append(DateUtility.format(rejectDate, DateUtility.DATE_FORMAT_NOW));
        MVEUser approveUserInfor = userRepository.findByUserId(approUser);
        if (approveUserInfor != null) {
            sb.append(" - ").append(approveUserInfor.getUserName());
        }
        sb.append("</b><br>").append(reason);
        user.setRejectReason(sb.toString());
        user.setUpdateDate(rejectDate);
        user.setUpdateUser(approUser);
        user.setApproveUser(approUser);
        user.setIsSendApproveEmail(0);
        userRepository.save(user);
        return ResponseDefine.responseOK();
    }

	/*
	private BaseResponse approveAccount(long approveUser, MVEUser user, MultipartFile regisForm,
			MultipartFile bussinessLicense, MultipartFile personalCard, SysApproveAccountRequest request) {
		String newPassword = Utils.generationPassword(configValue.getLengthPassword());
		long documentUser = MVEUtils.convertIDValueToLong(user.getId());
		long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
		request.getEnterpriseInfor().setEnterpriseId(bussinessId);
		BaseResponse rs = updateEnterprise(approveUser, request.getEnterpriseInfor(), true);
		if (rs.getErrorCode() != ErrorDefine.OK) {
			return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
		}
		BaseResponse rsUploadDcument = uploadRegisAccountFile(approveUser, documentUser, bussinessId, regisForm,
				bussinessLicense, personalCard);
		if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
			return rsUploadDcument;
		}
		BigInteger approUser = MVEUtils.convertLongToBigInteger(approveUser);
		user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
		user.setUpdateDate(new Date());
		user.setUpdateUser(approUser);
		user.setApproveUser(approUser);
		user.setApprovedDate(new Date());
		user.setFullName(request.getAccountInfor().getFullName());
		user.setPhone(request.getAccountInfor().getPhone());
		user.setEmail(request.getAccountInfor().getEmail());
		user.setPersonalId(request.getAccountInfor().getPersonalId());
		user.setPosition(request.getAccountInfor().getPosition());
		user.setPersonalDate(request.getAccountInfor().getPersonalIdDate());
		user.setPersonalArea(request.getAccountInfor().getPersonalIdArea());
		user.setPassword(encoder.encode(newPassword));
		userRepository.save(user);
		ApproveAccountResponse response = new ApproveAccountResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setDefaultPassword(newPassword);
		return response;
	}*/

    private BaseResponse approveAccountV2(long approveUser, MVEUser user, MultipartFile[] regisForm,
                                          MultipartFile[] bussinessLicense, MultipartFile[] personalCard,
                                          MultipartFile[] other, long[] deletedMedia, SysApproveAccountRequest request) {
        String newPassword = Utils.generationPassword(configValue.getLengthPassword());
        long documentUser = MVEUtils.convertIDValueToLong(user.getId());
        long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
        request.getEnterpriseInfor().setEnterpriseId(bussinessId);
        BaseResponse rs = updateEnterprise(approveUser, request.getEnterpriseInfor(),
                true, request.getAccountInfor().getEmail());
        if (rs.getErrorCode() != ErrorDefine.OK) {
            return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
        }
        BaseResponse rsUploadDcument = uploadRegisAccountFileV2(approveUser, documentUser, bussinessId, regisForm,
                bussinessLicense, personalCard, other);
        if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
            return rsUploadDcument;
        }
        BigInteger approUser = MVEUtils.convertLongToBigInteger(approveUser);
        user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
        user.setUpdateDate(new Date());
        user.setUpdateUser(approUser);
        user.setApproveUser(approUser);
        user.setApprovedDate(new Date());
        user.setFullName(request.getAccountInfor().getFullName());
        user.setPhone(request.getAccountInfor().getPhone());
        user.setEmail(request.getAccountInfor().getEmail());
        user.setPersonalId(request.getAccountInfor().getPersonalId());
        user.setPosition(request.getAccountInfor().getPosition());
        user.setPersonalDate(request.getAccountInfor().getPersonalIdDate());
        user.setPersonalArea(request.getAccountInfor().getPersonalIdArea());
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        ApproveAccountResponse response = new ApproveAccountResponse();
        response.setErrorCode(ErrorDefine.OK);
        response.setDefaultPassword(newPassword);
        deleteAttachFile(deletedMedia, documentUser, approveUser);
        return response;
    }

	/*
	private BaseResponse updateRegisterInforUser(long updateUser, MVEUser user, MultipartFile regisForm,
			MultipartFile bussinessLicense, MultipartFile personalCard, SysUpdateRegisUserRequest request) {
		long documentUser = MVEUtils.convertIDValueToLong(user.getId());
		long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
		request.getEnterpriseInfor().setEnterpriseId(bussinessId);
		BaseResponse rs = updateEnterprise(updateUser, request.getEnterpriseInfor(), false);
		if (rs.getErrorCode() != ErrorDefine.OK) {
			return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
		}
		BaseResponse rsUploadDcument = uploadRegisAccountFile(updateUser, documentUser, bussinessId, regisForm,
				bussinessLicense, personalCard);
		if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
			return rsUploadDcument;
		}
		BigInteger approUser = MVEUtils.convertLongToBigInteger(updateUser);
		user.setUpdateDate(new Date());
		user.setUpdateUser(approUser);
		user.setFullName(request.getAccountInfor().getFullName());
		user.setPhone(request.getAccountInfor().getPhone());
		user.setEmail(request.getAccountInfor().getEmail());
		user.setPersonalId(request.getAccountInfor().getPersonalId());
		user.setPosition(request.getAccountInfor().getPosition());
		user.setPersonalDate(request.getAccountInfor().getPersonalIdDate());
		user.setPersonalArea(request.getAccountInfor().getPersonalIdArea());
		user.setStatus(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
		BaseResponse response = new BaseResponse();
		response.setErrorCode(ErrorDefine.OK);
		// Check send email notify approve
		if (user.getIsSendApproveEmail() == 0) {
			user.setIsSendApproveEmail(1);
			accountService.sendNotifyNewRegisEmail(request.getEnterpriseInfor(), request.getAccountInfor());
		}
		userRepository.save(user);
		return response;
	}*/

    private void deleteAttachFile(long[] deletedMedia, long user, long modifyUser) {
        if (deletedMedia == null || deletedMedia.length == 0) {
            return;
        }
        BigInteger userId = MVEUtils.convertLongToBigInteger(user);
        BigInteger modifyUserId = MVEUtils.convertLongToBigInteger(modifyUser);
        List<BigInteger> mediaIds = new ArrayList<BigInteger>();
        for (long id : deletedMedia) {
            mediaIds.add(MVEUtils.convertLongToBigInteger(id));
        }
        List<Attach> lstAttach = attachRepository.findByMediaIds(mediaIds);
        List<Attach> lstAttachDelete = new ArrayList<Attach>();
        List<Long> deletedIds = new ArrayList<Long>();
        for (Attach attach : lstAttach) {
            if (attach.getUserId().equals(userId)) {
                attach.setIsDelete(1);
                attach.setUpdateUser(modifyUserId);
                attach.setUpdateDate(new Date());
                deletedIds.add(MVEUtils.convertIDValueToLong(attach.getId()));
                lstAttachDelete.add(attach);
            }
        }
        attachRepository.saveAll(lstAttachDelete);
        DeleteListMediaRequest deleteRequest = new DeleteListMediaRequest();
        deleteRequest.setModifyUser(modifyUser);
        deleteRequest.setMediaIds(deletedIds);
        mediaClient.deleteListMedia(deleteRequest);
    }

    private BaseResponse updateRegisterInforUserV2(long updateUser, MVEUser user, MultipartFile[] regisForm,
                                                   MultipartFile[] bussinessLicense, MultipartFile[] personalCard, MultipartFile[] other,
                                                   long[] deletedMedia, SysUpdateRegisUserRequest request) {
        long documentUser = MVEUtils.convertIDValueToLong(user.getId());
        long bussinessId = MVEUtils.convertIDValueToLong(user.getBusinessId());
        request.getEnterpriseInfor().setEnterpriseId(bussinessId);
        BaseResponse rs = updateEnterprise(updateUser, request.getEnterpriseInfor(),
                false, request.getAccountInfor().getEmail());
        if (rs.getErrorCode() != ErrorDefine.OK) {
            return ResponseDefine.responseBaseError(rs.getErrorCode(), rs.getMessage());
        }
        BaseResponse rsUploadDcument = uploadRegisAccountFileV2(updateUser, documentUser, bussinessId, regisForm,
                bussinessLicense, personalCard, other);
        if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
            return rsUploadDcument;
        }
        BigInteger approUser = MVEUtils.convertLongToBigInteger(updateUser);
        user.setUpdateDate(new Date());
        user.setUpdateUser(approUser);
        user.setFullName(request.getAccountInfor().getFullName());
        user.setPhone(request.getAccountInfor().getPhone());
        user.setEmail(request.getAccountInfor().getEmail());
        user.setPersonalId(request.getAccountInfor().getPersonalId());
        user.setPosition(request.getAccountInfor().getPosition());
        user.setPersonalDate(request.getAccountInfor().getPersonalIdDate());
        user.setPersonalArea(request.getAccountInfor().getPersonalIdArea());
        user.setStatus(StatusDefine.AccountStatus.WAITING_APPROVE.getValue());
        BaseResponse response = new BaseResponse();
        response.setErrorCode(ErrorDefine.OK);
        // Check send email notify approve
        if (user.getIsSendApproveEmail() == 0) {
            user.setIsSendApproveEmail(1);
            accountService.sendNotifyNewRegisEmail(request.getEnterpriseInfor(), request.getAccountInfor());
        }
        userRepository.save(user);
        deleteAttachFile(deletedMedia, documentUser, updateUser);
        return response;
    }

    private void updateBussinessType(long updateUser, long enterpriseId, ModifyEnterpriseInfor infor) {
        try {
            ModifyEnterpriseRequest modifyEnterpriseReq = new ModifyEnterpriseRequest();
            modifyEnterpriseReq.setEnterpriseInfor(infor);
            customerClient.updateBussinessType(updateUser, enterpriseId, modifyEnterpriseReq);
        } catch (Exception e) {
            MVELoggingUtils.logMVEException(e);
        }
    }

    @Override
    public BaseResponse createAccount(long updateUser, SysCreateAccountRequest request, MultipartFile[] files,
                                      MultipartFile avartar) {
        MVEUser rsUser = null;
        try {
            long bussinessId = 0;
            String newPassword = Utils.generationPassword(configValue.getLengthPassword());
            MVEUser user = createUserFromRequest(request, newPassword);
            user.setCreateUser(MVEUtils.convertLongToBigInteger(updateUser));
            if (request.getRoleId() == RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId()) {
                // Enterprise admin
                String userName = user.getUserName();
                //userName = userName + "_" + configValue.getSuffixAdmin().toUpperCase();
                BaseResponse rsValidate = validateUsername(userName);
                if (rsValidate != null) {
                    return rsValidate;
                }
                user.setUserName(userName);
                BaseResponse rs = getEnterpriseInforFromCache(request.getEnterpriseIdentify());
                if (rs.getErrorCode() != ErrorDefine.OK) {
                    return rs;
                }
                bussinessId = ((ModifyEnterpriseResp) rs).getEnterpriseId();
                user.setBusinessId(MVEUtils.convertLongToBigInteger(bussinessId));
            } else {
                BaseResponse rsValidate = validateUsername(request.getUsername());
                if (rsValidate != null) {
                    return rsValidate;
                }
            }
            rsUser = userRepository.save(user);
            BaseResponse rsUploadAvartar = uploadAvatar(avartar, bussinessId,
                    MVEUtils.convertIDValueToLong(rsUser.getId()));
            if (rsUploadAvartar != null) {
                if (rsUploadAvartar.getErrorCode() != ErrorDefine.OK) {
                    userRepository.delete(rsUser);
                    deleteEnterprise(updateUser, bussinessId);
                    return rsUploadAvartar;
                } else {
                    List<MediaItem> rsItems = ((UploadMediaResponse) rsUploadAvartar).getLstMedia();
                    rsUser.setAvartar(rsItems.get(0).getMediaPath());
                }
            }
            BaseResponse rsUploadDcument = uploadDocumentFile(updateUser, bussinessId, files,
                    MVEUtils.convertIDValueToLong(rsUser.getId()));
            if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
                userRepository.delete(rsUser);
                deleteEnterprise(updateUser, bussinessId);
                return rsUploadDcument;
            }
            saveUserRole(request.getRoleId(), rsUser);
//			deleteEnterpriseFromCache(request.getEnterpriseIdentify());
            CreateAccountResponse reponse = new CreateAccountResponse();
            reponse.setDefaultPassword(newPassword);
            reponse.setEnterpriseId(bussinessId);
            reponse.setUserName(rsUser.getUserName());
            reponse.setErrorCode(ErrorDefine.OK);
            return reponse;
        } catch (Exception e) {
            if (rsUser != null) {
                userRepository.delete(rsUser);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseResponse resetPassword(long updateUser, AdminResetPassRequest request) {
        BaseResponse response;
        String newPassword = Utils.generationPassword(configValue.getLengthPassword());
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        response = validateValidAccount(user);
        if (response != null) {
            return response;
        }
        user.setPassword(encoder.encode(newPassword));
        user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
        user.setUpdateDate(new Date());
        user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
        userRepository.save(user);
        response = new AdminResetPassResponse();
        response.setErrorCode(ErrorDefine.OK);
        ((AdminResetPassResponse) response).setDefaultPass(newPassword);
        ((AdminResetPassResponse) response).setUsername(user.getUserName());
        ((AdminResetPassResponse) response).setEmail(user.getEmail());
        return response;
    }

    @Override
    public BaseResponse updateStatus(long updateUser, AdminUpdateUserStatusRequest request) {
        BaseResponse response;
        List<MVEUser> updateUsers = new ArrayList<MVEUser>();
        Date updateTime;
        for (UserStatus item : request.getLstUserStatus()) {
            MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(item.getUserId()));
            response = validateValidAccount(user);
            if (response != null) {
                return response;
            }
            response = validateStatusAccount(user);
            if (response != null) {
                return response;
            }
            updateTime = new Date();
            user.setStatus(item.getStatus());
            user.setUpdateDate(updateTime);
            user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
            updateUsers.add(user);
            if (AccountStatus.STATUS_LOCKED.getValue() == item.getStatus()) {
                setLastModifyDate(item.getUserId(), updateTime);
            }
        }
        if (!updateUsers.isEmpty()) {
            userRepository.saveAll(updateUsers);
        }
        return ResponseDefine.responseOK();
    }

    @Override
    public BaseResponse deleteAccount(long updateUser, AdminDeleteAccountRequest request) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateValidAccount(user);
        if (response != null) {
            return response;
        }
        Date updateTime = new Date();
        user.setUserName(user.getUserName() + "_DEL_" + System.currentTimeMillis());
        user.setIsDelete(1);
        user.setUpdateDate(updateTime);
        user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
        userRepository.save(user);
        setLastModifyDate(request.getUserId(), updateTime);
        return ResponseDefine.responseOK();
    }

    @Override
    public BaseResponse updateAccount(long updateUser, SysAdminUpdateAccountRequest request, MultipartFile[] files) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateValidAccount(user);
        if (response != null) {
            return response;
        }
        long enterpriseId = MVEUtils.convertIDValueToLong(user.getBusinessId());
        BaseResponse rsUploadDcument = uploadDocumentFile(updateUser,
                enterpriseId, files,
                MVEUtils.convertIDValueToLong(user.getId()));
        if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
            return rsUploadDcument;
        }
        // Update bussiness type
        updateBussinessType(updateUser, enterpriseId, request.getEnterpriseInformation());
        // End update bussiness type
        String birthday = request.getAccountInformation().getBirthday();
        if (birthday != null) {
            user.setBirthday(DateUtility.convert(birthday, DateUtility.DATE_FORMAT_STR));
        }
        user.setAddress(request.getAccountInformation().getAddress());
        user.setFullName(request.getAccountInformation().getFullName());
        user.setPhone(request.getAccountInformation().getPhone());
        user.setEmail(request.getAccountInformation().getEmail());
        user.setSex(request.getAccountInformation().getSex());
        user.setPersonalId(request.getAccountInformation().getPersonalId());
        user.setUpdateDate(new Date());
        user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
        userRepository.save(user);
        return ResponseDefine.responseOK();
    }

    @Override
    public BaseResponse updateAvartar(long updateUser, long userId, MultipartFile avartar) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(userId));
        if (user == null) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
        }
        return excuteUpdateAvartar(user, avartar, MVEUtils.convertIDValueToLong(user.getBusinessId()), updateUser);
    }

    @Override
    public BaseResponse updateUserRole(long updateUser, AdminUpdateUserRoleRequest request) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateValidAccount(user);
        if (response != null) {
            return response;
        }
        if (user.getBusinessId() != null && MVEUtils.convertIDValueToLong(user.getBusinessId()) != 0) {
            return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
        }
        List<UserRole> userRoles = new ArrayList<UserRole>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<BigInteger> roleIds = new ArrayList<BigInteger>();
            for (long roleId : request.getRoles()) {
                roleIds.add(MVEUtils.convertLongToBigInteger(roleId));
            }
            List<Role> newRoles = roleRepository.findRolesByIds(roleIds);
            for (Role rsRole : newRoles) {
                if ((rsRole.getBusinessId() != null && MVEUtils.convertIDValueToLong(rsRole.getBusinessId()) != 0)) {
                    return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
                }
                if (rsRole.getIsDelete() == 1) {
                    return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("error.role.deleted"));
                }
                UserRole userRole = new UserRole();
                userRole.setCreateDate(new Date());
                userRole.setCreateUser(MVEUtils.convertLongToBigInteger(updateUser));
                userRole.setIsDelete(0);
                userRole.setRoleId(rsRole.getId());
                userRole.setUserId(user.getId());
                userRoles.add(userRole);
            }
        }
        excuteUpdateUserRoleTransaction(updateUser, user.getId(), userRoles);
        setLastModifyDate(request.getUserId(), new Date());
        return ResponseDefine.responseOK();
    }

    @Override
    public BaseResponse getApproveUserInfor(String authToken, GetUserRequest request) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateApproveAccount(user);
        if (response != null) {
            return response;
        }
        response = new SysDetailApproveReponse();
        ((SysDetailApproveReponse) response).setErrorCode(ErrorDefine.OK);
        ((SysDetailApproveReponse) response).setUserId(MVEUtils.convertIDValueToLong(user.getId()));
        ((SysDetailApproveReponse) response).setAccountInfor(createRegisInforFromEntity(user));
        ((SysDetailApproveReponse) response).setEnterpriseInfor(getEnterpriseInfor(user.getBusinessId()));
        ((SysDetailApproveReponse) response).setLstAttachs(getListAttachByUserId(authToken, request.getUserId()));
        return response;
    }

    /**
     @Override public BaseResponse approveUser(long approveUser, MultipartFile regisForm, MultipartFile bussinessLicense,
     MultipartFile personalCard, SysApproveAccountRequest request) {
     MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
     BaseResponse response = validateApproveAccount(user);
     if (response != null) {
     return response;
     }
     if (request.getStatus() == StatusDefine.AccountStatus.REJECT.getValue()) {
     return rejectAccount(approveUser, user, request.getReason(), request);
     } else {
     return approveAccount(approveUser, user, regisForm, bussinessLicense, personalCard, request);
     }
     }**/

    /**
     * @Override public BaseResponse updateRegisterInforUser(long updateUser, MultipartFile regisForm,
     * MultipartFile bussinessLicense, MultipartFile personalCard, SysUpdateRegisUserRequest request) {
     * MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
     * BaseResponse response = validateApproveAccount(user);
     * if (response != null) {
     * return response;
     * }
     * return updateRegisterInforUser(updateUser, user, regisForm, bussinessLicense, personalCard, request);
     * }
     **/

    @Override
    public BaseResponse updateRegisterInforUserV2(long updateUser, MultipartFile[] regisForm,
                                                  MultipartFile[] bussinessLicense, MultipartFile[] personalCard, MultipartFile[] other,
                                                  SysUpdateRegisUserRequest request, long[] deletedMedia) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateApproveAccount(user);
        if (response != null) {
            return response;
        }
        return updateRegisterInforUserV2(updateUser, user, regisForm,
                bussinessLicense, personalCard, other, deletedMedia, request);
    }

    @Override
    public BaseResponse approveUserV2(long approveUser, MultipartFile[] regisForm, MultipartFile[] bussinessLicense,
                                      MultipartFile[] personalCard, MultipartFile[] other, SysApproveAccountRequest request,
                                      long[] deletedMedia) {
        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateApproveAccount(user);
        if (response != null) {
            return response;
        }
        if (request.getStatus() == StatusDefine.AccountStatus.REJECT.getValue()) {
            return rejectAccount(approveUser, user, request.getReason(), request);
        } else {
            return approveAccountV2(approveUser, user, regisForm, bussinessLicense,
                    personalCard, other, deletedMedia, request);
        }
    }

    @Override
    public BaseResponse updateAccountV2(long updateUser, SysAdminUpdateAccountRequest request,
                                        MultipartFile[] regisForm, MultipartFile[] bussinessLicense, MultipartFile[] personalCard,
                                        MultipartFile[] other) {

        MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
        BaseResponse response = validateValidAccount(user);
        if (response != null) {
            return response;
        }
        long enterpriseId = MVEUtils.convertIDValueToLong(user.getBusinessId());
        BaseResponse rsUploadDcument = uploadRegisAccountFileV2(updateUser, request.getUserId(),
                enterpriseId, regisForm, bussinessLicense, personalCard, other);
        if (rsUploadDcument != null && rsUploadDcument.getErrorCode() != ErrorDefine.OK) {
            return rsUploadDcument;
        }
        // Update bussiness type
        updateBussinessType(updateUser, enterpriseId, request.getEnterpriseInformation());
        // End update bussiness type
        String birthday = request.getAccountInformation().getBirthday();
        if (birthday != null) {
            user.setBirthday(DateUtility.convert(birthday, DateUtility.DATE_FORMAT_STR));
        }
        user.setAddress(request.getAccountInformation().getAddress());
        user.setFullName(request.getAccountInformation().getFullName());
        user.setPhone(request.getAccountInformation().getPhone());
        user.setEmail(request.getAccountInformation().getEmail());
        user.setSex(request.getAccountInformation().getSex());
        user.setPersonalId(request.getAccountInformation().getPersonalId());
        user.setUpdateDate(new Date());
        user.setUpdateUser(MVEUtils.convertLongToBigInteger(updateUser));
        userRepository.save(user);
        return ResponseDefine.responseOK();
    }


    @Override
    public ResponseEntity<InputStreamResource> downloadTemplateImportAccount(ConfigValue configValue) throws IOException, Exception {
        String pathFile = "TemplateImportAccount.xls";

        DynamicExport dynamicExport = new DynamicExport(TemplateResouces.getImportFile(pathFile), 0, false);
        List<Category> lstBusinessType = getListBusinessType();
        dynamicExport.setActiveSheet(1);
        int row = 0;
        for (Category cat : lstBusinessType) {
            dynamicExport.setText(cat.getNames().get("vi"), 0, row);
            row++;
        }
        String fileName = dynamicExport.exportFile(configValue.getPathTmp(), "TemplateImportAccount");
        File file = new File(fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                // Content-Type
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }

    @Override
    public ResponseEntity<InputStreamResource> actionImportAccount(long updateUser, MultipartFile fileImport, ConfigValue configValue) throws IOException, Exception {
        String cfgName = "TemplateImportAccountConfig.cfg";
        String importFileName = new SimpleDateFormat("yyyyMMddHHmmss_").format(new Date()) + "ImportAccount.xls";
        ImportUtils importUtils = new ImportUtils(TemplateResouces.getImportFile(cfgName));
        List<Object[]> dataList = new LinkedList<Object[]>();
        Map<Integer, String> mapRowError = new HashMap<>();
        importUtils.validateCommon(fileImport, configValue.getPathTmp(), importFileName, dataList);
        List<String> lstPassword = new ArrayList<>();
        if (!dataList.isEmpty()) {
            List<Category> lstBusinessType = getListBusinessType();
            Map<String, String> mapHinhThucKD = new HashMap<>();
            lstBusinessType.forEach(x -> {
                mapHinhThucKD.put(x.getNames().get("vi"), x.getCode());
            });
            List<RegisterAccountRequest> listAccountInforRequest = new ArrayList<>();
            List<Integer> listIndexDataValid = new ArrayList<>();
            // validate nghiep vu
            for (int i = 0; i < dataList.size(); i++) {
                Object[] obj = dataList.get(i);
                int j = 1;
                boolean isValid = true;
                String tenKH = CommonUtil.NVL((String) obj[j++]);
                String maSoThue = CommonUtil.NVL((String) obj[j++]);
                String DKKD = CommonUtil.NVL((String) obj[j++]);
                String linhVucKD = CommonUtil.NVL((String) obj[j++]);
                String linhVucValue = null;
                String diaChi = CommonUtil.NVL((String) obj[j++]);
                String taiKhoan = CommonUtil.NVL((String) obj[j++]);
                String tenNguoiDung = CommonUtil.NVL((String) obj[j++]);
                String position = CommonUtil.NVL((String) obj[j++]);
                String email = CommonUtil.NVL((String) obj[j++]);
                String soDienThoai = CommonUtil.NVL((String) obj[j]);
                // email hop le
                if (!email.isEmpty() && !CommonUtil.isValidEmail(email)) {
                    importUtils.addError(i, -1, "Email không đúng định đạng", null);
                    isValid = false;
                } else if (!email.isEmpty()) {
                    // validate email info
                    ValidateRegisterRequest request = new ValidateRegisterRequest();
                    request.setEmail(email);
                    request.setTaxCode(maSoThue);
                    request.setBusCode(DKKD);
                    BaseResponse validateRegisterInfoRs = validateEmailInfo(request, null);
                    if (validateRegisterInfoRs.getErrorCode() != ErrorDefine.OK) {
                        importUtils.addError(i, -1, "Email đã đăng ký tài khoản", null);
                        isValid = false;
                    }
                }
                // tai khoan min 6 ki tu
                if (0 < taiKhoan.length() && taiKhoan.length() < 6) {
                    importUtils.addError(i, -1, "Tài khoản không được nhỏ hơn 6 kí tự", null);
                    isValid = false;
                } else {
                    // validate user
                    BaseResponse rsValidate = validateUsername(taiKhoan);
                    if (rsValidate != null) {
                        importUtils.addError(i, -1, "Tài khoản đã tồn tại", null);
                        isValid = false;
                    }
                }
                // hinh thuc kinh doanh hop le
                if (!linhVucKD.isEmpty() && !mapHinhThucKD.containsKey(linhVucKD)) {
                    importUtils.addError(i, -1, "Lĩnh vực kinh doanh không hợp lệ", null);
                    isValid = false;
                } else if (!linhVucKD.isEmpty()) {
                    linhVucValue = mapHinhThucKD.get(linhVucKD);
                }
                // dien thoai hop le
                if (!soDienThoai.isEmpty() && !CommonUtil.isValidPhoneNumber(soDienThoai)) {
                    importUtils.addError(i, -1, "Số điện thoại không đúng định đạng", null);
                    isValid = false;
                }
                for (ImportErrorBean errorBean : importUtils.getErrorList()) {
                    if (errorBean.getRow() - 1 == importUtils.getRowList().get(i)) {
                        isValid = false;
                    }
                }

                if (isValid) {
                    RegisterAccountRequest registerAccountRequest = new RegisterAccountRequest();
                    RegisterAccountInfor registerAccountInfor = new RegisterAccountInfor();
                    ModifyEnterpriseInfor enterpriseInfor = new ModifyEnterpriseInfor();

                    registerAccountInfor.setUsername(taiKhoan);
                    registerAccountInfor.setFullName(tenNguoiDung);
                    registerAccountInfor.setEmail(email);
                    registerAccountInfor.setPhone(soDienThoai);
                    registerAccountInfor.setPosition(position);

                    enterpriseInfor.setEnterpriseName(tenKH);
                    enterpriseInfor.setEnterpriseTaxCode(maSoThue);
                    enterpriseInfor.setBussinessCode(DKKD);
                    enterpriseInfor.setEnterpriseAddress(diaChi);
                    enterpriseInfor.setBussinessType(linhVucValue);

                    registerAccountRequest.setAccountInfor(registerAccountInfor);
                    registerAccountRequest.setEnterpriseInfor(enterpriseInfor);

                    listAccountInforRequest.add(registerAccountRequest);
                    listIndexDataValid.add(i);
                }
            }
            // Xu ly luu du lieu
            int indexData = 0;
            for (RegisterAccountRequest registerAccountRequest : listAccountInforRequest) {
                MVEUser rsUser = null;
                String newPassword = Utils.generationPassword(CommonUtil.NVL(configValue.getLengthPassword(), 10));
                long bussinessId = 0;
                try {
                    BaseResponse rs = accountService.createEnterprise(registerAccountRequest.getEnterpriseInfor());
                    if (rs.getErrorCode() != ErrorDefine.OK) {
                        importUtils.addError(listIndexDataValid.get(indexData), -1, rs.getMessage(), null);
                        continue;
                    }
                    bussinessId = ((ModifyEnterpriseResp) rs).getEnterpriseId();
                    registerAccountRequest.getEnterpriseInfor().setEnterpriseId(bussinessId);

                    ModifyEnterpriseRequest request = new ModifyEnterpriseRequest();
                    request.setModifyUser(updateUser);
                    request.setEnterpriseInfor(registerAccountRequest.getEnterpriseInfor());
                    request.setApproved(true);
                    ModifyEnterpriseResp rsApprove = customerClient.updateApproveEnterprise(request);
                    if (rsApprove.getErrorCode() != ErrorDefine.OK) {
                        importUtils.addError(listIndexDataValid.get(indexData), -1, rs.getMessage(), null);
                        continue;
                    }
                    MVEUser user = accountService.createUserFromRegisterRequest(registerAccountRequest.getAccountInfor());
                    user.setBusinessId(MVEUtils.convertLongToBigInteger(bussinessId));
                    user.setCreateUser(MVEUtils.convertLongToBigInteger(updateUser));
                    user.setIsSendApproveEmail(1);
                    user.setApproveUser(MVEUtils.convertLongToBigInteger(updateUser));
                    user.setApprovedDate(new Date());
                    user.setStatus(StatusDefine.AccountStatus.STATUS_NEW.getValue());
                    user.setPassword(encoder.encode(newPassword));
                    rsUser = userRepository.save(user);
                    Role role = roleRepository.findByRoleCode(configValue.getRoleCodeDefaultImportAccount());
                    if (role != null) {
                        saveUserRole(MVEUtils.convertIDValueToLong(role.getId()), rsUser);
                    }
                    accountService.sendNotifyImportAccountEmail(registerAccountRequest.getEnterpriseInfor(), registerAccountRequest.getAccountInfor(), newPassword);
                    lstPassword.add(newPassword);
                } catch (Exception e) {
                    try {
                        if (rsUser != null) {
                            userRepository.delete(rsUser);
                        }
                        if (bussinessId != 0) {
                            deleteEnterprise(-1l, bussinessId);
                        }
                    } catch (Exception e1) {
                        throw new RuntimeException(e1);
                    }
                    throw new RuntimeException(e);
                }
            }
        }
        // Xu ly fill ket qua import
        String fileName = "";
        DynamicExport dynamicExport = null;
        try {
            int endCell = 11;
            dynamicExport = new DynamicExport(new FileInputStream(configValue.getPathTmp() + importFileName), 0, false);
            dynamicExport.setEntry("Kết quả import", endCell, 2);
            if (dataList.isEmpty()) {
                dynamicExport.setEntry("Không có dữ liệu import", endCell, 3);
            }
            if (importUtils.hasError()) {
                for (ImportErrorBean errorBean : importUtils.getErrorList()) {
                    if (!mapRowError.containsKey(errorBean.getRow() - 1)) {
                        mapRowError.put(errorBean.getRow() - 1, errorBean.getDescription());
                    } else {
                        mapRowError.put(errorBean.getRow() - 1, mapRowError.get(errorBean.getRow() - 1) + "\n" + errorBean.getDescription());
                    }
                }
            }
            int i = 0;
            for (Integer row : importUtils.getRowList()) {
                if (!mapRowError.containsKey(row)) {
                    dynamicExport.setEntry("Thành công" + " " + lstPassword.get(i++), endCell, row);
                } else {
                    dynamicExport.setEntry(mapRowError.get(row), endCell, row);
                }
            }
            fileName = dynamicExport.exportFile(configValue.getPathTmp(), "ImportAccountResult");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("ERROR" + configValue.getPathTmp() + importFileName);
            LOGGER.error(e.getMessage(), e);
        }
        if (!fileName.isEmpty()) {
            File file = new File(fileName);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    // Content-Disposition
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                    // Content-Type
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    // Contet-Length
                    .contentLength(file.length()) //
                    .body(resource);
        } else {
            return ResponseEntity.ok()
                    // Content-Disposition
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=")
                    // Content-Type
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    // Contet-Length
                    .contentLength(0) //
                    .body(null);
        }

    }

    private List<Category> getListBusinessType() {
        GetListCategoriesRequest request = new GetListCategoriesRequest();
        List<Category> list = new ArrayList<Category>();
        request.setCategoriesType("BUSSINESS_TYPE");
        try {
            GetListCategoriesResponse res = categoriesClient.getListCategories(request);
            list = res.getLstCategories();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return list;
    }
}
