package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.api.OverrideRepositoryWrapper;

public class OverrideRepositoryWrapperFactory {

	public static OverrideRepositoryWrapper createOverrideRepositoryWrapper(OverrideRepository wrappedOverrideRepository) {
		return new OverrideRepositoryWrapper() {
			@Override public OverrideRepository getWrappedObject() { return wrappedOverrideRepository; }
		};
	}
	
}
