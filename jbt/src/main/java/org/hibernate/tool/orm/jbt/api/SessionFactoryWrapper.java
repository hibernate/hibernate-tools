package org.hibernate.tool.orm.jbt.api;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SessionFactoryWrapper extends Wrapper {

	void close();
	Map<String, EntityPersister> getAllClassMetadata();
	Map<String, CollectionPersister> getAllCollectionMetadata();
	Session openSession();
	EntityPersister getClassMetadata(String s);
	EntityPersister getClassMetadata(Class<?> c);
	CollectionPersister getCollectionMetadata(String s);

}
