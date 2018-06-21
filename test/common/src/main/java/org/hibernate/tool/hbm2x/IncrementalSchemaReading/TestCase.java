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
import org.hibernate.tool.api.reveng.DatabaseCollector;
import org.hibernate.tool.api.reveng.SchemaSelection;
import org.hibernate.tool.internal.dialect.JDBCMetaDataDialect;
import org.hibernate.tool.internal.metadata.DefaultDatabaseCollector;
import org.hibernate.tool.internal.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.tool.internal.reveng.JDBCReader;
import org.hibernate.tool.internal.reveng.JdbcReaderFactory;
import org.hibernate.tool.internal.reveng.TableSelectorStrategy;
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
		TableSelectorStrategy tss = new TableSelectorStrategy(new DefaultReverseEngineeringStrategy());
		MockedMetaDataDialect mockedMetaDataDialect = new MockedMetaDataDialect();
		JDBCReader reader = JdbcReaderFactory.newJDBCReader( properties, tss, mockedMetaDataDialect, serviceRegistry);
		
		tss.addSchemaSelection( new SchemaSelection(null,null, "CHILD") );
		
		DatabaseCollector dc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		reader.readDatabaseSchema( dc, null, null );
		
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.size(),1);
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.get(0),"CHILD");
		
		Iterator<Table> iterator = dc.iterateTables();
		Table firstChild = iterator.next();
		Assert.assertEquals(firstChild.getName(), "CHILD");
		Assert.assertFalse(iterator.hasNext());
		
		Assert.assertFalse("should not record foreignkey to table it doesn't know about yet",firstChild.getForeignKeyIterator().hasNext());
		
		tss.clearSchemaSelections();
		tss.addSchemaSelection( new SchemaSelection(null, null, "MASTER") );
		
		mockedMetaDataDialect.gottenTables.clear();
		reader.readDatabaseSchema( dc, null, null );
		
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.size(),1);
		Assert.assertEquals(mockedMetaDataDialect.gottenTables.get(0),"MASTER");
		
		
		iterator = dc.iterateTables();
		Assert.assertNotNull(iterator.next());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		
		Table table = dc.getTable( defaultSchema, defaultCatalog, "CHILD" );
		Assert.assertSame( firstChild, table );
		
		JUnitUtil.assertIteratorContainsExactly(
				"should have recorded one foreignkey to child table", 
				firstChild.getForeignKeyIterator(),
				1);		
		
		
		tss.clearSchemaSelections();		
		reader.readDatabaseSchema( dc, null, null );
		
		Table finalMaster = dc.getTable( defaultSchema, defaultCatalog, "MASTER" );
		
		Assert.assertSame(firstChild, dc.getTable( defaultSchema, defaultCatalog, "CHILD" ));
		JUnitUtil.assertIteratorContainsExactly(
				null,
				firstChild.getForeignKeyIterator(),
				1);
		JUnitUtil.assertIteratorContainsExactly(
				null,
				finalMaster.getForeignKeyIterator(),
				0);
	}

}
