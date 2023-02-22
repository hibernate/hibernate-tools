package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapperFactory.RootClassWrapperImpl;

public interface PersistentClassWrapper {

	default PersistentClass getWrappedObject() { return (PersistentClass)this; }
	String getEntityName();
	String getClassName();
	default boolean isAssignableToRootClass() { return isInstanceOfRootClass(); }
	default boolean isRootClass() { return getWrappedObject().getClass() == RootClassWrapperImpl.class; }
	Property getIdentifierProperty();
	boolean hasIdentifierProperty();
	default boolean isInstanceOfRootClass() { return RootClass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isInstanceOfSubclass() { return Subclass.class.isAssignableFrom(getWrappedObject().getClass()); }
	PersistentClass getRootClass();
	Iterator<Property> getPropertyClosureIterator();
	PersistentClass getSuperclass();
	Iterator<Property> getPropertyIterator();
	Property getProperty(String name);
	default Property getProperty() { throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); }
	Table getTable();
	Boolean isAbstract();
	Value getDiscriminator();
	Value getIdentifier();
	Iterator<Join> getJoinIterator();
	Property getVersion();
	void setClassName(String name);
	void setEntityName(String name);
	void setDiscriminatorValue(String str);
	void setAbstract(Boolean b);

}
