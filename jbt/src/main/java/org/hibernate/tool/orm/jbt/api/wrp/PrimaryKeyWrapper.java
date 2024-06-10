package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Iterator;
import java.util.List;

public interface PrimaryKeyWrapper extends Wrapper {

	void addColumn(ColumnWrapper column);
	int getColumnSpan();
	List<ColumnWrapper> getColumns();
	ColumnWrapper getColumn(int i);
	TableWrapper getTable();
	boolean containsColumn(ColumnWrapper column);
	Iterator<ColumnWrapper> columnIterator();
	String getName();

}
