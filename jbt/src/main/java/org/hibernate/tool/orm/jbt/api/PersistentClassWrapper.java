package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PersistentClassWrapper extends Wrapper {

	boolean isAssignableToRootClass();
	boolean isRootClass();
	boolean isInstanceOfRootClass();
	boolean isInstanceOfSubclass();
	boolean isInstanceOfJoinedSubclass();
	PropertyWrapper getProperty();
	void setTable(TableWrapper table);
	void setIdentifier(ValueWrapper value);
	void setKey(ValueWrapper value);
	boolean isInstanceOfSpecialRootClass();
	PropertyWrapper getParentProperty();
	void setIdentifierProperty(PropertyWrapper property);
	void setDiscriminator(ValueWrapper value);
	boolean isLazyPropertiesCacheable();
	Iterator<PropertyWrapper> getPropertyIterator();
	Iterator<JoinWrapper> getJoinIterator();
	Iterator<PersistentClassWrapper> getSubclassIterator();
	Iterator<PropertyWrapper> getPropertyClosureIterator();
	String getEntityName();
	String getClassName();
	PropertyWrapper getIdentifierProperty();
	boolean hasIdentifierProperty();
	PersistentClassWrapper getRootClass();
	PersistentClassWrapper getSuperclass();
	PropertyWrapper getProperty(String name);
	TableWrapper getTable();
	Boolean isAbstract();
	ValueWrapper getDiscriminator();
	ValueWrapper getIdentifier();
	PropertyWrapper getVersion();
	void setClassName(String name);
	void setEntityName(String name);
	void setDiscriminatorValue(String str);
	void setAbstract(Boolean b);
	void addProperty(PropertyWrapper p);
	void setProxyInterfaceName(String name);
	void setLazy(boolean b);
	boolean isCustomDeleteCallable();
	boolean isCustomInsertCallable();
	boolean isCustomUpdateCallable();
	boolean isDiscriminatorInsertable();
	boolean isDiscriminatorValueNotNull();
	boolean isDiscriminatorValueNull();
	boolean isExplicitPolymorphism();
	boolean isForceDiscriminator();
	boolean isInherited();
	boolean isJoinedSubclass();
	boolean isLazy();
	boolean isMutable();
	boolean isPolymorphic();
	boolean isVersioned();
	int getBatchSize();
	String getCacheConcurrencyStrategy();
	String getCustomSQLDelete();
	String getCustomSQLInsert();
	String getCustomSQLUpdate();
	String getDiscriminatorValue();
	String getLoaderName();
	int getOptimisticLockMode();
	String getWhere();
	TableWrapper getRootTable();
	List<PropertyWrapper> getProperties();
	List<JoinWrapper> getJoins();
	List<PersistentClassWrapper> getSubclasses();
	List<PropertyWrapper> getPropertyClosure();
	
}
