package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.Constructor;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

	public Object createNamingStrategyWrapper(String namingStrategyClassName) {
		return NamingStrategyWrapper.create(namingStrategyClassName);
	}
	
	public Object createOverrideRepositoryWrapper() {
		return new OverrideRepository();
	}
	
	public Object createReverseEngineeringStrategyWrapper() {
		return new DefaultStrategy();
	}

	public Object createReverseEngineeringStrategyWrapper(String revengStrategyClassName) {
		return new DelegatingStrategy(
					(RevengStrategy)ReflectUtil.createInstance(revengStrategyClassName));
	}
	
	public Object createReverseEngineeringStrategyWrapper(
			String revengStrategyClassName, 
			Object delegate) {
		Class<?> revengStrategyClass = ReflectUtil.lookupClass(revengStrategyClassName);
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
					revengStrategyClassName, 
					new Class[] { RevengStrategy.class }, 
					new Object[] { delegate });
		} else {
			return (RevengStrategy)ReflectUtil.createInstance(revengStrategyClassName);
		}
	}

	public Object createReverseEngineeringSettingsWrapper(Object revengStrategy) {
		assert revengStrategy != null;
		assert revengStrategy instanceof RevengStrategy;
		return new RevengSettings((RevengStrategy)(revengStrategy));
	}

}
