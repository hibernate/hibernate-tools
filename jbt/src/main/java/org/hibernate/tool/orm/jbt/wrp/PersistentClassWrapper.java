package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.PersistentClass;

public interface PersistentClassWrapper {

	PersistentClass getWrappedObject();
	String getEntityName();
	String getClassName();

}
