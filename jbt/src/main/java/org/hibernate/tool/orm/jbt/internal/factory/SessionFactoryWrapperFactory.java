package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.SessionFactoryWrapper;
import org.hibernate.tool.orm.jbt.wrp.CollectionPersisterWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory;

public class SessionFactoryWrapperFactory {

	public static SessionFactoryWrapper createSessionFactoryWrapper(SessionFactory sessionFactory) {
		return new SessionFactoryWrapperImpl(sessionFactory);
	}
	
	private static class SessionFactoryWrapperImpl implements SessionFactoryWrapper {
		
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
		public Map<String, EntityPersister> getAllClassMetadata() {
			Map<String, EntityPersister> origin = ((SessionFactoryImplementor)sessionFactory).getMetamodel().entityPersisters();
			Map<String, EntityPersister> result = new HashMap<String, EntityPersister>(origin.size());
			for (String key : origin.keySet()) {
				result.put(key, (EntityPersister)EntityPersisterWrapperFactory.create(origin.get(key)));
			}
			return result;
		}

		@Override 
		public Map<String, CollectionPersister> getAllCollectionMetadata() {
			Map<String, CollectionPersister> origin = ((SessionFactoryImplementor)sessionFactory).getMetamodel().collectionPersisters();
			Map<String, CollectionPersister> result = new HashMap<String, CollectionPersister>(origin.size());
			for (String key : origin.keySet()) {
				result.put(key, (CollectionPersister)CollectionPersisterWrapperFactory.create(origin.get(key)));
			}
			return result;
		}

		@Override 
		public Session openSession() { 
			return sessionFactory.openSession(); 
		}

		@Override 
		public EntityPersister getClassMetadata(String s) { 
			return getAllClassMetadata().get(s); 
		}

		@Override 
		public EntityPersister getClassMetadata(Class<?> c) { 
			return getAllClassMetadata().get(c.getName()); 
		}

		@Override 
		public CollectionPersister getCollectionMetadata(String s) { 
			return getAllCollectionMetadata().get(s); 
		}

	}

}
