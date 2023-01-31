package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.engine.spi.SessionImplementor;

public class SessionWrapperFactory {
	
	public static SessionImplementor createSessionWrapper(SessionImplementor session) {
		return (SessionImplementor)Proxy.newProxyInstance(
				SessionWrapperFactory.class.getClassLoader(), 
				new Class[] { SessionImplementor.class }, 
				new SessionWrapperInvocationHandler(session));
	}
	
	private static class SessionWrapperInvocationHandler implements InvocationHandler {
		
		private SessionImplementor session = null;
		
		private SessionWrapperInvocationHandler(SessionImplementor session) {
			this.session = session;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(session, args);
		}
		
	}

}
