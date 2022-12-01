package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class SessionFactoryWrapper extends SessionFactoryDelegatingImpl {
	
	public SessionFactoryWrapper(SessionFactoryImplementor delegate) {
		super(delegate);
	}
	
}
