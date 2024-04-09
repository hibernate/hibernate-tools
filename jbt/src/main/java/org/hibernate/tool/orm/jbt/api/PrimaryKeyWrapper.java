package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PrimaryKeyWrapper extends Wrapper {

	default void addColumn(Column column) { ((PrimaryKey)getWrappedObject()).addColumn(column); }
	default int getColumnSpan() { return ((PrimaryKey)getWrappedObject()).getColumnSpan(); }
	default List<Column> getColumns() { return ((PrimaryKey)getWrappedObject()).getColumns(); }
	default Column getColumn(int i) { return ((PrimaryKey)getWrappedObject()).getColumn(i); }
	default Table getTable() { return ((PrimaryKey)getWrappedObject()).getTable(); }
	default boolean containsColumn(Column column) { return ((PrimaryKey)getWrappedObject()).containsColumn(column); }

}
