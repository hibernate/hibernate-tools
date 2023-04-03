package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
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
		default boolean isDependantValue() { return DependantValue.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isAny() {return Any.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isSet() {return Set.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isPrimitiveArray() {return PrimitiveArray.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isArray() {return Array.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isIdentifierBag() {return IdentifierBag.class.isAssignableFrom(getWrappedObject().getClass()); }
		default boolean isBag() {return Bag.class.isAssignableFrom(getWrappedObject().getClass()); }
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
		default String getReferencedEntityName() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getReferencedEntityName()'." ); }
		default String getEntityName() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getEntityName()'." ); }
		default String getForeignKeyName() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getForeignKeyName()'." ); }
		default PersistentClass getOwner() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getOwner()'." ); }
		default Iterator<Property> getPropertyIterator() { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'getPropertyIterator()'." ); }
		default void addColumn(Column column) { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'addColumn(Column)'." ); }
		default void setTypeParameters(Properties properties) { throw new UnsupportedOperationException("Class '" + getWrappedObject().getClass().getName() + "' does not support 'setTypeParameters(Properties)'." ); }
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
