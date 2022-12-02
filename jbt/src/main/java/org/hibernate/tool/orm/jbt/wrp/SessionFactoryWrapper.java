package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class SessionFactoryWrapper extends SessionFactoryDelegatingImpl {
	
	public SessionFactoryWrapper(SessionFactory delegate) {
		super((SessionFactoryImplementor)delegate);
	}
	
}
