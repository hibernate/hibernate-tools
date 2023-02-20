package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.PersistentClass;

public interface PersistentClassWrapper {

	default PersistentClass getWrappedObject() { return (PersistentClass)this; }
	String getEntityName();
	String getClassName();

}
