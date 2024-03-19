package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Join;
import org.hibernate.tool.orm.jbt.api.JoinWrapper;

public class JoinWrapperFactory {

	public static JoinWrapper createJoinWrapper(Join wrappedJoin) {
		return new JoinWrapper() {
			@Override public Join getWrappedObject() { return wrappedJoin; }
		};
	}
	
}
