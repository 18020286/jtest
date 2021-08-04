package com.viettel.mve.authservice.service.impl.sysadmin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.authservice.core.db.entities.AccountProvinceLimit;
import com.viettel.mve.authservice.core.db.entities.MVEUser;
import com.viettel.mve.authservice.core.db.entities.RolePermission;
import com.viettel.mve.authservice.core.db.entities.UserRole;
import com.viettel.mve.authservice.core.db.repository.AccProvinceLimitRepository;
import com.viettel.mve.authservice.core.db.repository.PermissionRepositoryCustom;
import com.viettel.mve.authservice.core.db.repository.RolePermissionRepository;
import com.viettel.mve.authservice.core.db.repository.RoleRepository;
import com.viettel.mve.authservice.core.external.intercomm.CategoriesClient;
import com.viettel.mve.authservice.service.impl.base.BaseAccountServiceImpl;
import com.viettel.mve.authservice.service.sysadmin.SysAdminViewAccountService;
import com.viettel.mve.client.constant.ErrorDefine;
import com.viettel.mve.client.constant.GlobalConstant;
import com.viettel.mve.client.constant.GlobalConstant.CategoryType;
import com.viettel.mve.client.constant.RoleDefine;
import com.viettel.mve.client.request.auth.AdminGetListRoleByUserRequest;
import com.viettel.mve.client.request.auth.AdminViewAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysExportExcelListAccountRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchEnterpriseRequest;
import com.viettel.mve.client.request.auth.sysadmin.SysSearchListAccountRequest;
import com.viettel.mve.client.request.categories.GetDetailAddressRequest;
import com.viettel.mve.client.request.categories.GetListCategoriesRequest;
import com.viettel.mve.client.response.BaseResponse;
import com.viettel.mve.client.response.PagingObject;
import com.viettel.mve.client.response.auth.AdminGetDetailRolesByUserResponse;
import com.viettel.mve.client.response.auth.AdminGetListRoleByUserResponse;
import com.viettel.mve.client.response.auth.object.AdminAccountInformation;
import com.viettel.mve.client.response.auth.object.ListAccountItem;
import com.viettel.mve.client.response.auth.object.PermissionItem;
import com.viettel.mve.client.response.auth.object.RoleItem;
import com.viettel.mve.client.response.auth.sysadmin.SysAdminViewAccountResponse;
import com.viettel.mve.client.response.auth.sysadmin.SysSearchListAccountResponse;
import com.viettel.mve.client.response.categories.GetDetailAddressResponse;
import com.viettel.mve.client.response.categories.GetListCategoriesResponse;
import com.viettel.mve.client.response.categories.GetListProvinceResponse;
import com.viettel.mve.client.response.categories.object.AddressItem;
import com.viettel.mve.client.response.categories.object.Category;
import com.viettel.mve.client.response.customer.object.Enterprise;
import com.viettel.mve.client.response.customer.object.EnterpriseBasicInfor;
import com.viettel.mve.client.response.customer.object.EnterpriseServiceItem;
import com.viettel.mve.client.response.sme.object.InfoExport;
import com.viettel.mve.common.base.response.ResponseDefine;
import com.viettel.mve.common.datetime.DateUtility;
import com.viettel.mve.common.intercomm.request.GetServiceByEnterpriseIdRequest;
import com.viettel.mve.common.intercomm.request.SearchEnterpriseByIDSReq;
import com.viettel.mve.common.intercomm.response.GetAllServiceByEnterpriseResponse;
import com.viettel.mve.common.intercomm.response.SearchEnterpriseResp;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.number.NumberUtils;
import com.viettel.mve.common.stringutils.StringUtility;
import com.viettel.mve.common.utils.MVEUtils;
import com.viettel.mve.common.utils.MessagesUtils;
import com.viettel.mve.common.utils.excel.AccountExport;

@Service("SysAdminViewAccountService")
public class SysAdminViewAccountServiceImpl extends BaseAccountServiceImpl implements SysAdminViewAccountService {
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepositoryCustom permissionRepository;

	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	
	@Autowired
	private AccProvinceLimitRepository provinceLimitRepository;
	
	@Autowired
	private ConfigValue configValue;
	
