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
package org.hibernate.tool.hbm2x.IncrementalSchemaReading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbmlint.detector.TableSelectorStrategy;
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
	
	private Properties properties = null;
	private String defaultSchema = null;
	private String defaultCatalog = null;
	private List<String> gottenTables = new ArrayList<String>();
		
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		properties = Environment.getProperties();
		defaultSchema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);
		defaultCatalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testReadSchemaIncremental() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		ServiceRegistry serviceRegistry = builder.build();
		TableSelectorStrategy tss = new TableSelectorStrategy(new DefaultReverseEngineeringStrategy());
		Dialect dialect = serviceRegistry.getService(JdbcServices.class).getDialect();
		MetaDataDialect mockedMetaDataDialect = createMockedMetaDataDialect(
				MetaDataDialectFactory.fromDialect(dialect));
		JDBCReader reader = JDBCReaderFactory.newJDBCReader( properties, tss, mockedMetaDataDialect, serviceRegistry);
		
		tss.addSchemaSelection( new SchemaSelection(null,null, "CHILD") );
		
		DatabaseCollector dc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		reader.readDatabaseSchema( dc, null, null );
		
		assertEquals(gottenTables.size(),1);
		assertEquals(gottenTables.get(0),"CHILD");
		
		Iterator<Table> iterator = dc.iterateTables();
		Table firstChild = iterator.next();
		assertEquals(firstChild.getName(), "CHILD");
		assertFalse(iterator.hasNext());
		
		assertFalse(firstChild.getForeignKeyIterator().hasNext(), "should not record foreignkey to table it doesn't know about yet");
		
		tss.clearSchemaSelections();
		tss.addSchemaSelection( new SchemaSelection(null, null, "MASTER") );
		
		gottenTables.clear();
		reader.readDatabaseSchema( dc, null, null );
		
		assertEquals(gottenTables.size(),1);
		assertEquals(gottenTables.get(0),"MASTER");
		
		
		iterator = dc.iterateTables();
		assertNotNull(iterator.next());
		assertNotNull(iterator.next());
		assertFalse(iterator.hasNext());
		
		Table table = dc.getTable( defaultSchema, defaultCatalog, "CHILD" );
		assertSame( firstChild, table );
		
		JUnitUtil.assertIteratorContainsExactly(
				"should have recorded one foreignkey to child table", 
				firstChild.getForeignKeyIterator(),
				1);		
		
		
		tss.clearSchemaSelections();		
		reader.readDatabaseSchema( dc, null, null );
		
		Table finalMaster = dc.getTable( defaultSchema, defaultCatalog, "MASTER" );
		
		assertSame(firstChild, dc.getTable( defaultSchema, defaultCatalog, "CHILD" ));
		JUnitUtil.assertIteratorContainsExactly(
				null,
				firstChild.getForeignKeyIterator(),
				1);
		JUnitUtil.assertIteratorContainsExactly(
				null,
				finalMaster.getForeignKeyIterator(),
				0);
	}

	private MetaDataDialect createMockedMetaDataDialect(MetaDataDialect delegate) {
		return (MetaDataDialect)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { MetaDataDialect.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("getTables".equals(method.getName())) {
							gottenTables.add((String)args[2]);
							return delegate.getTables(
									(String)args[0], 
									(String)args[1], 
									args[2] == null ? "%" : (String)args[2]);
						} else if ("getColumns".equals(method.getName())) {
							return delegate.getColumns(
									(String)args[0], 
									(String)args[1], 
									args[2] == null ? "%" : (String)args[2], 
									args[3] == null ? "%" : (String)args[3]);
						} else {
							return method.invoke(delegate, args);
							
						}
					}
				});
	}
	
}
