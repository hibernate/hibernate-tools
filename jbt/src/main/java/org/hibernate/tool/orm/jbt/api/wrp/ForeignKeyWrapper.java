package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Iterator;
import java.util.List;

public interface ForeignKeyWrapper extends Wrapper {
	
	TableWrapper getReferencedTable();
	
	Iterator<ColumnWrapper> columnIterator();
	
	boolean isReferenceToPrimaryKey();
	
	List<ColumnWrapper> getReferencedColumns();

	boolean containsColumn(ColumnWrapper column);

}
