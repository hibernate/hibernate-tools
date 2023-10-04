package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.mapping.Table;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.wrp.DatabaseReaderWrapperFactory.DatabaseReaderWrapper;
import org.hibernate.tool.orm.jbt.wrp.DatabaseReaderWrapperFactory.DatabaseReaderWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseReaderWrapperFactoryTest {
	
	private DatabaseReaderWrapper databaseReaderWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		databaseReaderWrapper = DatabaseReaderWrapperFactory
				.createDatabaseReaderWrapper(
						properties, 
						new DefaultStrategy());
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(databaseReaderWrapper);
		assertTrue(databaseReaderWrapper instanceof DatabaseReaderWrapperImpl);
		assertNotNull(((DatabaseReaderWrapperImpl)databaseReaderWrapper).databaseReader);
		assertNotNull(((DatabaseReaderWrapperImpl)databaseReaderWrapper).revengMetadataCollector);
	}
	
	@Test
	public void testCollectDatabaseTables() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		Map<String, List<TableWrapper>> tableWrapperMap = databaseReaderWrapper.collectDatabaseTables();
		assertEquals(2, tableWrapperMap.size());
		Set<String> tableMapKeys = tableWrapperMap.keySet();
		assertTrue(tableMapKeys.contains("TEST.PUBLIC"));
		assertTrue(tableMapKeys.contains("TEST.INFORMATION_SCHEMA"));
		List<TableWrapper> tableWrappers = tableWrapperMap.get("TEST.PUBLIC");
		assertEquals(1, tableWrappers.size());
		TableWrapper tableWrapper = tableWrappers.get(0);
		assertEquals("TEST", tableWrapper.getCatalog());
		assertEquals("PUBLIC", tableWrapper.getSchema());
		assertEquals("FOO", tableWrapper.getName());
		assertTrue(tableWrapper.getColumnIterator().hasNext());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}

}
