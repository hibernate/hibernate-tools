package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;

public class ValueWrapperFactory {
	
	public static Value createArrayWrapper(PersistentClassWrapper persistentClassWrapper) {
		return new Array(DummyMetadataBuildingContext.INSTANCE, persistentClassWrapper.getWrappedObject());
	}

}
