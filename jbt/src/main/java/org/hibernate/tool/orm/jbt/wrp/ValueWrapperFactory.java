package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.List;
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

}
