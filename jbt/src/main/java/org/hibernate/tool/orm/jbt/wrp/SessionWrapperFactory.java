package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Session;

public class SessionWrapperFactory {
	
	public static Session createSessionWrapper(Session session) {
		return (Session)Proxy.newProxyInstance(
				SessionWrapperFactory.class.getClassLoader(), 
				new Class[] { Session.class }, 
				new SessionWrapperInvocationHandler(session));
	}
	
	private static class SessionWrapperInvocationHandler implements InvocationHandler {
		
		private Session session = null;
		
		private SessionWrapperInvocationHandler(Session session) {
			this.session = session;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(session, args);
		}
		
	}

}
