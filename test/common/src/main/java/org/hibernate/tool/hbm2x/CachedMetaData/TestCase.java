/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.hbm2x.CachedMetaData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.MetaDataDialectFactory;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.CachedMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@AfterEach
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
		JDBCReader reader = JDBCReaderFactory.newJDBCReader( 
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
		reader = JDBCReaderFactory.newJDBCReader( 
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
			fail("Only tables named 'MASTER' and 'CHILD' should exist");
		}
		assertNotNull(child);
		assertNotNull(master);
		
		iterator = dc.iterateTables();
		assertNotNull(iterator.next());
		assertNotNull(iterator.next());
		assertFalse(iterator.hasNext());		
		
		JUnitUtil.assertIteratorContainsExactly(
				"should have recorded one foreignkey to child table",  
				child.getForeignKeyIterator(),
				1);
	}

}
