package org.hibernate.tool.orm.jbt.api.wrp;

public interface PropertyWrapper extends Wrapper {

	ValueWrapper getValue();
	void setName(String name);
	void setPersistentClass(PersistentClassWrapper pc);
	PersistentClassWrapper getPersistentClass();
	boolean isComposite();
	String getPropertyAccessorName();
	String getName();
	TypeWrapper getType();
	void setValue(ValueWrapper value);
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
