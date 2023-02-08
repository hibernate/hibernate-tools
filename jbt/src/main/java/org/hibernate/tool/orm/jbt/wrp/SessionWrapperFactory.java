package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SessionWrapperFactory {
	
	public static SessionImplementor createSessionWrapper(
			SessionFactoryImplementor sessionFactory, 
			SessionImplementor session) {
		return (SessionImplementor)Proxy.newProxyInstance(
				SessionWrapperFactory.class.getClassLoader(), 
				new Class[] { SessionImplementorExtension.class }, 
				new SessionWrapperInvocationHandler(sessionFactory, session));
	}
	
	static interface SessionImplementorExtension extends SessionImplementor {
		Query createCriteria(Class<?> persistentClass);
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
			} else if ("contains".equals(method.getName())) {
				return contains(args[0]);
			} else if ("createCriteria".equals(method.getName())) {
				return createCriteria((Class<?>)args[0]);
			}
			return method.invoke(session, args);
		}
		
		private boolean contains(Object o) {
			boolean result = false;
			try {
				result = session.contains(o);
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().startsWith("Not an entity [")) {
					throw e;
				}
			}
			return result;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private Query createCriteria(Class<?> persistentClass) {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(persistentClass);
			Root root = criteriaQuery.from(persistentClass);
			criteriaQuery.select(root);
			return CriteriaWrapperFactory.createCriteriaWrapper(session.createQuery(criteriaQuery));
		}
		
	}

}
