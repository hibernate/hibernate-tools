package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PrimaryKeyWrapper extends Wrapper {

	void addColumn(Column column);
	int getColumnSpan();
	List<Column> getColumns();
	Column getColumn(int i);
	Table getTable();
	boolean containsColumn(Column column);
	Iterator<Column> columnIterator();
	String getName();

}
