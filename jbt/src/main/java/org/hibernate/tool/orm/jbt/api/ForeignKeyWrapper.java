package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ForeignKeyWrapper extends Wrapper {
	
	default Table getReferencedTable() { return ((ForeignKey)getWrappedObject()).getReferencedTable(); }
	
	default Iterator<Column> columnIterator() { return ((ForeignKey)getWrappedObject()).getColumns().iterator(); }
	
	default boolean isReferenceToPrimaryKey() { return ((ForeignKey)getWrappedObject()).isReferenceToPrimaryKey(); }
	
	default List<Column> getReferencedColumns() { return ((ForeignKey)getWrappedObject()).getReferencedColumns(); }

	default boolean containsColumn(Column column) { return ((ForeignKey)getWrappedObject()).containsColumn(column); }

}
