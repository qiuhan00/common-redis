package com.cfang.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cfang 2020/10/15 15:25
 * @description
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(value = {RedissonProperties.class})
public class RedissonAutoConfiguration {

	@Autowired
	RedissonProperties redissonProperties;

	@Bean(name = "redissonClient")
	@ConditionalOnProperty(name = "spring.redis.redisson.address")
	public RedissonClient redissonSingle(){
		Config config = new Config();
		SingleServerConfig serverConfig = config.useSingleServer()
				.setAddress(this.redissonProperties.getAddress())
				.setTimeout(this.redissonProperties.getTimeout())
				.setConnectionPoolSize(this.redissonProperties.getConnectionPoolSize()).setConnectionMinimumIdleSize(this.redissonProperties.getConnectionMinimumIdleSize())
				.setDatabase(this.redissonProperties.getDatabase());
		if(StrUtil.isNotBlank(redissonProperties.getPassword())) {
			serverConfig.setPassword(this.redissonProperties.getPassword());
		}

		if (this.redissonProperties.getClientName() != null && this.redissonProperties.getClientName().length() > 0) {
			serverConfig.setClientName(this.redissonProperties.getClientName());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		config.setCodec((Codec)new JsonJacksonCodec(objectMapper));
		return Redisson.create(config);
	}

	@Bean(name = "redissonClient")
	@ConditionalOnMissingBean(name = "redissonClient")
	@ConditionalOnProperty(name = "spring.redis.redisson.master-name")
	public RedissonClient redissonSentinel(){
		Config config = new Config();
		SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redissonProperties.getSentinelAddresses())
				.setMasterName(redissonProperties.getMasterName())
				.setTimeout(redissonProperties.getTimeout())
				.setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
				.setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize());

		if(StrUtil.isNotBlank(redissonProperties.getPassword())) {
			serverConfig.setPassword(redissonProperties.getPassword());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		config.setCodec((Codec)new JsonJacksonCodec(objectMapper));
		return Redisson.create(config);
	}


}
