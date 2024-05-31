package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface ValueWrapper extends Wrapper {

	boolean isSimpleValue();
	boolean isCollection();
	ValueWrapper getCollectionElement();
	boolean isOneToMany();
	boolean isManyToOne();
	boolean isOneToOne();
	boolean isMap();
	boolean isComponent();
	boolean isEmbedded();
	boolean isToOne();
	Table getTable();
	Type getType();
	void setElement(Value v);
	void setCollectionTable(Table table);
	void setTable(Table table);
	boolean isList();
	void setIndex(Value v);
	void setTypeName(String s);
	String getComponentClassName();
	Iterator<Selectable> getColumnIterator();
	boolean isTypeSpecified();
	Table getCollectionTable();
	KeyValue getKey();
	Value getIndex();
	String getElementClassName();
	String getTypeName();
	boolean isDependantValue();
	boolean isAny();
	boolean isSet();
	boolean isPrimitiveArray();
	boolean isArray();
	boolean isIdentifierBag();
	boolean isBag();
	String getReferencedEntityName();
	String getEntityName();
	Iterator<Property> getPropertyIterator();
	void addColumn(Column column);
	void setTypeParameters(Properties properties);
	String getForeignKeyName();
	PersistentClass getOwner();
	Value getElement();
	String getParentProperty();
	void setElementClassName(String name);
	void setKey(Value value);
	void setFetchModeJoin();
	boolean isInverse();
	PersistentClass getAssociatedClass() ;
	void setLazy(boolean b);
	void setRole(String role);
	void setReferencedEntityName(String name);
	void setAssociatedClass(PersistentClass pc);

}
