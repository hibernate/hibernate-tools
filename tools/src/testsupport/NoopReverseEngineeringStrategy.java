import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.reveng.AssociationInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

/**
 * noop naming strategy for testing validation of parameters in ant test.
 * @author max
 *
 */
public class NoopReverseEngineeringStrategy implements ReverseEngineeringStrategy {

	public String tableToClassName(TableIdentifier tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public String columnToPropertyName(TableIdentifier table, String column) {
		// TODO Auto-generated method stub
		return null;
	}

	public String foreignKeyToCollectionName(String keyname,
			TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns, boolean uniqueReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public String foreignKeyToEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames, TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean excludeTable(TableIdentifier ti) {
		// TODO Auto-generated method stub
		return false;
	}

	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getForeignKeys(TableIdentifier referencedTable) {
		// TODO Auto-generated method stub
		return null;
	}

	public String columnToHibernateTypeName(TableIdentifier identifier, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTableIdentifierStrategyName(TableIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getTableIdentifierProperties(TableIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPrimaryKeyColumnNames(TableIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public String classNameToCompositeIdName(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	public void configure(ReverseEngineeringRuntimeInfo runtimeInfo) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public String getOptimisticLockColumnName(TableIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean useColumnForOptimisticLock(TableIdentifier identifier, String column) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean excludeColumn(TableIdentifier identifier, String columnName) {
		// TODO Auto-generated method stub
		return false;
	}

	public List getSchemaSelections() {
		// TODO Auto-generated method stub
		return null;
	}

	public String tableToIdentifierPropertyName(TableIdentifier tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public String tableToCompositeIdName(TableIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean excludeForeignKeyAsCollection(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean excludeForeignKeyAsManytoOne(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isForeignKeyCollectionInverse(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isForeignKeyCollectionLazy(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSettings(ReverseEngineeringSettings settings) {
		// TODO Auto-generated method stub
		
	}

	public boolean isManyToManyTable(Table table) {
		// TODO Auto-generated method stub
		return false;
	}

	public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map columnToMetaAttributes(TableIdentifier identifier, String column) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isOneToOne(ForeignKey foreignKey) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public AssociationInfo foreignKeyToAssociationInfo(ForeignKey foreignKey) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String foreignKeyToInverseEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames,
			TableIdentifier referencedTable, List referencedColumnNames,
			boolean uniqueReference) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public AssociationInfo foreignKeyToInverseAssociationInfo(ForeignKey foreignKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
