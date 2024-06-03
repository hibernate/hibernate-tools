package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.api.SessionWrapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SessionWrapperFactory {

	public static SessionWrapper createSessionWrapper(Session wrappedSession) {
		return new SessionWrapperImpl(wrappedSession);
	}
	
	private static class SessionWrapperImpl implements SessionWrapper {
		
		private Session session = null;
		
		private SessionWrapperImpl(Session session) {
			this.session = session;
		}
		
		@Override 
		public Session getWrappedObject() { 
			return session; 
		}
		
		@Override 
		public String getEntityName(Object o) { 
			return session.getEntityName(o); 
		}

		@Override 
		public SessionFactory getSessionFactory() { 
			return session.getSessionFactory(); 
		}

		@Override 
		public Query<?> createQuery(String s) { 
			return session.createQuery(s); 
		}

		@Override 
		public boolean isOpen() { 
			return session.isOpen(); 
		}

		@Override 
		public void close() { 
			session.close(); 
		}

		@Override 
		public boolean contains(Object o) { 
			boolean result = false;
			try {
				result = session.contains(o);
			} catch (IllegalArgumentException e) {
				String message = e.getMessage();
				if (!(message.startsWith("Class '") && message.endsWith("' is not an entity class"))) {
					throw e;
				}
			}
			return result;
		}

		@Override 
		public jakarta.persistence.Query createCriteria(Class<?> c) {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery(c);
			Root root = criteriaQuery.from(c);
			criteriaQuery.select(root);
			return ((Session)getWrappedObject()).createQuery(criteriaQuery);
		}

	}

}
