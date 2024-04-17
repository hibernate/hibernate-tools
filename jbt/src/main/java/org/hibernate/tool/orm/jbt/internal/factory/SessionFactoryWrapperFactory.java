package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.SessionFactory;
import org.hibernate.tool.orm.jbt.api.SessionFactoryWrapper;

public class SessionFactoryWrapperFactory {

	public static SessionFactoryWrapper createSessionFactoryWrapper(SessionFactory sessionFactory) {
		return new SessionFactoryWrapper() {
			@Override public SessionFactory getWrappedObject() { return sessionFactory; }
		};
	}

}
