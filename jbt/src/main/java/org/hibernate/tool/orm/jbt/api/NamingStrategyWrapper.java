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
	
	default String columnName(String name) {
		return ((NamingStrategy)getWrappedObject()).columnName(name);
	}
	
	default String propertyToColumnName(String name) {
		return ((NamingStrategy)getWrappedObject()).propertyToColumnName(name);
	}
	
	default String tableName(String name) {
		return ((NamingStrategy)getWrappedObject()).tableName(name);
	}
	
	default String joinKeyColumnName(
			String primaryKeyColumnName,
			String primaryTableName) {
		return ((NamingStrategy)getWrappedObject()).joinKeyColumnName(
				primaryKeyColumnName,
				primaryTableName);
		
	}

}
