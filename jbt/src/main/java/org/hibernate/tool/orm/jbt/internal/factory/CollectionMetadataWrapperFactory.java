package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.tool.orm.jbt.api.wrp.CollectionMetadataWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;
import org.hibernate.type.Type;

public class CollectionMetadataWrapperFactory {

	public static CollectionMetadataWrapper createCollectionMetadataWrapper(final CollectionPersister collectionPersister) {
		return new CollectionMetadataWrapperImpl(collectionPersister);
	}
	
	private static class CollectionMetadataWrapperImpl
			extends AbstractWrapper
			implements CollectionMetadataWrapper {
		
		private CollectionPersister wrappedCollectionMetadata = null;
		
		private TypeWrapper elementTypeWrapper = null;
		
		private CollectionMetadataWrapperImpl(CollectionPersister collectionPersister) {
			wrappedCollectionMetadata = collectionPersister;
		}
		
		@Override 
		public CollectionPersister getWrappedObject() { 
			return wrappedCollectionMetadata; 
		}
		
		@Override 
		public TypeWrapper getElementType() { 
			Type elementType = getWrappedObject().getElementType();
			if (elementTypeWrapper == null || elementTypeWrapper.getWrappedObject() != elementType) {
				elementTypeWrapper = TypeWrapperFactory.createTypeWrapper(elementType);
			}
			return elementTypeWrapper; 
		}
	}
	
}
