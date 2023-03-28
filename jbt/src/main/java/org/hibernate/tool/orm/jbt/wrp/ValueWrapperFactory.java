package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
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
		return new ArrayWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createBagWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new BagWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createListWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new ListWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createManyToOneWrapper(Table table) {
		return new ManyToOneWrapperImpl(table);
	}

	public static ValueWrapper createMapWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new MapWrapperImpl(persistentClassWrapper);
	}

	public static ValueWrapper createOneToManyWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToManyWrapperImpl(persistentClassWrapper);
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
	
	
	private static class ArrayWrapperImpl extends Array implements ValueWrapper {
		protected ArrayWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class BagWrapperImpl extends Bag implements ValueWrapper {
		protected BagWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class ListWrapperImpl extends List implements ValueWrapper {
		protected ListWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class ManyToOneWrapperImpl extends ManyToOne implements ValueWrapper {
		protected ManyToOneWrapperImpl(Table table) {
			super(DummyMetadataBuildingContext.INSTANCE, table);
		}		
	}

	private static class MapWrapperImpl extends Map implements ValueWrapper {
		protected MapWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

	private static class OneToManyWrapperImpl extends OneToMany implements ValueWrapper {
		protected OneToManyWrapperImpl(PersistentClassWrapper persistentClassWrapper) {
			super(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
		}		
	}

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
	
	private static class ValueExtensionImpl implements ValueExtension {
		
		private Value extendedValue = null;
		
		public ValueExtensionImpl(Value value) {
			extendedValue = value;
		}
		
		@Override
		public Value getWrappedObject() {
			return extendedValue;
		}

	}
	
	private static class ValueWrapperInvocationHandler implements InvocationHandler {
		
		private ValueExtensionImpl valueTarget = null;
		
		public ValueWrapperInvocationHandler(Value value) {
			this.valueTarget = new ValueExtensionImpl(value);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (isMethodSupportedByValueClass(method)) {
				return method.invoke(valueTarget.getWrappedObject(), args);
			} else {
				return method.invoke(valueTarget, args);
			}
		}
		
		private boolean isMethodSupportedByValueClass(Method method) {
			try {
				valueTarget
					.getWrappedObject()
					.getClass()
					.getMethod(method.getName(), method.getParameterTypes());
				return true;
			} catch (NoSuchMethodException e) {
				return false;
			}
		}
		
	}
	
}
