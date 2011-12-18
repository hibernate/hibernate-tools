package org.hibernate.cfg.reveng;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PropertyGeneration;
import org.hibernate.mapping.Table;

public class DelegatingReverseEngineeringStrategy implements ReverseEngineeringStrategy {

	ReverseEngineeringStrategy delegate;

	public List getForeignKeys(TableIdentifier referencedTable) {
		return delegate==null?null:delegate.getForeignKeys(referencedTable);
	}

	public DelegatingReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		this.delegate = delegate;
	}

	public String columnToPropertyName(TableIdentifier table, String column) {
		return delegate==null?null:delegate.columnToPropertyName(table, column);
	}

	public PropertyGeneration columnToPropertyGeneration(TableIdentifier table, String column) {
		return delegate==null?null:delegate.columnToPropertyGeneration(table, column);
	}
	
	public boolean excludeTable(TableIdentifier ti) {
		return delegate==null?false:delegate.excludeTable(ti);
	}
	
	public boolean excludeColumn(TableIdentifier identifier, String columnName) {
		return delegate==null?false:delegate.excludeColumn(identifier, columnName);
	}

	public String foreignKeyToCollectionName(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns, boolean uniqueReference) {
		return delegate==null?null:delegate.foreignKeyToCollectionName(keyname, fromTable, fromColumns, referencedTable, referencedColumns, uniqueReference);
	}

	public String foreignKeyToEntityName(String keyname, TableIdentifier fromTable, List fromColumnNames, TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
		return delegate==null?null:delegate.foreignKeyToEntityName(keyname, fromTable, fromColumnNames, referencedTable, referencedColumnNames, uniqueReference);
	}

	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
		return delegate==null?null:delegate.columnToHibernateTypeName(table, columnName, sqlType, length, precision, scale, nullable, generatedIdentifier);
	}

	public String tableToClassName(TableIdentifier tableIdentifier) {
		return delegate==null?null:delegate.tableToClassName(tableIdentifier);
	}

	public String getTableIdentifierStrategyName(TableIdentifier tableIdentifier) {
		return delegate==null?null:delegate.getTableIdentifierStrategyName(tableIdentifier);
	}

	public Properties getTableIdentifierProperties(TableIdentifier identifier) {
		return delegate==null?null:delegate.getTableIdentifierProperties(identifier);
	}

	public List getPrimaryKeyColumnNames(TableIdentifier identifier) {
		return delegate==null?null:delegate.getPrimaryKeyColumnNames(identifier);
	}

	public String classNameToCompositeIdName(String className) {
		return delegate==null?null:delegate.classNameToCompositeIdName(className);
	}

	public void configure(ReverseEngineeringRuntimeInfo runtimeInfo) {
		if(delegate!=null) delegate.configure(runtimeInfo);		
	}

	public void close() {
		if(delegate!=null) delegate.close();
	}

	public String getOptimisticLockColumnName(TableIdentifier identifier) {
		return delegate==null?null:delegate.getOptimisticLockColumnName(identifier);		
	}

	public boolean useColumnForOptimisticLock(TableIdentifier identifier, String column) {
		return delegate==null?false:delegate.useColumnForOptimisticLock(identifier, column);
	}

	public List getSchemaSelections() {
		return delegate==null?null:delegate.getSchemaSelections();
	}

	public String tableToIdentifierPropertyName(TableIdentifier tableIdentifier) {
		return delegate==null?null:delegate.tableToIdentifierPropertyName(tableIdentifier);
	}

	public String tableToCompositeIdName(TableIdentifier identifier) {
		return delegate==null?null:delegate.tableToCompositeIdName(identifier);
	}

	public boolean excludeForeignKeyAsCollection(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		return delegate==null?false:delegate.excludeForeignKeyAsCollection(keyname, fromTable, fromColumns, referencedTable, referencedColumns);
	}

	public boolean excludeForeignKeyAsManytoOne(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		return delegate==null?false:delegate.excludeForeignKeyAsManytoOne(keyname, fromTable, fromColumns, referencedTable, referencedColumns);
	}

	public boolean isForeignKeyCollectionInverse(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		return delegate==null?true:delegate.isForeignKeyCollectionInverse(name, foreignKeyTable, columns, foreignKeyReferencedTable, referencedColumns);
	}

	public boolean isForeignKeyCollectionLazy(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		return delegate==null?true:delegate.isForeignKeyCollectionLazy(name, foreignKeyTable, columns, foreignKeyReferencedTable, referencedColumns);
	}

	/**
	 * Initialize the settings. 
	 * 
	 * If subclasses need to use the Settings then it should keep its own reference, but still remember to initialize the delegates settings by calling super.setSettings(settings).
	 * 
	 * @see ReverseEngineeringStrategy.setSettings
	 */
	public void setSettings(ReverseEngineeringSettings settings) {
		if(delegate!=null) delegate.setSettings(settings);
	}

	public boolean isManyToManyTable(Table table) {
		return delegate==null?true:delegate.isManyToManyTable( table );
	}
	
	public boolean isOneToOne(ForeignKey foreignKey) { 
		return delegate==null?true:delegate.isOneToOne( foreignKey );
    }


	public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
		return delegate==null?null:delegate.foreignKeyToManyToManyName( fromKey, middleTable, toKey, uniqueReference );
	}

	public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
		return delegate==null?null:delegate.tableToMetaAttributes( tableIdentifier );		
	}

	public Map columnToMetaAttributes(TableIdentifier identifier, String column) {
		return delegate==null?null:delegate.columnToMetaAttributes( identifier, column );
	}

	public AssociationInfo foreignKeyToAssociationInfo(ForeignKey foreignKey) {
		return delegate==null?null:delegate.foreignKeyToAssociationInfo(foreignKey);
	}
	
	public AssociationInfo foreignKeyToInverseAssociationInfo(ForeignKey foreignKey) {
		return delegate==null?null:delegate.foreignKeyToInverseAssociationInfo(foreignKey);
	}
	
	public String foreignKeyToInverseEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames,
			TableIdentifier referencedTable, List referencedColumnNames,
			boolean uniqueReference) {
		return delegate==null?null:delegate.foreignKeyToInverseEntityName(keyname, fromTable, fromColumnNames, referencedTable, referencedColumnNames, uniqueReference);
	}	
	
}
