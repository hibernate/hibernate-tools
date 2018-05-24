/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.CachedMetaData;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.dialect.MetaDataDialect;
import org.hibernate.tool.api.dialect.MetaDataDialectFactory;
import org.hibernate.tool.api.reveng.DatabaseCollector;
import org.hibernate.tool.api.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.tool.internal.dialect.CachedMetaDataDialect;
import org.hibernate.tool.internal.metadata.DefaultDatabaseCollector;
import org.hibernate.tool.internal.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.tool.internal.reveng.JDBCReader;
import org.hibernate.tool.internal.reveng.JdbcReaderFactory;
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

	public class MockedMetaDataDialect implements MetaDataDialect {

		MetaDataDialect delegate;
		private boolean failOnDelegateAccess;

		public MockedMetaDataDialect(MetaDataDialect realMetaData) {
			delegate = realMetaData;
		}

		public void close() {
			delegate.close();
		}

		public void close(Iterator<?> iterator) {
			delegate.close( iterator );
		}

		public void configure(ReverseEngineeringRuntimeInfo info) {
			delegate.configure(info);			
		}
		
		public Iterator<Map<String, Object>> getColumns(String catalog, String schema, String table, String column) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getColumns( catalog, schema, table, column );
			}
		}

		public Iterator<Map<String, Object>> getExportedKeys(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getExportedKeys( catalog, schema, table );
			}
		}

		public Iterator<Map<String, Object>> getIndexInfo(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getIndexInfo( catalog, schema, table );
			}
		}

		public Iterator<Map<String, Object>> getPrimaryKeys(String catalog, String schema, String name) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getPrimaryKeys( catalog, schema, name );
			}
		}

		public Iterator<Map<String, Object>> getTables(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getTables( catalog, schema, table );
			}
		}

		public boolean needQuote(String name) {
			return delegate.needQuote( name );
		}

		public void setDelegate(Object object) {
			this.delegate = null;			
		}

		public void setFailOnDelegateAccess(boolean b) {
			failOnDelegateAccess = b;			
		}

		public Iterator<Map<String, Object>> getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String name) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getSuggestedPrimaryKeyStrategyName(catalog, schema, name);
			}
		}
		
	}
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testCachedDialect() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		ServiceRegistry serviceRegistry = builder.build();		
		Properties properties = Environment.getProperties();
		MetaDataDialect realMetaData = MetaDataDialectFactory.createMetaDataDialect( 
				serviceRegistry.getService(JdbcServices.class).getDialect(), 
				Environment.getProperties() );
		MockedMetaDataDialect mock = new MockedMetaDataDialect(realMetaData);
		CachedMetaDataDialect dialect = new CachedMetaDataDialect(mock);
		JDBCReader reader = JdbcReaderFactory.newJDBCReader( 
				properties, 
				new DefaultReverseEngineeringStrategy(), 
				dialect, 
				serviceRegistry );
		DatabaseCollector dc = new DefaultDatabaseCollector(
				reader.getMetaDataDialect());
		reader.readDatabaseSchema( 
				dc, 
				properties.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				properties.getProperty(AvailableSettings.DEFAULT_SCHEMA) );
		validate( dc );				
		mock.setFailOnDelegateAccess(true);	
		reader = JdbcReaderFactory.newJDBCReader( 
				properties, 
				new DefaultReverseEngineeringStrategy(), 
				dialect, 
				serviceRegistry );
		dc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		reader.readDatabaseSchema( 
				dc, 
				properties.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				properties.getProperty(AvailableSettings.DEFAULT_SCHEMA) );
		validate(dc);
	}

	private void validate(DatabaseCollector dc) {
		Iterator<Table> iterator = dc.iterateTables();
		Table table = iterator.next();
		Table master = null, child = null;
		if ("MASTER".equals(table.getName())) {
			master = table;
			child = iterator.next();
		} else if ("CHILD".equals(table.getName())) {
			child = table;
			master = iterator.next();
		} else {
			Assert.fail("Only tables named 'MASTER' and 'CHILD' should exist");
		}
		Assert.assertNotNull(child);
		Assert.assertNotNull(master);
		
		iterator = dc.iterateTables();
		Assert.assertNotNull(iterator.next());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());		
		
		JUnitUtil.assertIteratorContainsExactly(
				"should have recorded one foreignkey to child table",  
				child.getForeignKeyIterator(),
				1);
	}

}