	@Autowired
	private CategoriesClient categoriesClient;

	private boolean checkIsEnterpriseAdmin(MVEUser user) {
		BigInteger enterpriseAdminRole = MVEUtils
				.convertLongToBigInteger(RoleDefine.SystemRole.ENTERPRISE_ADMIN.getId());
		List<UserRole> roles = userRoleRepository.findByUserAndRole(user.getId(), enterpriseAdminRole);
		return roles != null && !roles.isEmpty();
	}

	private AdminAccountInformation getAccountInforFromMVEUser(String authToken, MVEUser user) {
		AdminAccountInformation rs = new AdminAccountInformation();
		rs.setAddress(user.getAddress());
		rs.setEmail(user.getEmail());
		if (user.getBirthday() != null) {
			rs.setBirthday(DateUtility.format(user.getBirthday(), DateUtility.DATE_FORMAT_STR));
		}
		rs.setAvartar(user.getAvartar());
		rs.setFullName(user.getFullName());
		rs.setPersonalId(user.getPersonalId());
		rs.setPhone(user.getPhone());
		rs.setSex(user.getSex());
		rs.setUserId(MVEUtils.convertIDValueToLong(user.getId()));
		rs.setUsername(user.getUserName());
		int accountType;
		if (user.getBusinessId() == null || MVEUtils.convertIDValueToLong(user.getBusinessId()) == 0) {
			accountType = GlobalConstant.AccountType.ACCOUNT_TYPE_SYSTEM;
		} else {
			accountType = GlobalConstant.AccountType.ACCOUNT_TYPE_ENTERPRISE;
			boolean isEnterpriseAdmin = checkIsEnterpriseAdmin(user);
			rs.setEnterpriseAdmin(isEnterpriseAdmin);
			if (isEnterpriseAdmin) {
				// Lay danh sach file dinh kem
				rs.setLstAttach(getListAttachByUserId(authToken, MVEUtils.convertIDValueToLong(user.getId())));
			}
		}
		rs.setAccountType(accountType);
		return rs;
	}

	private List<EnterpriseServiceItem> getListServiceByEnterprise(BigInteger enterpriseId) {
		if (enterpriseId == null || MVEUtils.convertIDValueToLong(enterpriseId) == 0) {
			return null;
		}
		GetServiceByEnterpriseIdRequest request = new GetServiceByEnterpriseIdRequest();
		request.setEnterpriseId(MVEUtils.convertIDValueToLong(enterpriseId));
		request.setLangCode(MVEUtils.getCurrentLocaleCode());
		GetAllServiceByEnterpriseResponse response = customerClient.getListServiceByEnterprise(request);
		if (response.getErrorCode() != ErrorDefine.OK) {
			throw new RuntimeException("getListServiceByEnterprise error");
		} else {
			return response.getLstService();
		}
	}

	private EnterpriseBasicInfor getEnterpriseInfor(BigInteger enterpriseId) {
		long lEnterpriseId = MVEUtils.convertIDValueToLong(enterpriseId);
		if (lEnterpriseId <= 0) {
			return null;
		}
		Enterprise e = getEnterpriseById(lEnterpriseId);
		EnterpriseBasicInfor rs = new EnterpriseBasicInfor();
		rs.setEnterpriseId(lEnterpriseId);
		rs.setAddress(e.getEnterpriseAddress());
		rs.setEnterpriseName(e.getEnterpriseName());
		rs.setPhone(e.getEnterprisePhone());
		rs.setEmail(e.getEnterpriseEmail());
		rs.setDocumentNo(e.getBccsID());
		rs.setProvinceCode(e.getProvinceCode());
		rs.setTownCode(e.getTownCode());
		rs.setDistrictCode(e.getDistrictCode());
		rs.setReferrerEmail(e.getReferrerEmail());
		rs.setBussinessCode(e.getBussinessCode());
		rs.setTaxNo(e.getEnterpriseTaxCode());
		rs.setBussinessType(e.getBussinessType());
		rs.setBussinessTypeName(e.getBussinessTypeName());
		return rs;
	}
	
