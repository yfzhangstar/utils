package com.akoo.common.util;

import java.util.concurrent.atomic.AtomicLong;

public class StrIDUtil {
	
	private String key;
	
	public StrIDUtil(String key){
		this.key = key;
	}
	
	private final AtomicLong counter = new AtomicLong(
			System.currentTimeMillis() * 1000L);

	public String getUUIDShort() {
		return key + Long.toString(counter.incrementAndGet(), 36);
	}
}
