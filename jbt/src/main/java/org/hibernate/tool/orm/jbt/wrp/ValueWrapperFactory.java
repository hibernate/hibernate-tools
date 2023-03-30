package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class ValueWrapperFactory {
	
	public static ValueWrapper createValueWrapper(Value wrappedValue) {
		return (ValueWrapper)Proxy.newProxyInstance(
				ValueWrapperFactory.class.getClassLoader(), 
				new Class[] { ValueWrapper.class }, 
				new ValueWrapperInvocationHandler(wrappedValue));
	}

	static interface ValueExtension extends Wrapper {
		default Value getWrappedObject() { return (Value)this; }
		default boolean isCollection() { return Collection.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isOneToMany() { return OneToMany.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isManyToOne() { return ManyToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isOneToOne() { return OneToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isMap() { return Map.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isList() { return List.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isToOne() { return ToOne.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isEmbedded() { return false; }
		default Value getElement() { return null; }
		default void setElement(Value element) {}
		default void setTable(Table table) {}
		default Table getCollectionTable() { return null; }
		default void setCollectionTable(Table table) {}
		default Value getCollectionElement() { return getElement(); }
		default void setIndex(Value index) {}
		default Value getIndex() { return null; }
		default void setTypeName(String name) {}
		default String getTypeName() { return null; }
		default String getComponentClassName() { return null; }
		default boolean isTypeSpecified() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'isTypeSpecified()'." ); }
		default KeyValue getKey() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getKey()'." ); }
		default String getElementClassName() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getElementClassName()'." ); }
	}
	
	static interface ValueWrapper extends Value, ValueExtension {}
	
	
	private static class ValueWrapperInvocationHandler implements ValueExtension, InvocationHandler {
		
		private Value extendedValue = null;
		
		public ValueWrapperInvocationHandler(Value value) {
			extendedValue = value;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				Method valueClassMethod = lookupMethodInValueClass(extendedValue, method);
				if (valueClassMethod != null) {
					return valueClassMethod.invoke(extendedValue, args);
				} else {
					return method.invoke(this, args);
				}
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		
		@Override
		public Value getWrappedObject() {
			return extendedValue;
		}
		
		@Override
		public Value getElement() {
			try {
				return (Value)extendedValue.getClass().getMethod("getElement", new Class[] {}).invoke(extendedValue);
			} catch (NoSuchMethodException e) {
				// TODO for now return null, needs to be replaced by throwing UnsupportedOperationException
				return null;
				// throw new UnsupportedOperationException("Class '" + extendedValue.getClass().getName() + "' does not support 'isTypeSpecified()'." );
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	private static Method lookupMethodInValueClass(Value value, Method method) {
		try {
			return value
				.getClass()
				.getMethod(method.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
}
