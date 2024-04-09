package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PropertyWrapper extends Wrapper {

	default Value getValue() { return ((Property)getWrappedObject()).getValue(); }
	default void setName(String name) { ((Property)getWrappedObject()).setName(name); }
	default void setPersistentClass(PersistentClass pc) { ((Property)getWrappedObject()).setPersistentClass(pc); }
	default PersistentClass getPersistentClass() { return ((Property)getWrappedObject()).getPersistentClass(); }
	default boolean isComposite() { return ((Property)getWrappedObject()).isComposite(); }

}
