package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface NamingStrategyWrapper extends Wrapper {
	
	default String collectionTableName(
			String ownerEntity, 
			String ownerEntityTable, 
			String associatedEntity, 
			String associatedEntityTable,
			String propertyName) { 
		return ((NamingStrategy)getWrappedObject()).collectionTableName(
				ownerEntity, 
				ownerEntityTable, 
				associatedEntity, 
				associatedEntityTable, 
				propertyName);
	}
	
	default String columnName(String specifiedName) {
		return ((NamingStrategy)getWrappedObject()).columnName(specifiedName);
	}

}
