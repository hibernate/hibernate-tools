package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.api.NamingStrategyWrapper;

public class NamingStrategyWrapperFactory {

	public static NamingStrategyWrapper createNamingStrategyWrapper(NamingStrategy wrappedNamingStrategy) {
		return new NamingStrategyWrapper() {
			@Override public NamingStrategy getWrappedObject() { return wrappedNamingStrategy; }
		};
	}
	
}
