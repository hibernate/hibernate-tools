package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ValueWrapper extends Wrapper {

	default boolean isSimpleValue() { return ((Value)getWrappedObject()).isSimpleValue(); }
	default boolean isCollection() { return Collection.class.isAssignableFrom(getWrappedObject().getClass()); }
	default ValueWrapper getCollectionElement() {
		if (isCollection()) {
			Value v = ((Collection)getWrappedObject()).getElement();
			if (v != null) return ValueWrapperFactory.createValueWrapper(v);
		}
		return null;
	}
	default boolean isOneToMany() { return OneToMany.class.isAssignableFrom(getWrappedObject().getClass()); }

}
