package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public interface ForeignKeyWrapper extends Wrapper {
	
	@Override ForeignKey getWrappedObject();	
	Table getReferencedTable();
	Iterator<Column> columnIterator();
	boolean isReferenceToPrimaryKey();
	List<Column> getReferencedColumns();
	boolean containsColumn(Column column);

}