	private String getApproveInfor(MVEUser user) {
		StringBuilder sb = new StringBuilder();
		if(user.getApproveUser() != null && user.getApprovedDate() != null) {
			MVEUser approveUserInfor = userRepository.findByUserId(user.getApproveUser());
			if(approveUserInfor != null) {
				sb.append("<b>").append(DateUtility.format(user.getApprovedDate(), DateUtility.DATE_FORMAT_NOW));
				sb.append(" - ").append(approveUserInfor.getUserName());
				sb.append("</b><br>").append(MessagesUtils.getMessage("content.approved"));
			}
		}
		return sb.toString();
	}

	private SearchEnterpriseResp searchEnterprise(SysSearchListAccountRequest request) {
		SysSearchEnterpriseRequest enterpriseRequest = (SysSearchEnterpriseRequest)request;
		if(StringUtility.isNullOrEmpty(enterpriseRequest.getCompanySearch())
				&& StringUtility.isNullOrEmpty(enterpriseRequest.getProvinceCode()) 
				&& (enterpriseRequest.getProvinceLimits() == null || enterpriseRequest.getProvinceLimits().isEmpty())) {
			// Skip. No search by enterprise
			return null;
		}
		SearchEnterpriseResp response = customerClient.searchEnterprise(enterpriseRequest);
		if (response.getErrorCode() != ErrorDefine.OK) {
			throw new RuntimeException("searchEnterprise error");
		} else {
			List<BigInteger> rs = new ArrayList<BigInteger>();
			if (response.getLstEnterprise() != null && !response.getLstEnterprise().isEmpty()) {
				for (EnterpriseBasicInfor e : response.getLstEnterprise()) {
					rs.add(MVEUtils.convertLongToBigInteger(e.getEnterpriseId()));
				}
			}
			response.setRs(rs);
			return response;
		}
	}
	
	private List<EnterpriseBasicInfor> searchEnterpriseByIds(List<Long> eids) {
		List<EnterpriseBasicInfor> lstEnterprise = new ArrayList<EnterpriseBasicInfor>();
		SearchEnterpriseByIDSReq request = new SearchEnterpriseByIDSReq();
		request.setEnterpriseIds(eids);
		SearchEnterpriseResp response = customerClient.searchEnterpriseByIds(request);
		if (response.getErrorCode() == ErrorDefine.OK) {
			lstEnterprise.addAll(response.getLstEnterprise());
		}
		return lstEnterprise;
	}
	
	@Override
	public void exportListAccount(long currentUser, SysExportExcelListAccountRequest request,
			HttpServletResponse httpResponse) {
		List<ListAccountItem> lstAccountItemAlls = new ArrayList<ListAccountItem>();		
		int pageSize = 1000;		
		int page = 1;
		request.setPage(page);
		request.setPageSize(pageSize);
		String fromDate = request.getFromDate();
		String toDate = request.getToDate();
		SysSearchListAccountResponse response =  (SysSearchListAccountResponse)searchListAccount(currentUser, request);
		if(response.getErrorCode()==ErrorDefine.OK) {
			List<ListAccountItem> lstAccounts = response.getListData();
			lstAccountItemAlls.addAll(lstAccounts);
			long total = response.getTotalRow();
			long totalRemain = total-pageSize;
			while (totalRemain>0) {				
				page++;
				request.setPage(page);
				response =  (SysSearchListAccountResponse)searchListAccount(currentUser, request);
				lstAccounts = response.getListData();
				lstAccountItemAlls.addAll(lstAccounts);
				totalRemain = totalRemain-pageSize;
			}
			String city ="";
			if(StringUtility.isNullOrEmpty(request.getProvinceCode())) {
				city=MessagesUtils.getMessage("account.export.all");
			}
			else {
				String locale = MVEUtils.getCurrentLocaleCode();
				String[] address = getAddressNameFromCode(request.getProvinceCode(),
						null, null, locale);				
				if(address!=null && address.length>0) {
					city =address[0];
				}
			}		
			exportExcel(request.getOutputHeaders(),city,fromDate,toDate,lstAccountItemAlls,httpResponse);
		}
		else {
			try {
				BaseResponse error = ResponseDefine.responseBaseError(response.getErrorCode(), response.getMessage());
				MVEUtils.writeObjectResponse(httpResponse, HttpStatus.OK.value(), error);
			} catch (Exception e) {
				MVELoggingUtils.logMVEException(e);
			}
		}
	}
	
