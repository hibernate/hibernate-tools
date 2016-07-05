import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.reveng.AssociationInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.Table;

/**
 * noop naming strategy for testing validation of parameters in ant test.
 * @author max
 *
 */
public class NoopReverseEngineeringStrategy implements ReverseEngineeringStrategy {

	public String tableToClassName(TableIdentifier tableIdentifier) {
		return null;
	}

	public String columnToPropertyName(TableIdentifier table, String column) {
		return null;
	}

	public String foreignKeyToCollectionName(
			String keyname,
			TableIdentifier fromTable, 
			List<?> fromColumns, 
			TableIdentifier referencedTable, 
			List<?> referencedColumns, 
			boolean uniqueReference) {
		return null;
	}

	public String foreignKeyToEntityName(
			String keyname,
			TableIdentifier fromTable, 
			List<?> fromColumnNames, 
			TableIdentifier referencedTable, 
			List<?> referencedColumnNames, 
			boolean uniqueReference) {
		return null;
	}

	public boolean excludeTable(TableIdentifier ti) {
		return false;
	}

	public String columnToHibernateTypeName(
			TableIdentifier table, 
			String columnName, 
			int sqlType, 
			int length, 
			int precision, 
			int scale, 
			boolean nullable, 
			boolean generatedIdentifier) {
		return null;
	}

	public List<ForeignKey> getForeignKeys(TableIdentifier referencedTable) {
		return null;
	}

	public String columnToHibernateTypeName(TableIdentifier identifier, String name) {
		return null;
	}

	public String getTableIdentifierStrategyName(TableIdentifier identifier) {
		return null;
	}

	public Properties getTableIdentifierProperties(TableIdentifier identifier) {
		return null;
	}

	public List<String> getPrimaryKeyColumnNames(TableIdentifier identifier) {
		return null;
	}

	public String classNameToCompositeIdName(String className) {
		return null;
	}

	public void configure(ReverseEngineeringRuntimeInfo runtimeInfo) {}

	public void close() {}

	public String getOptimisticLockColumnName(TableIdentifier identifier) {
		return null;
	}

	public boolean useColumnForOptimisticLock(TableIdentifier identifier, String column) {
		return false;
	}

	public boolean excludeColumn(TableIdentifier identifier, String columnName) {
		return false;
	}

	public List<SchemaSelection> getSchemaSelections() {
		return null;
	}

	public String tableToIdentifierPropertyName(TableIdentifier tableIdentifier) {
		return null;
	}

	public String tableToCompositeIdName(TableIdentifier identifier) {
		return null;
	}

	public boolean excludeForeignKeyAsCollection(
			String keyname, 
			TableIdentifier fromTable, 
			List<Column> fromColumns, 
			TableIdentifier referencedTable, 
			List<Column> referencedColumns) {
		return false;
	}

	public boolean excludeForeignKeyAsManytoOne(
			String keyname, 
			TableIdentifier fromTable, 
			List<?> fromColumns, 
			TableIdentifier referencedTable, 
			List<?> referencedColumns) {
		return false;
	}

	public boolean isForeignKeyCollectionInverse(
			String name, TableIdentifier 
			foreignKeyTable, 
			List<?> columns, 
			TableIdentifier foreignKeyReferencedTable, 
			List<?> referencedColumns) {
		return false;
	}

	public boolean isForeignKeyCollectionLazy(
			String name, 
			TableIdentifier foreignKeyTable, 
			List<?> columns, 
			TableIdentifier foreignKeyReferencedTable, 
			List<?> referencedColumns) {
		return false;
	}

	public void setSettings(ReverseEngineeringSettings settings) {}

	public boolean isManyToManyTable(Table table) {
		return false;
	}

	public String foreignKeyToManyToManyName(
			ForeignKey fromKey, 
			TableIdentifier middleTable, 
			ForeignKey toKey, 
			boolean uniqueReference) {
		return null;
	}

	public Map<String,MetaAttribute> tableToMetaAttributes(
			TableIdentifier tableIdentifier) {
		return null;
	}

	public Map<String, MetaAttribute> columnToMetaAttributes(
			TableIdentifier identifier, 
			String column) {
		return null;
	}
	
	public boolean isOneToOne(ForeignKey foreignKey) {
		return false;
	}

	
	public AssociationInfo foreignKeyToAssociationInfo(ForeignKey foreignKey) {
		return null;
	}

	
	public String foreignKeyToInverseEntityName(
			String keyname,
			TableIdentifier fromTable, 
			List<?> fromColumnNames,
			TableIdentifier referencedTable, 
			List<?> referencedColumnNames,
			boolean uniqueReference) {
		return null;
	}

	
	public AssociationInfo foreignKeyToInverseAssociationInfo(ForeignKey foreignKey) {
		return null;
	}
}
