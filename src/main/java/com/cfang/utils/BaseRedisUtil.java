package com.cfang.utils;

import com.alibaba.fastjson.JSON;
import com.cfang.config.LockConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author cfang 2020/10/15 13:57
 * @description
 */
@Slf4j
public abstract class BaseRedisUtil<K extends Serializable, HK extends Serializable, V> {

	@Autowired
	RedisTemplate<K, V> redisTemplate;
	@Autowired
	RedissonUtil redissonUtil;

	public void add(K key, V value) {
		this.redisTemplate.opsForValue().set(key, value);
	}

	public boolean set(K key, V value) {
		boolean result = false;
		try {
			redisTemplate.opsForValue().set(key, value);
			result = true;
		} catch (Exception e) {
			log.warn(String.format("设置键值 %s:%s 在异常，msg:%s", key, JSON.toJSONString(value), e.getMessage()));
		}
		return result;
	}

	public boolean set(K key, V value, long ttl){
		boolean result = false;
		try {
			if(ttl > 0){
				redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
			}else{
				set(key, value);
			}
			result = true;
		} catch (Exception e) {
			log.warn(String.format("设置键值 %s:%s,ttl:%s 在异常，msg:%s", key,JSON.toJSONString(value), ttl, e.getMessage()));
		}
		return result;
	}

	public V get(K key){
		return null == key ? null : redisTemplate.opsForValue().get(key);
	}

	public boolean del(K ... key){
		boolean result = false;
		if(null != key && key.length > 0){
			if(key.length==1){
				result = redisTemplate.delete(key[0]);
			}else{
				Long ret = redisTemplate.delete(Arrays.asList(key));
				if(ret == Arrays.asList(key).size()) {
					result = true;
				}
			}
		}
		return result;
	}

	public boolean expire(K key, long ttl) {
		boolean result = false;
		try {
			if(ttl > 0) {
				redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
			}
			result = true;
		} catch (Exception e) {
			log.warn(String.format("键 %s 设置有效期 %s 异常，msg:%s", key, ttl, e.getMessage()));
		}
		return result;
	}

	public long getExpire(K key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	public boolean hasKey(K key) {
		boolean result = false;
		try {
			result = redisTemplate.hasKey(key);
		} catch (Exception e) {
			log.warn(String.format("查询键 %s 是否存在异常，msg:%s", key, e.getMessage()));
		}
		return result;
	}

	public long incr(K key, long delta){
		if(delta < 0){
			throw new RuntimeException("递增因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

	public long decr(K key, long delta){
		if(delta < 0){
			throw new RuntimeException("递减因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, -delta);
	}

	public Set<K> patternKeys(K pattern) {
		return this.redisTemplate.opsForList().getOperations().keys(pattern);
	}

	public void add2Set(K key, V value) {
//		this.redisTemplate.opsForSet().add(key, value);
	}

	public void delete4Set(K key, V value) {
		this.redisTemplate.opsForSet().remove(key, new Object[]{value});
	}

	public void delete4Set(K key, List<V> values) {
		this.redisTemplate.opsForSet().remove(key, new Object[]{values});
	}

	public Set<V> getMembers4Set(K key) {
		return this.redisTemplate.opsForSet().members(key);
	}

	public Long getSize4Set(K key) {
		return this.redisTemplate.opsForSet().size(key);
	}

	public List<V> randmember(K key, long count) {
		return this.redisTemplate.opsForSet().randomMembers(key, count);
	}

	public void add2Hash(K key, HK hKey, V value) {
		this.redisTemplate.opsForHash().put(key, hKey, value);
	}

	public Object getFromHash(K key, Object hKey) {
		return this.redisTemplate.opsForHash().get(key, hKey);
	}

	public Map<Object,Object> getMembers4Hash(K key) {
		return this.redisTemplate.opsForHash().entries(key);
	}

	public long getSize4List(K key) {
		return this.redisTemplate.opsForList().size(key).longValue();
	}

	public void repalce4List(K key, long index, V value) {
		this.redisTemplate.opsForList().set(key, index, value);
	}

	public void delete4List(K key, long count, V values) {
		this.redisTemplate.opsForList().remove(key, count, values);
	}

	public void add2List(K key, V value) {
		this.redisTemplate.opsForList().rightPush(key, value);
	}

	public void addLeft2List(K key, V value) {
		this.redisTemplate.opsForList().leftPush(key, value);
	}

	public List<V> getMembers4List(K key) {
		long size = getSize4List(key);
		if (size < 1L) {
			return null;
		}
		return this.redisTemplate.opsForList().range(key, 0L, size);
	}

	public Lock lock(LockConf lockConf) {
		return this.redissonUtil.lock(lockConf);
	}

	public Lock lock(LockConf lockConf, boolean fair) {
		return this.redissonUtil.lock(lockConf, fair);
	}

	public Lock tryLock(LockConf lockConf) {
		return this.redissonUtil.tryLock(lockConf);
	}

	public Lock tryLock(LockConf lockConf, boolean fair) {
		return this.redissonUtil.tryLock(lockConf, fair);
	}

	public void unlock(Lock lock) {
		this.redissonUtil.unLock(lock);
	}

	public void unlock(String lockKey) {
		this.redissonUtil.unLock(lockKey);
	}
}
