package org.hibernate.cfg.reveng;

import java.util.Iterator;

import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;

public class MappingsDatabaseCollector extends AbstractDatabaseCollector {

	private final Mappings mappings;
	
	public MappingsDatabaseCollector(Mappings mappings, MetaDataDialect metaDataDialect) {
		super(metaDataDialect);
		this.mappings = mappings;
	}

	public Iterator iterateTables() {
		return mappings.iterateTables();
	}

	public Table addTable(String schema, String catalog, String name) {
		//pass catalog as it is as Table#setCatalog(catalog) doesn't do unquote
		return mappings.addTable(quote(schema), catalog, quote(name), null, false);
	}

	public Table getTable(String schema, String catalog, String name) {
		return mappings.getTable(quote(schema), catalog, quote(name));
	}

}
