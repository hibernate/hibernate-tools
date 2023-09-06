package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public class DelegatingTableWrapperImpl extends Table implements TableWrapper{
	
	private Table delegate = null;
	
	public DelegatingTableWrapperImpl(Table t) {
		delegate = t;
	}
	
	@Override
	public Table getWrappedObject() {
		return delegate;
	}

	@Override
	public KeyValue getIdentifierValue() {
		KeyValue result = delegate.getIdentifierValue();
		return result == null ? null : (KeyValue)ValueWrapperFactory.createValueWrapper(result);
	}
	
	@Override 
	public String getName() {
		return delegate.getName();
	}
	
	@Override
	public void addColumn(Column column) {
		delegate.addColumn(column);
	}
	
	@Override 
	public String getCatalog() {
		return delegate.getCatalog();
	}
	
	@Override 
	public String getSchema() {
		return delegate.getSchema();
	}
	
	@Override
	public PrimaryKey getPrimaryKey() {
		PrimaryKey result = delegate.getPrimaryKey();
		return result == null ? null : new DelegatingPrimaryKeyWrapperImpl(result);
	}
	
	@Override
	public Iterator<Column> getColumnIterator() {
		final Iterator<Column> iterator = delegate.getColumns().iterator();
		return new Iterator<Column>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public Column next() {
				return new DelegatingColumnWrapperImpl(iterator.next());
			}			
		};
	}
	
	@Override
	public Iterator<ForeignKey> getForeignKeyIterator() {
		Iterator<ForeignKey> iterator = delegate.getForeignKeyIterator();
		return new Iterator<ForeignKey>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public ForeignKey next() {
				return new DelegatingForeignKeyWrapperImpl(iterator.next());
			}		
		};
	}
	
	@Override
	public String getComment() {
		return delegate.getComment();
	}
	
	@Override
	public String getRowId() {
		return delegate.getRowId();
	}
	
	@Override
	public String getSubselect() {
		return delegate.getSubselect();
	}
	
	@Override
	public boolean hasDenormalizedTables() {
		return delegate.hasDenormalizedTables();
	}
	
	@Override 
	public boolean isAbstract() {
		return delegate.isAbstract();
	}
	
	@Override
	public boolean isAbstractUnionTable() {
		return delegate.isAbstractUnionTable();
	}
	
	@Override
	public boolean isPhysicalTable() {
		return delegate.isPhysicalTable();
	}

}
