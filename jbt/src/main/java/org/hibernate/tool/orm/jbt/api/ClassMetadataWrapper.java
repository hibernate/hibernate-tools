package org.hibernate.tool.orm.jbt.api;

import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface ClassMetadataWrapper extends Wrapper {

	default String getEntityName() { return ((EntityPersister)getWrappedObject()).getEntityName(); }
	default String getIdentifierPropertyName() { return ((EntityPersister)getWrappedObject()).getIdentifierPropertyName(); }
	default String[] getPropertyNames() { return ((EntityPersister)getWrappedObject()).getPropertyNames(); }
	default Type[] getPropertyTypes() { return ((EntityPersister)getWrappedObject()).getPropertyTypes(); }
	default Class<?> getMappedClass() { return ((EntityPersister)getWrappedObject()).getMappedClass(); }
	default Type getIdentifierType() { return ((EntityPersister)getWrappedObject()).getIdentifierType(); }
	default Object getPropertyValue(Object object, String name) { return ((EntityPersister)getWrappedObject()).getPropertyValue(object, name); }
	default boolean hasIdentifierProperty() { return ((EntityPersister)getWrappedObject()).hasIdentifierProperty(); }
	default Object getIdentifier(Object object, Session session) { return ((EntityPersister)getWrappedObject()).getIdentifier(object, (SharedSessionContractImplementor)session); }
	default boolean isInstanceOfAbstractEntityPersister() { return (EntityPersister)getWrappedObject() instanceof AbstractEntityPersister; }
	default Integer getPropertyIndexOrNull(String id) { return ((EntityPersister)getWrappedObject()).getEntityMetamodel().getPropertyIndexOrNull(id); }

}
