package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ValueWrapper extends Wrapper {

	default boolean isSimpleValue() { return ((Value)getWrappedObject()).isSimpleValue(); }
	default boolean isCollection() { return Collection.class.isAssignableFrom(getWrappedObject().getClass()); }

}
