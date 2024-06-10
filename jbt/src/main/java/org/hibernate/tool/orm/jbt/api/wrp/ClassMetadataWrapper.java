package org.hibernate.tool.orm.jbt.api.wrp;

public interface ClassMetadataWrapper extends Wrapper {

	String getEntityName();
	String getIdentifierPropertyName();
	String[] getPropertyNames();
	TypeWrapper[] getPropertyTypes();
	Class<?> getMappedClass();
	TypeWrapper getIdentifierType();
	Object getPropertyValue(Object object, String name);
	boolean hasIdentifierProperty();
	Object getIdentifier(Object object, SessionWrapper session);
	boolean isInstanceOfAbstractEntityPersister();
	Integer getPropertyIndexOrNull(String id);
	Object getTuplizerPropertyValue(Object entity, int i);

}
