package org.hibernate.tool.orm.jbt.api;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface CollectionMetadataWrapper extends Wrapper {
	
	default Type getElementType() { return ((CollectionPersister)getWrappedObject()).getElementType(); }

}
