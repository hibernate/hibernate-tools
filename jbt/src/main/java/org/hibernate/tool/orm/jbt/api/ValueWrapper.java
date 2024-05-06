package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
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
	default boolean isManyToOne() { return ManyToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isOneToOne() { return OneToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isMap() { return Map.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isComponent() { return Component.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isEmbedded() { 
		if (isComponent()) {
			return ((Component)getWrappedObject()).isEmbedded();
		}
		return false;
	}
	default boolean isToOne() { return ToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
	default Table getTable() { return ((Value)getWrappedObject()).getTable(); }

}
