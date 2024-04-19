package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableWrapper extends Wrapper {

	default String getName() { return ((Table)getWrappedObject()).getName(); }

	default void addColumn(Column column) { ((Table)getWrappedObject()).addColumn(column); }

	default String getCatalog() { return ((Table)getWrappedObject()).getCatalog(); }

	default String getSchema() { return ((Table)getWrappedObject()).getSchema(); }

	default PrimaryKey getPrimaryKey() { return ((Table)getWrappedObject()).getPrimaryKey(); }

	default Iterator<Column> getColumnIterator() { return ((Table)getWrappedObject()).getColumns().iterator(); }

	default String getComment() { return ((Table)getWrappedObject()).getComment(); }

	default String getRowId() { return ((Table)getWrappedObject()).getRowId(); }

	default String getSubselect() { return ((Table)getWrappedObject()).getSubselect(); }

	default boolean hasDenormalizedTables() { return ((Table)getWrappedObject()).hasDenormalizedTables(); }

	default boolean isAbstract() { return ((Table)getWrappedObject()).isAbstract(); }

	default boolean isAbstractUnionTable() { return ((Table)getWrappedObject()).isAbstractUnionTable(); }

	default boolean isPhysicalTable() { return ((Table)getWrappedObject()).isPhysicalTable(); }

}
