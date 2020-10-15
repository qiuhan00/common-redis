package com.cfang.config;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cfang 2020/10/15 10:41
 * @description
 */
@Data
@NoArgsConstructor
public class LockConf {

	private List<String> keys;
	private long leaseTime = -1;
	private long tryWaitTime;

	public String getKey(){
		if(CollectionUtils.isEmpty(keys)){
			return null;
		}
		return keys.get(0);
	}

	public void setKey(String key){
		if(null == keys){
			keys = Lists.newArrayList();
		}
		keys.add(key);
	}
}
