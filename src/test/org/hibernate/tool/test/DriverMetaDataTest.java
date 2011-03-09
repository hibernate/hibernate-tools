package org.hibernate.tool.test;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;


/**
 * Various tests to validate the "sanity" of the jdbc drivers meta data implementation.
 * 
 * @author Max Rydahl Andersen
 *
 */
public class DriverMetaDataTest extends JDBCMetaDataBinderTestCase {

protected String[] getCreateSQL() {
		
	return new String[] {
				"create table tab_master ( id char not null, name varchar(20), primary key (id) )",
				"create table tab_child  ( childid character not null, masterref character, primary key (childid), foreign key (masterref) references tab_master(id) )",
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {				
				"drop table tab_child",
				"drop table tab_master",					
		};
	}

	public void testExportedKeys() {
	
		MetaDataDialect dialect = new JDBCMetaDataDialect();
		
		Settings settings = cfg.buildSettings();
		
		dialect.configure( ReverseEngineeringRuntimeInfo.createInstance(settings.getConnectionProvider(), settings.getSQLExceptionConverter(), new DefaultDatabaseCollector(dialect)));
		
		Iterator tables = dialect.getTables( settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), identifier("tab_master") ); 
		
		boolean foundMaster = false;
		while(tables.hasNext()) {
			Map map = (Map) tables.next();
			
			String tableName = (String) map.get("TABLE_NAME");
			String schemaName = (String) map.get("TABLE_SCHEM");
	        String catalogName = (String) map.get("TABLE_CAT");
	        
	        if(tableName.equals(identifier("tab_master"))) {
				foundMaster = true;
				Iterator exportedKeys = dialect.getExportedKeys( catalogName, schemaName, tableName );
				int cnt = 0;
				while ( exportedKeys.hasNext() ) {
					Map element = (Map) exportedKeys.next();
					cnt++;
				}
				assertEquals(1,cnt);
			/*	assertEquals(schemaName, settings.getDefaultSchemaName());
				assertEquals(catalogName, settings.getDefaultCatalogName());*/
			}
		}
		
		assertTrue(foundMaster);
	}

	public void testDataType() {
		
		MetaDataDialect dialect = new JDBCMetaDataDialect();
		
		Settings settings = cfg.buildSettings();
		
		dialect.configure( ReverseEngineeringRuntimeInfo.createInstance(settings.getConnectionProvider(), settings.getSQLExceptionConverter(), new DefaultDatabaseCollector(dialect)));
		
		Iterator tables = dialect.getColumns( settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), "test", null ); 
		
		
		while(tables.hasNext()) {
			Map map = (Map) tables.next();
			
			System.out.println(map);
			
		}
	}
	
	public void testCaseTest() {
		
	
		MetaDataDialect dialect = new JDBCMetaDataDialect();
		
		Settings settings = cfg.buildSettings();
		
		dialect.configure( ReverseEngineeringRuntimeInfo.createInstance(settings.getConnectionProvider(), settings.getSQLExceptionConverter(), new DefaultDatabaseCollector(dialect)));
		
		Iterator tables = dialect.getTables( settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), identifier( "TAB_MASTER"));
		
		assertHasNext( 1,	tables );
		
		
		
	}

	
}
