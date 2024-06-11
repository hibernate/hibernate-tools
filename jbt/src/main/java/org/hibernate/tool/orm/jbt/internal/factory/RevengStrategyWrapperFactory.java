package org.hibernate.tool.orm.jbt.internal.factory;

import java.lang.reflect.Constructor;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.api.wrp.RevengSettingsWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class RevengStrategyWrapperFactory {

	public static RevengStrategyWrapper createRevengStrategyWrapper(Object...objects) {
		RevengStrategy wrappedRevengStrategy = null;
		if (objects.length == 0) {
			wrappedRevengStrategy = createDefaultStrategy();
		} else if (objects.length == 2) {
			wrappedRevengStrategy = createDelegatingStrategy((String)objects[0], (RevengStrategy)objects[1]);
		} else {
			throw new RuntimeException("RevengStrategyWrapperFactory#create has either 0 or 2 arguments");
		}
		return createRevengStrategyWrapper(wrappedRevengStrategy);
	}
	
	private static RevengStrategy createDefaultStrategy() {
		return new DefaultStrategy();
	}
	
	private static RevengStrategy createDelegatingStrategy(String strategyClassName, RevengStrategy delegate) {
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

	static RevengStrategyWrapper createRevengStrategyWrapper(RevengStrategy wrappedRevengStrategy) {
		return new RevengStrategyWrapperImpl(wrappedRevengStrategy);
	}
	
	private static class RevengStrategyWrapperImpl 
			extends AbstractWrapper
			implements RevengStrategyWrapper {
		
		private RevengStrategy revengStrategy = null;
		
		private RevengStrategyWrapperImpl(RevengStrategy revengStrategy) {
			this.revengStrategy = revengStrategy;
		}
		
		@Override 
		public RevengStrategy getWrappedObject() { 
			return revengStrategy; 
		}
		
		@Override
		public void setSettings(RevengSettingsWrapper revengSettings) { 
			revengStrategy.setSettings((RevengSettings)revengSettings.getWrappedObject()); 
		}
		
	}

}
