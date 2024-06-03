package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface PropertyWrapper extends Wrapper {

	Value getValue();
	void setName(String name);
	void setPersistentClass(PersistentClass pc);
	PersistentClass getPersistentClass();
	boolean isComposite();
	String getPropertyAccessorName();
	String getName();
	Type getType();
	void setValue(Value value);
	void setPropertyAccessorName(String s);
	void setCascade(String s);
	boolean isBackRef();
	boolean isSelectable();
	boolean isInsertable();
	boolean isUpdateable();
	String getCascade();
	boolean isLazy();
	boolean isOptional();
	boolean isNaturalIdentifier();
	boolean isOptimisticLocked();

}
