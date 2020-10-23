package com.cfang.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @author cfang 2020/10/23 13:42
 * @description
 */
@Slf4j
public class NotifyExceptionCacheErrorHandler implements CacheErrorHandler {

	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		log.warn("handleCacheGetError,error:{}, key:{}", exception.getMessage(), key);
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		log.warn("handleCachePutError,error:{}, key:{}, value:{}", exception.getMessage(), key, JSON.toJSONString(value));
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		log.warn("handleCacheEvictError,error:{}, key:{}", exception.getMessage(), key);
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		log.warn("handleCacheClearError,error:{}", exception.getMessage());
	}
}
