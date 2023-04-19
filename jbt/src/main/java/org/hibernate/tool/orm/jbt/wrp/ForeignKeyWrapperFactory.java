package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public class ForeignKeyWrapperFactory {
	
	public static ForeignKeyWrapper createForeinKeyWrapper(ForeignKey foreignKey) {
		return (ForeignKeyWrapper)Proxy.newProxyInstance(
				ForeignKeyWrapper.class.getClassLoader(), 
				new Class[] { ForeignKeyWrapper.class },
				new ForeignKeyWrapperInvocationHandler(foreignKey));				
	}
	
	static interface ForeignKeyExtension extends Wrapper {
		default Iterator<Column> columnIterator() {
			return ((ForeignKey)getWrappedObject()).getColumns().iterator();
		}
		default Table getReferencedTable() {
			return ((ForeignKey)getWrappedObject()).getReferencedTable();
		}
	}
	
	static interface ForeignKeyWrapper extends ForeignKeyExtension {}
	
	private static class ForeignKeyWrapperInvocationHandler implements ForeignKeyExtension, InvocationHandler {
		
		private ForeignKey extendedForeignKey = null;
		
		private ForeignKeyWrapperInvocationHandler(ForeignKey foreignKey) {
			extendedForeignKey = foreignKey;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				Method foreignKeyClassMethod = lookupMethodInForeignKeyClass(extendedForeignKey, method);
				if (foreignKeyClassMethod != null) {
					return foreignKeyClassMethod.invoke(extendedForeignKey, args);
				} else {
					return method.invoke(this, args);
				}
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		
		@Override
		public ForeignKey getWrappedObject() {
			return extendedForeignKey;
		}
		
	}

	private static Method lookupMethodInForeignKeyClass(ForeignKey foreignKey, Method method) {
		try {
			return foreignKey
				.getClass()
				.getMethod(method.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
}
