package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface CollectionMetadataWrapper extends Wrapper {
	
	TypeWrapper getElementType();

}
