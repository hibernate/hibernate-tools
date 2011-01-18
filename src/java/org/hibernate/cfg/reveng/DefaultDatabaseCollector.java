package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;

public class DefaultDatabaseCollector extends AbstractDatabaseCollector  {

	private Map tables;		
	private Map qualifiers;

	public DefaultDatabaseCollector(MetaDataDialect metaDataDialect) {
		super(metaDataDialect);
		tables = new HashMap();
		qualifiers = new HashMap();
	}
	
	public Iterator iterateTables() {
		return tables.values().iterator();
	}

	public Table addTable(String schema, 
			String catalog, 
			String name) {
		
        String key = Table.qualify(quote(catalog), quote(schema), quote(name));
		Table table = (Table) tables.get(key);
		
		if (table == null) {
			table = new Table();
			table.setAbstract(false);
			table.setName(name);
			table.setSchema(schema);
			table.setCatalog(catalog);
			tables.put(key, table);
			
			String qualifier = StringHelper.qualifier(key);
			List schemaList = (List) qualifiers.get(qualifier);
			if(schemaList==null) {
				schemaList = new ArrayList();
				qualifiers.put(qualifier, schemaList);				
			}
			schemaList.add(table);
		}
		else {
			table.setAbstract(false);
		}
		
		return table;
	}

	public Table getTable(String schema, String catalog, String name) {
        String key = Table.qualify(quote(catalog), quote(schema), quote(name));
		return (Table) tables.get(key);
	}

	public Iterator getQualifierEntries() {
		return qualifiers.entrySet().iterator();
	}
	
	
}
