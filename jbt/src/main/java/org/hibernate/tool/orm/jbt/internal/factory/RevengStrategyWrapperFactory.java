package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.api.RevengStrategyWrapper;

public class RevengStrategyWrapperFactory {

	public static RevengStrategyWrapper createRevengStrategyWrapper(RevengStrategy wrappedRevengStrategy) {
		return new RevengStrategyWrapper() {
			@Override public RevengStrategy getWrappedObject() { return wrappedRevengStrategy; }
		};
	}

}
