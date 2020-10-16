package com.cfang.utils;

import com.cfang.config.LockConf;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author cfang 2020/10/15 10:33
 * @description
 */
@Slf4j
public class RedissonUtil {

	@Autowired
	private RedissonClient redissonClient;

	public Lock lock(LockConf conf){
		return this.lock(conf, false);
	}

	public Lock lock(LockConf conf, boolean fair){
		Lock lock = getLock(conf, fair);
		if(lock instanceof RLock){
			((RLock) lock).lock(conf.getLeaseTime(), TimeUnit.SECONDS);
		}
		if(lock instanceof RedissonMultiLock){
			((RedissonMultiLock) lock).lock(conf.getLeaseTime(), TimeUnit.SECONDS);
		}
		return lock;
	}

	public Lock tryLock(LockConf conf){
		return this.tryLock(conf, false);
	}

	public Lock tryLock(LockConf conf, boolean fair){
		Lock lock = getLock(conf, fair);
		boolean canGetLock = false;
		try {
			if(lock instanceof RLock) {
				canGetLock = ((RLock) lock).tryLock(conf.getTryWaitTime(), conf.getLeaseTime(), TimeUnit.SECONDS);
			}
			if(lock instanceof RedissonMultiLock) {
				canGetLock = ((RedissonMultiLock) lock).tryLock(conf.getTryWaitTime(), conf.getLeaseTime(), TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
				log.error("尝试获取锁异常，msg:{}", e.getMessage());
		}
		if(canGetLock){
			return lock;
		}
		return null;
	}

	public void unLock(String key){
		Lock lock = redissonClient.getLock(key);
		unLock(lock);
	}

	public void unLock(Lock lock){
		lock.unlock();
	}


	private Lock getLock(LockConf conf, boolean fair){
		RLock[] rLocks = new RLock[conf.getKeys().size()];
		for (int i = 0; i < conf.getKeys().size(); i++) {
			String key = conf.getKeys().get(i);
			if(fair){
				rLocks[i] = redissonClient.getFairLock(key);
			}else{
				rLocks[i] = redissonClient.getLock(key);
			}
		}
		if(rLocks.length == 1){
			return rLocks[0];
		}
		RedissonMultiLock multiLock = new RedissonMultiLock(rLocks);
		return multiLock;
	}
}
