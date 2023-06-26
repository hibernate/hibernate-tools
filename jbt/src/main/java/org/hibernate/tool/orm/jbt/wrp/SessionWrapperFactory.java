package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.tool.orm.jbt.wrp.QueryWrapperFactory.QueryWrapper;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SessionWrapperFactory {
	
	public static SessionWrapper createSessionWrapper(
			SessionFactoryImplementor sessionFactory, 
			SessionImplementor session) {
		return (SessionWrapper)Proxy.newProxyInstance(
				SessionWrapperFactory.class.getClassLoader(), 
				new Class[] { SessionWrapper.class }, 
				new SessionWrapperInvocationHandler(
						new SessionWrapperImpl(sessionFactory, session)));
	}
	
	static interface SessionWrapper extends SessionImplementor, Wrapper {
		Query createCriteria(Class<?> persistentClass);
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
	
	@SuppressWarnings("serial")
	private static class SessionWrapperImpl extends SessionDelegatorBaseImpl implements SessionWrapper {
		
		private SessionFactoryImplementor sessionFactory;

		public SessionWrapperImpl(SessionFactoryImplementor sessionFactory, SessionImplementor delegate) {
			super(delegate);
			this.sessionFactory = sessionFactory;
		}
		
		@Override
		public SessionFactoryImplementor getSessionFactory() {
			return sessionFactory;
		}
		
		@Override
		public SessionImplementor getWrappedObject() {
			return delegate();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Query createCriteria(Class<?> persistentClass) {
			CriteriaBuilder criteriaBuilder = delegate().getCriteriaBuilder();
			CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(persistentClass);
			Root root = criteriaQuery.from(persistentClass);
			criteriaQuery.select(root);
			return CriteriaWrapperFactory.createCriteriaWrapper(delegate().createQuery(criteriaQuery));
		}
		
		@Override
		public QueryWrapper<?> createQuery(String queryString) {
			return QueryWrapperFactory.createQueryWrapper(delegate().createQuery(queryString, null));
		}
		
		@Override
		public boolean contains(Object o) {
			boolean result = false;
			try {
				result = delegate().contains(o);
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().startsWith("Not an entity [")) {
					throw e;
				}
			}
			return result;
		}
		
	}

}
