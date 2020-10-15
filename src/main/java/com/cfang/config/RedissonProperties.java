package com.cfang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cfang 2020/10/15 14:58
 * @description
 */
@ConfigurationProperties(prefix = "spring.redis.redisson")
@Data
public class RedissonProperties {
	private int timeout = 3000;
	private String password;
	private String clientName;
	private String address;
	private int connectionMinimumIdleSize = 10;
	private int connectionPoolSize = 64;
	private int database = 0;
	private String[] sentinelAddresses;
	private int slaveConnectionPoolSize = 250;
	private int masterConnectionPoolSize = 250;
	private String masterName;
}
