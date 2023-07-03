package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public class DelegatingForeignKeyWrapperImpl extends ForeignKey implements ForeignKeyWrapper {
	
	private ForeignKey delegate = null;
	
	public DelegatingForeignKeyWrapperImpl(ForeignKey fk) {
		delegate = fk;
	}

	@Override
	public ForeignKey getWrappedObject() {
		return delegate;
	}

	@Override
	public Iterator<Column> columnIterator() {
		return delegate.getColumns().iterator();
	}

	@Override
	public Table getReferencedTable() {
		Table result = delegate.getReferencedTable();
		return result == null ? null : new DelegatingTableWrapperImpl(result);
	}
	
	@Override
	public boolean isReferenceToPrimaryKey() {
		return delegate.isReferenceToPrimaryKey();
	}
	
	@Override
	public List<Column> getReferencedColumns() {
		return delegate.getReferencedColumns();
	}
	
	@Override
	public boolean containsColumn(Column column) {
		return delegate.containsColumn(column);
	}
}
