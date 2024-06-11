package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.wrp.ClassMetadataWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.CollectionMetadataWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.SessionFactoryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.SessionWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class SessionFactoryWrapperFactory {

	public static SessionFactoryWrapper createSessionFactoryWrapper(SessionFactory sessionFactory) {
		return new SessionFactoryWrapperImpl(sessionFactory);
	}
	
	private static class SessionFactoryWrapperImpl 
			extends AbstractWrapper
			implements SessionFactoryWrapper {
		
		private SessionFactory sessionFactory = null;
	
		private SessionFactoryWrapperImpl(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}
		
		@Override 
		public SessionFactory getWrappedObject() { 
			return sessionFactory; 
		}
		
		@Override 
		public void close() { 
			sessionFactory.close(); 
		}

		@Override 
		public Map<String, ClassMetadataWrapper> getAllClassMetadata() {
			Map<String, EntityPersister> origin = ((SessionFactoryImplementor)sessionFactory).getMetamodel().entityPersisters();
			Map<String, ClassMetadataWrapper> result = new HashMap<String, ClassMetadataWrapper>(origin.size());
			for (String key : origin.keySet()) {
				result.put(key, ClassMetadataWrapperFactory.createClassMetadataWrapper(origin.get(key)));
			}
			return result;
		}

		@Override 
		public Map<String, CollectionMetadataWrapper> getAllCollectionMetadata() {
			Map<String, CollectionPersister> origin = ((SessionFactoryImplementor)sessionFactory).getMetamodel().collectionPersisters();
			Map<String, CollectionMetadataWrapper> result = new HashMap<String, CollectionMetadataWrapper>(origin.size());
			for (String key : origin.keySet()) {
				result.put(key, CollectionMetadataWrapperFactory.createCollectionMetadataWrapper(origin.get(key)));
			}
			return result;
		}

		@Override 
		public SessionWrapper openSession() { 
			Session s = sessionFactory.openSession();
			return s == null ? null : SessionWrapperFactory.createSessionWrapper(s); 
		}

		@Override 
		public ClassMetadataWrapper getClassMetadata(String s) { 
			return getAllClassMetadata().get(s); 
		}

		@Override 
		public ClassMetadataWrapper getClassMetadata(Class<?> c) { 
			return getAllClassMetadata().get(c.getName()); 
		}

		@Override 
		public CollectionMetadataWrapper getCollectionMetadata(String s) { 
			return getAllCollectionMetadata().get(s); 
		}

	}

}
