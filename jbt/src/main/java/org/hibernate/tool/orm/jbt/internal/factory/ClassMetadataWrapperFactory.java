package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.ClassMetadataWrapper;

public class ClassMetadataWrapperFactory {

	public static ClassMetadataWrapper createClassMetadataWrapper(final EntityPersister entityPersister) {
		return new ClassMetadataWrapper() {
			@Override public EntityPersister getWrappedObject() { return entityPersister; }
		};
	}
	
}
