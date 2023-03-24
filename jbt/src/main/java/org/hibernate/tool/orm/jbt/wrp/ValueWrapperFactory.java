package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
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
	
	public static Value createArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new ArrayWrapperImpl(persistentClassWrapper);
	}

	public static Value createBagWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new BagWrapperImpl(persistentClassWrapper);
	}

	public static Value createListWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new ListWrapperImpl(persistentClassWrapper);
	}

	public static Value createManyToOneWrapper(Table table) {
		return new ManyToOneWrapperImpl(table);
	}

	public static Value createMapWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new MapWrapperImpl(persistentClassWrapper);
	}

	public static Value createOneToManyWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToManyWrapperImpl(persistentClassWrapper);
	}

	public static Value createOneToOneWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToOneWrapperImpl(persistentClassWrapper);
	}

	public static Value createPrimitiveArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new PrimitiveArrayWrapperImpl(persistentClassWrapper);
	}

	public static Value createSetWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new SetWrapperImpl(persistentClassWrapper);
	}

	public static Value createSimpleValueWrapper() {
		return new SimpleValueWrapperImpl();
	}
	
	public static Value createComponentWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new ComponentWrapperImpl(persistentClassWrapper);
	}

	static interface ValueWrapper extends Value {
		default boolean isCollection() { return Collection.class.isAssignableFrom(getClass()); }
		default boolean isOneToMany() { return OneToMany.class.isAssignableFrom(getClass()); }
		default boolean isManyToOne() { return ManyToOne.class.isAssignableFrom(getClass()); }
		default boolean isOneToOne() { return OneToOne.class.isAssignableFrom(getClass()); }
		default boolean isMap() { return Map.class.isAssignableFrom(getClass()); }
		default boolean isEmbedded() { return false; }
		default boolean isToOne() { return ToOne.class.isAssignableFrom(getClass()); }
		default Value getElement() { return null; }
		default void setElement(Value element) {}
		default Table getCollectionTable() { return null; }
		default void setCollectionTable(Table table) {}
		default Value getCollectionElement() { return getElement(); }
	}
	
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

}
