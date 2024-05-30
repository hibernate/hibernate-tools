package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ForeignKeyWrapper extends Wrapper {
	
	Table getReferencedTable();
	
	Iterator<Column> columnIterator();
	
	boolean isReferenceToPrimaryKey();
	
	List<Column> getReferencedColumns();

	boolean containsColumn(Column column);

}
