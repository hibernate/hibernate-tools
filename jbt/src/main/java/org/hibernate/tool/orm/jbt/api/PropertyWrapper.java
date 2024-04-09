package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PropertyWrapper extends Wrapper {

	default Value getValue() { return ((Property)getWrappedObject()).getValue(); }
	default void setName(String name) { ((Property)getWrappedObject()).setName(name); }

}
