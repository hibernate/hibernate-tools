package org.hibernate.tool.orm.jbt.api;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ClassMetadataWrapper extends Wrapper {

	default String getEntityName() { return ((EntityPersister)getWrappedObject()).getEntityName(); }
	default String getIdentifierPropertyName() { return ((EntityPersister)getWrappedObject()).getIdentifierPropertyName(); }
	default String[] getPropertyNames() { return ((EntityPersister)getWrappedObject()).getPropertyNames(); }

}
