package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableWrapper extends Wrapper {

	default String getName() { return ((Table)getWrappedObject()).getName(); }

	default void addColumn(Column column) { ((Table)getWrappedObject()).addColumn(column); }

	default String getCatalog() { return ((Table)getWrappedObject()).getCatalog(); }

}
