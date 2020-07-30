package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.Table;

public class RevEngUtils {

    public static final String CLASS_DB_DESCRIPTION = "class-db-description";
    public static final String FIELD_DB_DESCRIPTION = "field-db-description";

    public static List<String> getPrimaryKeyInfoInRevengStrategy(
			ReverseEngineeringStrategy revengStrat, 
			Table table, 
			String defaultCatalog, 
			String defaultSchema) {
		List<String> result = null;
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		result = revengStrat.getPrimaryKeyColumnNames(tableIdentifier);
		if (result == null) {
			String catalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
			String schema = getSchemaForModel(table.getSchema(), defaultSchema);
			tableIdentifier = new TableIdentifier(catalog, schema, table.getName());
			result = revengStrat.getPrimaryKeyColumnNames(tableIdentifier);
		}
		return result;
	}
	
	public static String getTableIdentifierStrategyNameInRevengStrategy(
			ReverseEngineeringStrategy revengStrat, 
			Table table, 
			String defaultCatalog, 
			String defaultSchema) {
		String result = null;
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		result = revengStrat.getTableIdentifierStrategyName(tableIdentifier);
		if (result == null) {
			String catalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
			String schema = getSchemaForModel(table.getSchema(), defaultSchema);
			tableIdentifier = new TableIdentifier(catalog, schema, table.getName());
			result = revengStrat.getTableIdentifierStrategyName(tableIdentifier);
		}
		return result;	
	}

	public static Map<String,MetaAttribute> getColumnToMetaAttributesInRevengStrategy(
			ReverseEngineeringStrategy revengStrat,
			Table table,
			String defaultCatalog,
			String defaultSchema,
			String column) {
		Map<String,MetaAttribute> result = null;
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		result = revengStrat.columnToMetaAttributes(tableIdentifier, column);
		if (result == null) {
			String catalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
			String schema = getSchemaForModel(table.getSchema(), defaultSchema);
			tableIdentifier = new TableIdentifier(catalog, schema, table.getName());
			result = revengStrat.columnToMetaAttributes(tableIdentifier, column);
		}

    Column col = table.getColumn(new Column(column));
    return putMetaAttributeDbDescription(result, FIELD_DB_DESCRIPTION, col == null ? null : col.getComment());
	}
	
	public static Map<String,MetaAttribute> getTableToMetaAttributesInRevengStrategy(
			ReverseEngineeringStrategy revengStrat,
			Table table,
			String defaultCatalog,
			String defaultSchema) {
		Map<String,MetaAttribute> result = null;
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		result = revengStrat.tableToMetaAttributes(tableIdentifier);
		if (result == null) {
			String catalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
			String schema = getSchemaForModel(table.getSchema(), defaultSchema);
			tableIdentifier = new TableIdentifier(catalog, schema, table.getName());
			result = revengStrat.tableToMetaAttributes(tableIdentifier);
		}

     return putMetaAttributeDbDescription(result, CLASS_DB_DESCRIPTION, table.getComment());
	}

    private static Map<String,MetaAttribute> putMetaAttributeDbDescription(Map<String, MetaAttribute> map,
                                                                           String metaAttributeName, String description) {
        if (metaAttributeName != null && description != null) {
            if (map == null) {
                map = new HashMap<>();
            }
            List<SimpleMetaAttribute> comment = new ArrayList<>();
            comment.add(new SimpleMetaAttribute(description, true));
            map.put(metaAttributeName, MetaAttributeBinder.toRealMetaAttribute(metaAttributeName, comment));
        }
        return map;
    }

    public static String getColumnToPropertyNameInRevengStrategy(
			ReverseEngineeringStrategy revengStrat,
			Table table,
			String defaultCatalog,
			String defaultSchema,
			String columnName) {
		String result = null;
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		result = revengStrat.columnToPropertyName(tableIdentifier, columnName);
		if (result == null) {
			String catalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
			String schema = getSchemaForModel(table.getSchema(), defaultSchema);
			tableIdentifier = new TableIdentifier(catalog, schema, table.getName());
			result = revengStrat.columnToPropertyName(tableIdentifier, columnName);
		}
		return result;
	}

	public static TableIdentifier createTableIdentifier(
			Table table, 
			String defaultCatalog, 
			String defaultSchema) {
		String tableName = table.getName();
		String tableCatalog = getCatalogForModel(table.getCatalog(), defaultCatalog);
		String tableSchema = getSchemaForModel(table.getSchema(), defaultSchema);
		return new TableIdentifier(tableCatalog, tableSchema, tableName);
	}

	/** If catalog is equal to defaultCatalog then we return null so it will be null in the generated code. */
	private static String getCatalogForModel(String catalog, String defaultCatalog) {
		if(catalog==null) return null;
		if(catalog.equals(defaultCatalog)) return null;
		return catalog;
	}

	/** If catalog is equal to defaultSchema then we return null so it will be null in the generated code. */
	private static String getSchemaForModel(String schema, String defaultSchema) {
		if(schema==null) return null;
		if(schema.equals(defaultSchema)) return null;
		return schema;
	}
	
}
