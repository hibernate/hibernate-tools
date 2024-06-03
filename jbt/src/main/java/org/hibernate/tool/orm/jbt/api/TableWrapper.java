package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableWrapper extends Wrapper {

	String getName();
	void addColumn(Column column);
	String getCatalog();
	String getSchema();
	PrimaryKey getPrimaryKey();
	Iterator<Column> getColumnIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables() ;
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();
	KeyValue getIdentifierValue();
	
}