	private List<String> getListHeader(List<String> ltsOutputs, List<String> ltsResultCodes, String keyColumn) {
		List<String> lstHeader = new ArrayList<String>();
		lstHeader.add(MessagesUtils.getMessage("cell.export.index"));
		GetListCategoriesRequest request = new GetListCategoriesRequest();
		request.setCategoriesType(keyColumn);
		GetListCategoriesResponse listCategoriesResponse = categoriesClient.getListCategories(request);
		List<Category> lstCategories = listCategoriesResponse.getLstCategories();
		String locale = MVEUtils.getCurrentLocaleCode();
		for (String codeOutput : ltsOutputs) {
			for (Category category : lstCategories) {
				String code = category.getCode();
				if (codeOutput.equals(code)) {
					String header = category.getNames().get(locale);
					lstHeader.add(header);
					ltsResultCodes.add(code);
					break;
				}
			}
		}
		return lstHeader;
	}
	
	private void exportExcel(String outPutCodes,String value,String fromDate,String toDate,List<ListAccountItem> lstCallAlls,HttpServletResponse httpResponse) {
		String path = configValue.getPathTmp();		
		List<String> ltsOutputCodes = Arrays.asList(outPutCodes.split(","));
		List<String> ltsResultCodes = new ArrayList<String>();
		List<String> lstHeaderTitles = getListHeader(ltsOutputCodes, ltsResultCodes,CategoryType.ACCOUNT_COLUMN);		
		int rowBegin = NumberUtils.getIntValue(configValue.getRowBegin());
		String valueNo = String.format(MessagesUtils.getMessage("account.export.time.value"), fromDate,toDate);
		AccountExport callExport = new AccountExport(rowBegin,path,value,valueNo, lstHeaderTitles, ltsResultCodes, lstCallAlls);
		InfoExport infoExport = callExport.getListDataTypes();
		callExport.exportExcel(infoExport, httpResponse);
	}
	
	private String replaceReasonContent(String input) {
		String output = new String(input);
		output = output.replaceAll("<b>", "");
		output = output.replaceAll("</b>", "");
		output = output.replaceAll("<br>", "\n");
		String[] replaceAccounts = null;
		if(configValue.getReplaceResonAccount() != null) {
			replaceAccounts = configValue.getReplaceResonAccount().split(",");
			if(replaceAccounts != null && replaceAccounts.length > 0) {
				for (String accReplace : replaceAccounts) {
					output = output.replaceAll(" - " + accReplace.toUpperCase() + "\n", " : ");
				}
			}
		}
		return output;
	}

