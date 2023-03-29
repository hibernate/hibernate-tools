package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;

public class ValueWrapperFactory {
	
	public static ValueWrapper createArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return createValueWrapper(
				new Array(
						DummyMetadataBuildingContext.INSTANCE, 
						persistentClassWrapper.getWrappedObject()));
	}

	public static ValueWrapper createBagWrapper(PersistentClassWrapper persistentClassWrapper) {
		return createValueWrapper(
				new Bag(
						DummyMetadataBuildingContext.INSTANCE, 
						persistentClassWrapper.getWrappedObject()));
	}

	public static ValueWrapper createListWrapper(PersistentClassWrapper persistentClassWrapper) {
		return createValueWrapper(
				new List(
						DummyMetadataBuildingContext.INSTANCE, 
						persistentClassWrapper.getWrappedObject()));
	}

	public static ValueWrapper createManyToOneWrapper(Table table) {
		return createValueWrapper(
				new ManyToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						table));
	}

	public static ValueWrapper createMapWrapper(PersistentClassWrapper persistentClassWrapper) {
		return createValueWrapper(
				new Map(
						DummyMetadataBuildingContext.INSTANCE, 
						persistentClassWrapper.getWrappedObject()));
	}

	public static ValueWrapper createOneToManyWrapper(PersistentClassWrapper persistentClassWrapper) {
		return createValueWrapper(
				new OneToMany(
						DummyMetadataBuildingContext.INSTANCE, 
						persistentClassWrapper.getWrappedObject()));
	}

	public static ValueWrapper createOneToOneWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToOneWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createPrimitiveArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new PrimitiveArrayWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createSetWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new SetWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createSimpleValueWrapper() {
		return new SimpleValueWrapperImpl();
	}
	
	public static ValueWrapper createComponentWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new ComponentWrapperImpl(persistentClassWrapper);
	}
	
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
	
	
	private static class OneToOneWrapperImpl extends OneToOne implements ValueWrapper {
		protected OneToOneWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, 
					persistentClassWrapper.getWrappedObject().getTable(),
					persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class PrimitiveArrayWrapperImpl extends PrimitiveArray implements ValueWrapper {
		protected PrimitiveArrayWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class SetWrapperImpl extends Set implements ValueWrapper {
		protected SetWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class SimpleValueWrapperImpl extends BasicValue implements ValueWrapper {
		protected SimpleValueWrapperImpl() {
			super(DummyMetadataBuildingContext.INSTANCE);
		}		
	}
	
	private static class ComponentWrapperImpl extends Component implements ValueWrapper {
		protected ComponentWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}
	
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
