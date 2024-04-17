package org.hibernate.tool.orm.jbt.api;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
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
	};

}
