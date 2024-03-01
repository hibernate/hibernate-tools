package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.tool.orm.jbt.api.CollectionMetadataWrapper;

public class CollectionMetadataWrapperFactory {

	public static CollectionMetadataWrapper createCollectionMetadataWrapper(final CollectionPersister collectionPersister) {
		return new CollectionMetadataWrapper() {
			@Override public CollectionPersister getWrappedObject() { return collectionPersister; }
		};
	}
	
}
