package org.hibernate.tool.orm.jbt.wrp;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MappingException;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public class SessionFactoryWrapper extends SessionFactoryDelegatingImpl {
	
	public SessionFactoryWrapper(SessionFactory delegate) {
		super((SessionFactoryImplementor)delegate);
	}

	public Map<String, EntityPersister> getAllClassMetadata() {
		return getMetamodel().entityPersisters();
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
	
}
