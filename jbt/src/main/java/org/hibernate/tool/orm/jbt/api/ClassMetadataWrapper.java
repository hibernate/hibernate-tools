package org.hibernate.tool.orm.jbt.api;

import org.hibernate.Session;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface ClassMetadataWrapper extends Wrapper {

	String getEntityName();
	String getIdentifierPropertyName();
	String[] getPropertyNames();
	Type[] getPropertyTypes();
	Class<?> getMappedClass();
	Type getIdentifierType();
	Object getPropertyValue(Object object, String name);
	boolean hasIdentifierProperty();
	Object getIdentifier(Object object, Session session);
	boolean isInstanceOfAbstractEntityPersister();
	Integer getPropertyIndexOrNull(String id);
	Object getTuplizerPropertyValue(Object entity, int i);

}
