package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;

public class SessionWrapperFactory {
	
	public static SessionImplementor createSessionWrapper(
			SessionFactoryImplementor sessionFactory, 
			SessionImplementor session) {
		return (SessionImplementor)Proxy.newProxyInstance(
				SessionWrapperFactory.class.getClassLoader(), 
				new Class[] { SessionImplementor.class }, 
				new SessionWrapperInvocationHandler(sessionFactory, session));
	}
	
	private static class SessionWrapperInvocationHandler implements InvocationHandler {
		
		private SessionImplementor session = null;
		private SessionFactoryImplementor sessionFactory = null;
		
		private SessionWrapperInvocationHandler(
				SessionFactoryImplementor sessionFactory, 
				SessionImplementor session) {
			this.session = session;
			this.sessionFactory = sessionFactory;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("getSessionFactory".equals(method.getName())) {
				return sessionFactory;
			}
			return method.invoke(session, args);
		}
		
	}

}
