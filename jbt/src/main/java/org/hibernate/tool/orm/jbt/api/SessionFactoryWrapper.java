package org.hibernate.tool.orm.jbt.api;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.CollectionPersisterWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SessionFactoryWrapper extends Wrapper {

	default void close() { ((SessionFactory)getWrappedObject()).close(); }

	default Map<String, EntityPersister> getAllClassMetadata() {
		Map<String, EntityPersister> origin = ((SessionFactoryImplementor)getWrappedObject()).getMetamodel().entityPersisters();
		Map<String, EntityPersister> result = new HashMap<String, EntityPersister>(origin.size());
		for (String key : origin.keySet()) {
			result.put(key, (EntityPersister)EntityPersisterWrapperFactory.create(origin.get(key)));
		}
		return result;
	}

	default Map<String, CollectionPersister> getAllCollectionMetadata() {
		Map<String, CollectionPersister> origin = ((SessionFactoryImplementor)getWrappedObject()).getMetamodel().collectionPersisters();
		Map<String, CollectionPersister> result = new HashMap<String, CollectionPersister>(origin.size());
		for (String key : origin.keySet()) {
			result.put(key, (CollectionPersister)CollectionPersisterWrapperFactory.create(origin.get(key)));
		}
		return result;
	}

	default Session openSession() { return ((SessionFactory)getWrappedObject()).openSession(); }

	default EntityPersister getClassMetadata(String s) { return getAllClassMetadata().get(s); }

	default Object getClassMetadata(Class<?> c) { return getAllClassMetadata().get(c.getName()); }

}
