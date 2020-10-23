package com.cfang.config;

import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author cfang 2020/10/23 11:26
 * @description
 */
public class PrefixStringKeySerializer extends StringRedisSerializer {

	private String prefixKey;

	public PrefixStringKeySerializer(String prefixKey){
		super();
		this.prefixKey = prefixKey;
	}

	@Override
	public String deserialize(byte[] bytes) {
		 String key = super.deserialize(bytes);
		 int i = key.indexOf(prefixKey + ":");
		 if(i > 0){
		 	key = key.substring(i);
		 }
		 return key;
	}

	@Override
	public byte[] serialize(String string) {
		string = prefixKey + ":" + string;
		return super.serialize(string);
	}
}
