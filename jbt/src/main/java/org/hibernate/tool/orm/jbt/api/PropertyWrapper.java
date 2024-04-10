package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface PropertyWrapper extends Wrapper {

	default Value getValue() { return ((Property)getWrappedObject()).getValue(); }
	default void setName(String name) { ((Property)getWrappedObject()).setName(name); }
	default void setPersistentClass(PersistentClass pc) { ((Property)getWrappedObject()).setPersistentClass(pc); }
	default PersistentClass getPersistentClass() { return ((Property)getWrappedObject()).getPersistentClass(); }
	default boolean isComposite() { return ((Property)getWrappedObject()).isComposite(); }
	default String getPropertyAccessorName() { return ((Property)getWrappedObject()).getPropertyAccessorName(); }
	default String getName() { return ((Property)getWrappedObject()).getName(); }
	default Type getType() { 
		Value v = ((Property)getWrappedObject()).getValue();
		return v == null ? null : v.getType();
	}
	default void setValue(BasicValue value) { ((Property)getWrappedObject()).setValue(value); }
	default void setPropertyAccessorName(String s) { ((Property)getWrappedObject()).setPropertyAccessorName(s); }
	default void setCascade(String s) { ((Property)getWrappedObject()).setCascade(s); }
	default boolean isBackRef() { return ((Property)getWrappedObject()).isBackRef(); }
	default boolean isSelectable() { return ((Property)getWrappedObject()).isSelectable(); }
	default boolean isInsertable() { return ((Property)getWrappedObject()).isInsertable(); }
	default boolean isUpdateable() { return ((Property)getWrappedObject()).isUpdateable(); }
	default String getCascade() { return ((Property)getWrappedObject()).getCascade(); }
	default boolean isLazy() { return ((Property)getWrappedObject()).isLazy(); }
	default boolean isOptional() { return ((Property)getWrappedObject()).isOptional(); }
	default boolean isNaturalIdentifier() { return ((Property)getWrappedObject()).isNaturalIdentifier(); }

}
