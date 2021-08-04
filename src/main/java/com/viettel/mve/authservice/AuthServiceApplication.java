package com.viettel.mve.authservice;

import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.viettel.mve.authservice.common.ConfigValue;
import com.viettel.mve.common.caching.RedisCaching;
import com.viettel.mve.common.caching.config.RedisConfig;
import com.viettel.mve.common.logging.MVELoggingUtils;
import com.viettel.mve.common.spring.BaseMVEApplication;
import com.viettel.mve.common.spring.MultipartSupportConfig;
import com.viettel.mve.common.utils.MVEUtils;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = MultipartSupportConfig.class)
@ComponentScan(basePackageClasses = {AuthServiceApplication.class, RedisConfig.class})
public class AuthServiceApplication extends BaseMVEApplication {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private ConfigValue configValue;
	
	@Autowired
	private RedisConfig redisConfig;

	public static void main(String[] args) {
		System.setProperty("mve.log.hostname", MVEUtils.getCurrentHostName());
		MVELoggingUtils.setLogger(LoggerFactory.getLogger("auth_service"));
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RedisCaching redisCaching() {
		return new RedisCaching(redissonClient, redisConfig);
	}

	@Override
	public String defaultLanguge() {
		return configValue.getDefaultLanguage();
	}

}
