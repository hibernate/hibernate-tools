package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public class PrimaryKeyWrapperFactory {

	public static PrimaryKeyWrapper createForeinKeyWrapper(PrimaryKey primaryKey) {
		return (PrimaryKeyWrapper)Proxy.newProxyInstance(
				PrimaryKeyWrapper.class.getClassLoader(), 
				new Class[] { PrimaryKeyWrapper.class },
				new PrimaryKeyWrapperInvocationHandler(primaryKey));				
	}
	
	static interface PrimaryKeyWrapper extends Wrapper {
		@Override PrimaryKey getWrappedObject();
		default void addColumn(Column column) { getWrappedObject().addColumn(column); }
		default int getColumnSpan() { return getWrappedObject().getColumnSpan(); }
		default List<Column> getColumns() { return getWrappedObject().getColumns(); }
		default Column getColumn(int i) { return getWrappedObject().getColumn(i); }
		default Table getTable() { return getWrappedObject().getTable(); }
		default boolean containsColumn(Column column) { return getWrappedObject().containsColumn(column); }
		default Iterator<Column> columnIterator() { 
			return getWrappedObject().getColumns().iterator();
		}
		default String getName() { return getWrappedObject().getName(); }
	}
	
	private static class PrimaryKeyWrapperInvocationHandler implements PrimaryKeyWrapper, InvocationHandler {
		
		private PrimaryKey wrappedPrimaryKey = null;
		
		private PrimaryKeyWrapperInvocationHandler(PrimaryKey primaryKey) {
			wrappedPrimaryKey = primaryKey;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(this, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		
		@Override
		public PrimaryKey getWrappedObject() {
			return wrappedPrimaryKey;
		}
		
	}

}