	@Override
	public BaseResponse searchListAccount(long currentUser, SysSearchListAccountRequest request) {
		List<BigInteger> eIds = null;
		List<EnterpriseBasicInfor> lstEnterprise = null;
		List<String> provincesLimit = getProvinceLimitsByUser(currentUser);
		request.setProvinceLimits(provincesLimit);
		SearchEnterpriseResp response = searchEnterprise(request);
		if (response != null) {
			eIds = response.getRs();
			lstEnterprise  = response.getLstEnterprise();
		}
		List<BigInteger> rIds = null;
		PagingObject<ListAccountItem> rsQuery;
		if (request.getRole() == 0) {
			// Search all roles
			Optional<MVEUser> rs = userRepository.findById(MVEUtils.convertLongToBigInteger(currentUser));
			if (!rs.isPresent()) {
				return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
			}
			MVEUser user = rs.get();
			if (user.getIsDelete() == 1) {
				return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.notallow"));
			}
			rsQuery = userRepositoryCustom.searchListAllAccountForSysAdmin(request, eIds, user.getIsSystem() != 1);
		} else if (request.getRole() == RoleDefine.SystemRole.ENTERPRISE_MEMBER.getId()) {
			rsQuery = userRepositoryCustom.searchListAllEnterpriseMember(request, eIds);
		} else {
			rIds = new ArrayList<BigInteger>();
			rIds.add(MVEUtils.convertLongToBigInteger(request.getRole()));
			rsQuery = userRepositoryCustom.searchListAccountForSysAdmin(request, eIds, rIds);
		}
		SysSearchListAccountResponse reponse = new SysSearchListAccountResponse();
		List<ListAccountItem> lstAccountItem = rsQuery.getListData();
		if(lstEnterprise == null) {
			// Lay thong tin doanh nghiep trong truong hop khong tim kiem theo doanh nghiep
			List<Long> eids = (lstAccountItem).stream()
				    .map(item -> item.getBusinessId())
				    .collect(Collectors.toList());
			lstEnterprise = searchEnterpriseByIds(eids);
		}
		
		for (ListAccountItem accountItem : lstAccountItem) {
			long businessId=accountItem.getBusinessId();				
			EnterpriseBasicInfor entry = lstEnterprise.stream()
					.filter(entrySearch -> businessId==entrySearch.getEnterpriseId()).findAny().orElse(null);
			accountItem.setEnterpriseName(entry != null ? entry.getEnterpriseName() : "");
			if(request instanceof SysExportExcelListAccountRequest) {
				String reason = accountItem.getRejectReason();
				if(!StringUtility.isNullOrEmpty(reason)){
					reason = replaceReasonContent(reason);
					accountItem.setRejectReason(reason);
				}
				accountItem.setStatusText(getStatusText(accountItem.getStatus()));
				if(entry != null) {
					updateItemEnterprise(entry, accountItem.getBusinessId(), accountItem);					
				}
			}
		}
		reponse.setListData(rsQuery.getListData());
		reponse.setCurrentPage(rsQuery.getCurrentPage());
		reponse.setTotalRow(rsQuery.getTotalRow());
		reponse.setErrorCode(ErrorDefine.OK);
		return reponse;
	}
	
	public void updateItemEnterprise(EnterpriseBasicInfor entry, long businessId,ListAccountItem listAccountItem ) {
		listAccountItem.setAddress(entry.getAddress());
		listAccountItem.setTaxNo(entry.getTaxNo());
		listAccountItem.setIdNo(entry.getDocumentNo());
		listAccountItem.setReferrerEmail(entry.getReferrerEmail());
		
		String locale = MVEUtils.getCurrentLocaleCode();
		String[] address = getAddressNameFromCode(entry.getProvinceCode(),
				entry.getDistrictCode(), entry.getTownCode(), locale);
		
		listAccountItem.setProvinceCode(address[0]);
		listAccountItem.setDistrictCode(address[1]);
		listAccountItem.setTownCode(address[2]);
		BigInteger enterpriseId = new BigInteger(String.valueOf(businessId));
		List<EnterpriseServiceItem> lstServices = getListServiceByEnterprise(enterpriseId);
		listAccountItem.setServiceName(getListServiceName(lstServices));
	}
	
	
	private String getStatusText(int status) {
		if (status == -1) {
			return MessagesUtils.getMessage("account.status.lock");
		} else if (status == 3){
			return MessagesUtils.getMessage("account.status.waitingForApproval");
		} else if (status == 4){
			return MessagesUtils.getMessage("account.status.denied");
		} else {
			return MessagesUtils.getMessage("account.status.active");
		}		
	}
	
	private String getListServiceName(List<EnterpriseServiceItem> lstServices) {
		StringBuilder sb = new StringBuilder();
		if (lstServices != null) {
			//Set<String> sIdNos = new LinkedHashSet<String>(lstIdNos);  
			for (EnterpriseServiceItem enterpriseServiceItem : lstServices) {
				if(!StringUtility.isNullOrEmpty(enterpriseServiceItem.getName())) {
					sb.append(enterpriseServiceItem.getName());
					sb.append("; ");
				}
			}
			int n = sb.length();
			if (n > 0) {
				String serviceName = sb.substring(0, n - 2);
				return serviceName;
			}
		}
		return "";
	}
	
	private List<String> getProvinceLimitsByUser(long userId){
		List<String> provincesLimit = null;
		List<AccountProvinceLimit> accountProvinceLimits = 
				provinceLimitRepository.findProvinceByAccount(MVEUtils.convertLongToBigInteger(userId));
		if(accountProvinceLimits != null && !accountProvinceLimits.isEmpty()) {
			provincesLimit = accountProvinceLimits.stream().map(item -> item.getProvinceCode()).collect(Collectors.toList());
		}
		return provincesLimit;
	}
	
