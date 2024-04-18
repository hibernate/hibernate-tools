package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.Session;
import org.hibernate.tool.orm.jbt.api.SessionWrapper;

public class SessionWrapperFactory {

	public static SessionWrapper createSessionWrapper(Session wrappedSession) {
		return new SessionWrapper() {
			@Override public Session getWrappedObject() { return wrappedSession; }
		};
	}

}
