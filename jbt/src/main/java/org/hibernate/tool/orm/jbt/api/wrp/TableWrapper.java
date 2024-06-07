package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Iterator;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableWrapper extends Wrapper {

	String getName();
	void addColumn(ColumnWrapper column);
	String getCatalog();
	String getSchema();
	PrimaryKeyWrapper getPrimaryKey();
	Iterator<ColumnWrapper> getColumnIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables() ;
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();
	ValueWrapper getIdentifierValue();
	
}
