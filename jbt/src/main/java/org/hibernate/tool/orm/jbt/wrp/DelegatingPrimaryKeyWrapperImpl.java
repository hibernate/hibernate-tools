package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;

public class DelegatingPrimaryKeyWrapperImpl extends PrimaryKey implements PrimaryKeyWrapper {
	
	private PrimaryKey delegate = null;
	
	public DelegatingPrimaryKeyWrapperImpl(PrimaryKey pk) {
		super(pk.getTable());
		delegate = pk;
	}

	@Override
	public PrimaryKey getWrappedObject() {
		return delegate;
	}

	@Override
	public Iterator<Column> columnIterator() {
		return delegate.getColumns().iterator();
	}

}
