package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;

public class ValueWrapperFactory {
	
	public static Value createArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new Array(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createBagWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new Bag(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createListWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new List(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createManyToOneWrapper(Table table) {
		return new ManyToOne(DummyMetadataBuildingContext.INSTANCE, table);
	}

	public static Value createMapWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new Map(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createOneToManyWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToMany(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createOneToOneWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new OneToOne(
				DummyMetadataBuildingContext.INSTANCE, 
				persistentClassWrapper.getWrappedObject().getTable(),
				persistentClassWrapper.getWrappedObject());
	}

	public static Value createPrimitiveArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new PrimitiveArray(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createSetWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new Set(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

	public static Value createSimpleValue() {
		return new BasicValue(DummyMetadataBuildingContext.INSTANCE);
	}

}