	protected String[] getAddressNameFromCode(String provinceCode, String districtCode, String townCode, String lang) {
        String[] rs = new String[] { provinceCode, districtCode, townCode };
        GetDetailAddressRequest request = new GetDetailAddressRequest();
        request.setProvinceCode(provinceCode);
        request.setDistrictCode(districtCode);
        request.setTownCode(townCode);
        request.setLangCode(lang);
        GetDetailAddressResponse response = categoriesClient.getDetailAddress(request);
        if (response.getErrorCode() == ErrorDefine.OK) {
        	rs = new String[] { response.getProvinceName(), response.getDistrictName(), response.getTownName() };
        }
        return rs;
    }

	@Override
	public BaseResponse getAccountInfor(String authToken, AdminViewAccountRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		SysAdminViewAccountResponse reponse = new SysAdminViewAccountResponse();
		AdminAccountInformation accountInfor = getAccountInforFromMVEUser(authToken, user);
		accountInfor.setRoles(getRoleCodesByUser(user.getId()));
		reponse.setAccountInfor(accountInfor);
		reponse.setLstService(getListServiceByEnterprise(user.getBusinessId()));
		reponse.setEnterpriseInfor(getEnterpriseInfor(user.getBusinessId()));
		reponse.setRejectReson(user.getRejectReason());
		reponse.setApproveInfor(getApproveInfor(user));
		reponse.setErrorCode(ErrorDefine.OK);
		return reponse;
	}

	@Override
	public BaseResponse getListRoleByUser(AdminGetListRoleByUserRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
		List<Long> roleIds = new ArrayList<Long>();
		if (userRoles != null && !userRoles.isEmpty()) {
			for (UserRole userRole : userRoles) {
				roleIds.add(MVEUtils.convertIDValueToLong(userRole.getRoleId()));
			}
		}
		AdminGetListRoleByUserResponse response = new AdminGetListRoleByUserResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setRoles(roleIds);
		return response;
	}

	@Override
	public BaseResponse getDetailRolesByUser(String langKey, AdminGetListRoleByUserRequest request) {
		MVEUser user = userRepository.findByUserId(MVEUtils.convertLongToBigInteger(request.getUserId()));
		if (user == null) {
			return ResponseDefine.responseInvalidError(MessagesUtils.getMessage("message.error.deletedorinvalid"));
		}
		List<BigInteger> roleIds = null, perIds = null;
		List<RoleItem> lstRoles = null;
		List<PermissionItem> lstPers = null;
		List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
		if (userRoles != null && !userRoles.isEmpty()) {
			roleIds = userRoles.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
		}
		if (roleIds != null && !roleIds.isEmpty()) {
			lstRoles = roleRepository.getRoleByIDS(roleIds);
			List<RolePermission> rolePers = rolePermissionRepository.findRolePermissionByRoles(roleIds);
			if (rolePers != null && !rolePers.isEmpty()) {
				perIds = rolePers.stream().map(e -> e.getPermissionId()).collect(Collectors.toList());
			}
			lstPers = permissionRepository.getPermissionByIds(langKey, perIds);
		}
		AdminGetDetailRolesByUserResponse response = new AdminGetDetailRolesByUserResponse();
		response.setErrorCode(ErrorDefine.OK);
		response.setRoles(lstRoles);
		response.setPermissions(lstPers);
		return response;
	}

	@Override
	public BaseResponse getListProvinceByAccountManagement(long userId) {
		List<String> provincesLimit = getProvinceLimitsByUser(userId);
		GetListProvinceResponse provincesResponse = categoriesClient.getListOnlyProvince();
		if(provincesLimit != null && !provincesLimit.isEmpty()) {
			List<AddressItem> lstProvince = 
					provincesResponse.getLstProvince().stream().filter(item -> provincesLimit.contains(item.getCode()))
					.collect(Collectors.toList());
			provincesResponse.setLstProvince(lstProvince);
		}
		return provincesResponse;
	}

}
