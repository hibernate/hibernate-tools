package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.Constructor;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class RevengStrategyWrapperFactory {
	
	public static Object create(Object...objects) {
		if (objects.length == 0) {
			return createDefault();
		} else if (objects.length == 2) {
			return create((String)objects[0], objects[1]);
		} else {
			throw new RuntimeException("RevengStrategyWrapperFactory#create has either 0 or 2 arguments");
		}
	}
	
	private static Object createDefault() {
		return new DefaultStrategy();
	}
	
	private static Object create(String strategyClassName, Object delegate) {
		Class<?> revengStrategyClass = ReflectUtil.lookupClass(strategyClassName);
		Constructor<?> constructor = null;
		for (Constructor<?> c : revengStrategyClass.getConstructors()) {
			if (c.getParameterCount() == 1 && 
					c.getParameterTypes()[0].isAssignableFrom(RevengStrategy.class)) {
				constructor = c;
				break;
			}
		}
		if (constructor != null) {
			return (RevengStrategy)ReflectUtil.createInstance(
					strategyClassName, 
					new Class[] { RevengStrategy.class }, 
					new Object[] { delegate });
		} else {
			return (RevengStrategy)ReflectUtil.createInstance(strategyClassName);
		}
	}

}
