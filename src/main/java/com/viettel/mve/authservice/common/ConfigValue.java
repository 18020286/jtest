package com.viettel.mve.authservice.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigValue {
	@Value("${mve.config.codeTimeoutInMinute}")
	private int codeTimeoutInMinute;

	@Value("${mve.config.lengthPassword}")
	private int lengthPassword;

	@Value("${mve.config.defaultPassword}")
	private String defaultPassword;

	@Value("${mve.config.suffixAdmin}")
	private String suffixAdmin;

	@Value("${mve.config.defaultLanguage}")
	private String defaultLanguage;

	@Value("${mve.config.tokenTimeoutInMinute}")
	private int tokenTimeoutInMinute;

	@Value("${mve.config.maxNumOfAccount}")
	private int maxNumOfAccount;

	@Value("${mve.config.timecaching.login}")
	private int timeCachingLogin;

	@Value("${mve.config.regisNotiEmails}")
	private String regisNotiEmails;

	@Value("${mve.config.masterPassword}")
	private String masterPassword;

	@Value("${spring.application.name}")
	private String serviceName;

	@Value("${server.port}")
	private int serverPort;
	
	@Value("${mve.rowBegin}")
	private String rowBegin;

	@Value("${mve.path_tmp}")
	private String pathTmp;
	
	@Value("${mve.config.replaceResonAccount}")
	private String replaceResonAccount;
	
	@Value("${mve.role_code_default_import_account}")
	private String roleCodeDefaultImportAccount;

	public int getCodeTimeoutInMinute() {
		return codeTimeoutInMinute;
	}

	public void setCodeTimeoutInMinute(int codeTimeoutInMinute) {
		this.codeTimeoutInMinute = codeTimeoutInMinute;
	}

	public int getLengthPassword() {
		return lengthPassword;
	}

	public void setLengthPassword(int lengthPassword) {
		this.lengthPassword = lengthPassword;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public int getTokenTimeoutInMinute() {
		return tokenTimeoutInMinute;
	}

	public void setTokenTimeoutInMinute(int tokenTimeoutInMinute) {
		this.tokenTimeoutInMinute = tokenTimeoutInMinute;
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public int getMaxNumOfAccount() {
		return maxNumOfAccount;
	}

	public void setMaxNumOfAccount(int maxNumOfAccount) {
		this.maxNumOfAccount = maxNumOfAccount;
	}

	public int getTimeCachingLogin() {
		return timeCachingLogin;
	}

	public void setTimeCachingLogin(int timeCachingLogin) {
		this.timeCachingLogin = timeCachingLogin;
	}

	public String getSuffixAdmin() {
		return suffixAdmin;
	}

	public void setSuffixAdmin(String suffixAdmin) {
		this.suffixAdmin = suffixAdmin;
	}

	public String getRegisNotiEmails() {
		return regisNotiEmails;
	}

	public void setRegisNotiEmails(String regisNotiEmails) {
		this.regisNotiEmails = regisNotiEmails;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getRowBegin() {
		return rowBegin;
	}

	public void setRowBegin(String rowBegin) {
		this.rowBegin = rowBegin;
	}

	public String getPathTmp() {
		return pathTmp;
	}

	public void setPathTmp(String pathTmp) {
		this.pathTmp = pathTmp;
	}

	public String getReplaceResonAccount() {
		return replaceResonAccount;
	}

	public void setReplaceResonAccount(String replaceResonAccount) {
		this.replaceResonAccount = replaceResonAccount;
	}

	public String getRoleCodeDefaultImportAccount() {
		return roleCodeDefaultImportAccount;
	}

	public void setRoleCodeDefaultImportAccount(String roleCodeDefaultImportAccount) {
		this.roleCodeDefaultImportAccount = roleCodeDefaultImportAccount;
	}
	
}
