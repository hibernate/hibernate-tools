/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.IncrementalSchemaReading;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.reveng.RevengDialect;
import org.hibernate.tool.api.reveng.RevengStrategy.SchemaSelection;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.RevengMetadataCollector;
import org.hibernate.tool.internal.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.tool.internal.reveng.reader.DatabaseReader;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.TableSelectorStrategy;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private Properties properties = null;
	private String defaultSchema = null;
	private String defaultCatalog = null;
		
	public class MockedMetaDataDialect extends JDBCMetaDataDialect {
		List<String> gottenTables = new ArrayList<String>();
		public Iterator<Map<String, Object>> getTables(String catalog, String schema, String table) {
			gottenTables.add(table);
			return super.getTables( catalog, schema, table == null ? "%" : table );
		}	
		public Iterator<Map<String, Object>> getColumns(String catalog, String schema, String table, String column) {
			return super.getColumns(catalog, schema, table == null ? "%" : table, column == null ? "%" : column);
		}
	}	
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		properties = Environment.getProperties();
		defaultSchema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);
		defaultCatalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testReadSchemaIncremental() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		ServiceRegistry serviceRegistry = builder.build();
		TableSelectorStrategy tss = new TableSelectorStrategy(new DefaultStrategy());
		MockedMetaDataDialect mockedMetaDataDialect = new MockedMetaDataDialect();
		DatabaseReader reader = DatabaseReader.create( properties, tss, mockedMetaDataDialect, serviceRegistry);
		
		tss.addSchemaSelection( createSchemaSelection(null,null, "CHILD") );
		
		RevengMetadataCollector dc = new RevengMetadataCollector();
		reader.readDatabaseSchema(dc);
		
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.size(),1);
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.get(0),"CHILD");
		
		Iterator<Table> iterator = dc.iterateTables();
		Table firstChild = iterator.next();
		Assert.assertEquals(firstChild.getName(), "CHILD");
		Assert.assertFalse(iterator.hasNext());
		
		Assert.assertFalse("should not record foreignkey to table it doesn't know about yet",firstChild.getForeignKeyIterator().hasNext());
		
		tss.clearSchemaSelections();
		tss.addSchemaSelection( createSchemaSelection(null, null, "MASTER") );
		
		mockedMetaDataDialect.gottenTables.clear();
		reader.readDatabaseSchema(dc);
		
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.size(),1);
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.get(0),"MASTER");
		
		
		iterator = dc.iterateTables();
		Assert.assertNotNull(iterator.next());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		
		Table table = getTable(dc, mockedMetaDataDialect, defaultCatalog, defaultSchema, "CHILD" );
		Assert.assertSame( firstChild, table );
		
		JUnitUtil.assertIteratorContainsExactly(
				"should have recorded one foreignkey to child table", 
				firstChild.getForeignKeyIterator(),
				1);		
		
		
		tss.clearSchemaSelections();		
		reader.readDatabaseSchema(dc);
		
		Table finalMaster = getTable(dc, mockedMetaDataDialect, defaultCatalog, defaultSchema, "MASTER" );
		
		Assert.assertSame(firstChild, getTable(dc, mockedMetaDataDialect, defaultCatalog, defaultSchema, "CHILD" ));
		JUnitUtil.assertIteratorContainsExactly(
				null,
				firstChild.getForeignKeyIterator(),
				1);
		JUnitUtil.assertIteratorContainsExactly(
				null,
				finalMaster.getForeignKeyIterator(),
				0);
	}

	private Table getTable(
			RevengMetadataCollector revengMetadataCollector, 
			RevengDialect metaDataDialect, 
			String catalog, 
			String schema, 
			String name) {
		return revengMetadataCollector.getTable(
				TableIdentifier.create(
						quote(metaDataDialect, catalog), 
						quote(metaDataDialect, schema), 
						quote(metaDataDialect, name)));
	}
 	
	private String quote(RevengDialect metaDataDialect, String name) {
		if (name == null)
			return name;
		if (metaDataDialect.needQuote(name)) {
			if (name.length() > 1 && name.charAt(0) == '`'
					&& name.charAt(name.length() - 1) == '`') {
				return name; // avoid double quoting
			}
			return "`" + name + "`";
		} else {
			return name;
		}
	}

	private SchemaSelection createSchemaSelection(String matchCatalog, String matchSchema, String matchTable) {
		return new SchemaSelection() {
			@Override
			public String getMatchCatalog() {
				return matchCatalog;
			}
			@Override
			public String getMatchSchema() {
				return matchSchema;
			}
			@Override
			public String getMatchTable() {
				return matchTable;
			}		
		};
	}
	
	
}
