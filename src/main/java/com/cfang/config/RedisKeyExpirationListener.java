package com.cfang.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author cfang 2020/10/30 9:58
 * @description
 */
@Slf4j
public abstract class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

	public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString(); //失效key
		log.info("过期key={}", expiredKey);
		//TO DO... 过期key的业务处理

	}
}
