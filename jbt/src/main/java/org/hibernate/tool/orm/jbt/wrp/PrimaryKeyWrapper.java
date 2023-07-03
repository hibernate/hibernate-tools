package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public interface PrimaryKeyWrapper extends Wrapper {

	@Override PrimaryKey getWrappedObject();
	void addColumn(Column column);
	int getColumnSpan();
	List<Column> getColumns();
	Column getColumn(int i);
	Table getTable();
	boolean containsColumn(Column column);
	Iterator<Column> columnIterator();
	String getName();

}
