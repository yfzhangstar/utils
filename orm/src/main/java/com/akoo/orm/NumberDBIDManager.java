package com.akoo.orm;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.akoo.common.util.GameUtil;
import com.akoo.common.util.SpringBeanFactory;

public class NumberDBIDManager {
	private static final Map<Class<? extends DBVO>,AtomicInteger> baseDAOIntegerPKMapping = GameUtil.createMap();
	private static final Map<Class<? extends DBVO>,AtomicLong> baseDAOLongPKMapping  = GameUtil.createMap();
	
	static{
		Collection<BaseDAO> values =  SpringBeanFactory.getBeanByType(BaseDAO.class).values();
		for (BaseDAO<?> baseDAO : values) {
			if(baseDAO.getIsNumberPK()){
				Class<? extends DBVO> voClass = baseDAO.getVoClass();
				Number maxKey = baseDAO.getMaxKey();
				switch(baseDAO.getNumPKType()){
				case DBObj.KEY_TYPE_INTEGER:
				case DBObj.KEY_TYPE_SHORT:
					baseDAOIntegerPKMapping.put(voClass,new AtomicInteger(maxKey.intValue()));
					break;
				case DBObj.KEY_TYPE_LONG:
					baseDAOLongPKMapping.put(voClass, new AtomicLong(maxKey.longValue()));
					break;
				}
		
			}
		}
	}
	
	public static Number initDBID(Class<? extends DBVO> clazz){
		AtomicInteger atoInt = baseDAOIntegerPKMapping.get(clazz);
		if(atoInt != null)
			return atoInt.incrementAndGet();
		AtomicLong atoLong = baseDAOLongPKMapping.get(clazz);
		if(atoLong != null)
			return atoLong.incrementAndGet();
		return null;
	}

	
	
	
	
}
