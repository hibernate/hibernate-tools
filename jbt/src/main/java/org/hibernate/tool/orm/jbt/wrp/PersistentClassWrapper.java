package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapperFactory.RootClassWrapperImpl;

public interface PersistentClassWrapper extends Wrapper {

	default PersistentClass getWrappedObject() { return (PersistentClass)this; }
	String getEntityName();
	String getClassName();
	default boolean isAssignableToRootClass() { return isInstanceOfRootClass(); }
	default boolean isRootClass() { return getWrappedObject().getClass() == RootClassWrapperImpl.class; }
	Property getIdentifierProperty();
	boolean hasIdentifierProperty();
	default boolean isInstanceOfRootClass() { return RootClass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isInstanceOfSubclass() { return Subclass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isInstanceOfJoinedSubclass() { return JoinedSubclass.class.isAssignableFrom(getWrappedObject().getClass()); }
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
	void addProperty(Property p);
	default void setTable(Table table) { throw new RuntimeException("Method 'setTable(Table)' is not supported."); }
	default void setIdentifier(KeyValue value) { throw new RuntimeException("Method 'setIdentifier(KeyValue)' can only be called on RootClass instances"); }
	default void setKey(KeyValue value) { throw new RuntimeException("setKey(KeyValue) is only allowed on JoinedSubclass"); }
	default boolean isInstanceOfSpecialRootClass() { return SpecialRootClass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default Property getParentProperty() { throw new RuntimeException("getParentProperty() is only allowed on SpecialRootClass"); }
	default void setIdentifierProperty(Property property) { throw new RuntimeException("setIdentifierProperty(Property) is only allowed on RootClass instances"); }
	default void setDiscriminator(Value value) { throw new RuntimeException("Method 'setDiscriminator(Value)' can only be called on RootClass instances"); }
	void setProxyInterfaceName(String name);
	void setLazy(boolean b);
	Iterator<Subclass> getSubclassIterator();
	boolean isCustomDeleteCallable();
	boolean isCustomInsertCallable();
	boolean isCustomUpdateCallable();
	boolean isDiscriminatorInsertable();
	
}
