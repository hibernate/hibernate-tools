package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Value;

public interface TableWrapper extends Wrapper {
	
	String getName();
	void addColumn(Column column);
	String getCatalog();
	String getSchema();
	PrimaryKey getPrimaryKey();
	Iterator<Column> getColumnIterator();
	Iterator<ForeignKey> getForeignKeyIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables();
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();
	Value getIdentifierValue();

}
