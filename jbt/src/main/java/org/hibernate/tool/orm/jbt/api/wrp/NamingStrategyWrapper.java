package org.hibernate.tool.orm.jbt.api.wrp;

public interface NamingStrategyWrapper extends Wrapper {
	
	String collectionTableName(
			String ownerEntity, 
			String ownerEntityTable, 
			String associatedEntity, 
			String associatedEntityTable,
			String propertyName);
	
	String columnName(String name);
	
	String propertyToColumnName(String name);
	
	String tableName(String name);
	
	String joinKeyColumnName(
			String primaryKeyColumnName,
			String primaryTableName);
	
	String classToTableName(String name);
	
	String getStrategyClassName();

}
