/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.cfg.DriverMetaData;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.dialect.MetaDataDialect;
import org.hibernate.tool.api.dialect.MetaDataDialectFactory;
import org.hibernate.tool.api.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.tool.internal.dialect.JDBCMetaDataDialect;
import org.hibernate.tool.internal.metadata.DefaultDatabaseCollector;
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
	private ServiceRegistry serviceRegistry;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		properties = Environment.getProperties();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		serviceRegistry = ssrb.build();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testExportedKeys() {	
		MetaDataDialect dialect = new JDBCMetaDataDialect();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ServiceRegistry serviceRegistry = ssrb.build();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		ConnectionProvider connectionProvider = 
				serviceRegistry.getService(ConnectionProvider.class);			
		dialect.configure(
				ReverseEngineeringRuntimeInfo.createInstance(
						connectionProvider,
						jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(), 
						new DefaultDatabaseCollector(dialect)));		
		String catalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
		String schema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);		
		Iterator<Map<String,Object>> tables = 
				dialect.getTables(
						catalog, 
						schema, 
						"TAB_MASTER"); 		
		boolean foundMaster = false;
		while(tables.hasNext()) {
			Map<?,?> map = (Map<?,?>) tables.next();		
			String tableName = (String) map.get("TABLE_NAME");
			String schemaName = (String) map.get("TABLE_SCHEM");
	        String catalogName = (String) map.get("TABLE_CAT");        
	        if(tableName.equals("TAB_MASTER")) {
				foundMaster = true;
				Iterator<?> exportedKeys = 
						dialect.getExportedKeys(
								catalogName, 
								schemaName, 
								tableName );
				int cnt = 0;
				while ( exportedKeys.hasNext() ) {
					exportedKeys.next();
					cnt++;
				}
				Assert.assertEquals(1,cnt);
			}
		}	
		Assert.assertTrue(foundMaster);
	}

	@Test
	public void testDataType() {	
		MetaDataDialect dialect = MetaDataDialectFactory
				.fromDialectName(properties.getProperty(AvailableSettings.DIALECT));
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		ConnectionProvider connectionProvider = 
				serviceRegistry.getService(ConnectionProvider.class);	
		dialect.configure(
				ReverseEngineeringRuntimeInfo.createInstance(
						connectionProvider,
						jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(), 
						new DefaultDatabaseCollector(dialect)));		
		String catalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
		String schema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);		
		Iterator<?> tables = 
				dialect.getColumns( 
						catalog, 
						schema, 
						"test", 
						null ); 
		while(tables.hasNext()) {
			Map<?,?> map = (Map<?,?>) tables.next();			
			System.out.println(map);			
		}
	}
	
	@Test
	public void testCaseTest() {
		MetaDataDialect dialect = new JDBCMetaDataDialect();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		ConnectionProvider connectionProvider = 
				serviceRegistry.getService(ConnectionProvider.class);
		dialect.configure( 
				ReverseEngineeringRuntimeInfo.createInstance(
						connectionProvider,
						jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(), 
						new DefaultDatabaseCollector(dialect)));
		String catalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
		String schema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);		
		Iterator<Map<String, Object>> tables = 
				dialect.getTables(
						catalog, 
						schema, 
						"TAB_MASTER");	
		
		JUnitUtil.assertIteratorContainsExactly(null, tables, 1);
	}

}
