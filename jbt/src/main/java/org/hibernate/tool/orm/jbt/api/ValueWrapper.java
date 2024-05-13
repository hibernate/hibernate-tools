package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

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
	default Type getType() { return ((Value)getWrappedObject()).getType(); }
	default void setElement(Value v) { 
		if (isCollection()) {
				((Collection)getWrappedObject()).setElement(v);
		}
	}
	default void setCollectionTable(Table table) {
		if (isCollection()) {
			((Collection)getWrappedObject()).setCollectionTable(table);
		}
	}
	default void setTable(Table table) {
		if (isSimpleValue()) {
			((SimpleValue)getWrappedObject()).setTable(table);
		}
	}
	default boolean isList() { return List.class.isAssignableFrom(getWrappedObject().getClass()); }
	default void setIndex(Value v) {
		if (IndexedCollection.class.isAssignableFrom(getWrappedObject().getClass())) {
			((IndexedCollection)getWrappedObject()).setIndex(v);
		}
	}
	default void setTypeName(String s) { 
		if (isCollection()) {
			((Collection)getWrappedObject()).setTypeName(s);
		} else if (isSimpleValue()) {
			((SimpleValue)getWrappedObject()).setTypeName(s);
		}
	}
	default String getComponentClassName() { 
		if ((isComponent())) {
			return ((Component)getWrappedObject()).getComponentClassName();
		}
		return null;
	}
	default Iterator<Selectable> getColumnIterator() {
		return ((Value)getWrappedObject()).getSelectables().iterator();
	}
	default boolean isTypeSpecified() { 
		if (isSimpleValue()) {
			return ((SimpleValue)getWrappedObject()).isTypeSpecified();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'isTypeSpecified()'." );
		}
	}
	default Table getCollectionTable() {
		if (isCollection()) {
			return ((Collection)getWrappedObject()).getCollectionTable();
		} else {
			return null;
		}
	}
	default KeyValue getKey() { 
		if (isCollection()) {
			return ((Collection)getWrappedObject()).getKey();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getKey()'." ); 
		}
	}
	default Value getIndex() {
		if (IndexedCollection.class.isAssignableFrom(getWrappedObject().getClass())) {
			return ((IndexedCollection)getWrappedObject()).getIndex();
		} else {
			return null;
		}
	}
	default String getElementClassName() {
		if (Array.class.isAssignableFrom(getWrappedObject().getClass())) {
			return ((Array)getWrappedObject()).getElementClassName();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getElementClassName()'." );
		}
	}
	default String getTypeName() { 
		if (isCollection()) {
			return ((Collection)getWrappedObject()).getTypeName();
		} else if (isSimpleValue()) {
			return ((SimpleValue)getWrappedObject()).getTypeName();
		} else { 
			return null; 
		}
	}
	default boolean isDependantValue() { return DependantValue.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isAny() {return Any.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isSet() {return Set.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isPrimitiveArray() {return PrimitiveArray.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isArray() {return Array.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isIdentifierBag() {return IdentifierBag.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isBag() {return Bag.class.isAssignableFrom(getWrappedObject().getClass()); }
	default String getReferencedEntityName() { 
		if (isManyToOne() || isOneToOne()) {
			return ((ToOne)getWrappedObject()).getReferencedEntityName();
		} else if (isOneToMany()) {
			return ((OneToMany)getWrappedObject()).getReferencedEntityName();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getReferencedEntityName()'." ); 
		}
	}
	default String getEntityName() { 
		if (isOneToOne()) {
			return ((OneToOne)getWrappedObject()).getEntityName();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getEntityName()'." ); }
	}
	default Iterator<Property> getPropertyIterator() { 
		if (isComponent()) {
			return ((Component)getWrappedObject()).getProperties().iterator();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getPropertyIterator()'." ); 
		}
	}
	default void addColumn(Column column) { 
		if (isSimpleValue()) {
			((SimpleValue)getWrappedObject()).addColumn(column);
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'addColumn(Column)'." ); 
		}
	}
	default void setTypeParameters(Properties properties) {
		if (isCollection()) {
			((Collection)getWrappedObject()).setTypeParameters(properties);
		} else if (isSimpleValue()) {
			((SimpleValue)getWrappedObject()).setTypeParameters(properties);
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'setTypeParameters(Properties)'." );		
		}
	}
	default String getForeignKeyName() { 
		if (isSimpleValue()) {
			return ((SimpleValue)getWrappedObject()).getForeignKeyName();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getForeignKeyName()'." ); 
		}
	}
	default PersistentClass getOwner() { 
		if (isCollection()) {
			return ((Collection)getWrappedObject()).getOwner();
		} else if (isComponent()) {
			return ((Component)getWrappedObject()).getOwner();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getOwner()'." ); 
		}
	}
	default Value getElement() { 
		if (isCollection()) {
			return ((Collection)getWrappedObject()).getElement();
		} else {
			return null; 
		}
	}
	default String getParentProperty() { 
		if (isComponent()) {
			return ((Component)getWrappedObject()).getParentProperty();
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getParentProperty()'." ); 
		}
	}
	default void setElementClassName(String name) { 
		if (isArray()) {
			((Array)getWrappedObject()).setElementClassName(name);
		} else {
			throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'setElementClassName(String)'." ); 
		}
	}
}
