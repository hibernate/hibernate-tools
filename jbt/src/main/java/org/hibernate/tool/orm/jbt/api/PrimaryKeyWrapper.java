package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PrimaryKeyWrapper extends Wrapper {

	default void addColumn(Column column) { ((PrimaryKey)getWrappedObject()).addColumn(column); }
	default int getColumnSpan() { return ((PrimaryKey)getWrappedObject()).getColumnSpan(); }

}
