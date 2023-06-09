package org.hibernate.tool.orm.jbt.wrp;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public class SessionFactoryWrapper extends SessionFactoryDelegatingImpl {
	
	public SessionFactoryWrapper(SessionFactory delegate) {
		super((SessionFactoryImplementor)delegate);
	}
	
	@Override
	public SessionImplementor openSession() {
		return SessionWrapperFactory.createSessionWrapper(this, super.openSession());
	}
	
	public Map<String, EntityPersister> getAllClassMetadata() {
		Map<String, EntityPersister> origin = getMetamodel().entityPersisters();
		Map<String, EntityPersister> result = new HashMap<String, EntityPersister>(origin.size());
		for (String key : origin.keySet()) {
			result.put(key, (EntityPersister)EntityPersisterWrapperFactory.create(origin.get(key)));
		}
		return result;
	}

	public Map<String, CollectionPersister> getAllCollectionMetadata() {
		return getMetamodel().collectionPersisters();
	}

	public EntityPersister getClassMetadata(String string) {
		return getAllClassMetadata().get(string);
	}
	
	public EntityPersister getClassMetadata(Class<?> clazz) {
		return getClassMetadata(clazz.getName());
	}

	public CollectionPersister getCollectionMetadata(String string) {
		return getAllCollectionMetadata().get(string); 
	}
	
}
