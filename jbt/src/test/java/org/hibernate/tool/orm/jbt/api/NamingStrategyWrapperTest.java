package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.NamingStrategyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamingStrategyWrapperTest {

	private NamingStrategyWrapper namingStrategyWrapper = null;
	private NamingStrategy wrappedNamingStrategy = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedNamingStrategy = new TestNamingStrategy();
		namingStrategyWrapper = NamingStrategyWrapperFactory.createNamingStrategyWrapper(wrappedNamingStrategy);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedNamingStrategy);
		assertNotNull(namingStrategyWrapper);
	}
	
	@Test
	public void testCollectionTableName() {
		String tableName = namingStrategyWrapper.collectionTableName(
				"FooEntity", 
				"FooTable", 
				"BarEntity", 
				"BarTable", 
				"FooBarProperty");
		assertEquals("FooBarCollectionTableName", tableName);
	}
	
	@Test
	public void testColumnName() {
		assertEquals("FooBarColumnName", namingStrategyWrapper.columnName("foo"));
	}
	
	@Test
	public void testPropertyToColumnName() {
		assertEquals("BarFooPropertyColumn", namingStrategyWrapper.propertyToColumnName("bar"));
	}
	
	@Test
	public void testTableName() {
		assertEquals("BarFooTable", namingStrategyWrapper.tableName("foobar"));
	}
	
	@Test
	public void testJoinKeyColumnName() {
		assertEquals("FooBarJoinKeyColumnName", namingStrategyWrapper.joinKeyColumnName("foo", "bar"));
	}
	
	@Test
	public void testClassToTableName() {
		assertEquals("FooBarClassTable", namingStrategyWrapper.classToTableName("foobar"));
	}
	
	public static class TestNamingStrategy extends DefaultNamingStrategy {
		private static final long serialVersionUID = 1L;
		@Override
		public String collectionTableName(
				String ownerEntity, 
				String ownerEntityTable, 
				String associatedEntity, 
				String associatedEntityTable,
				String propertyName) {
			return "FooBarCollectionTableName";
		}
		@Override
		public String columnName(String columnName) {
			return "FooBarColumnName";
		}
		@Override
		public String propertyToColumnName(String propertyName) {
			return "BarFooPropertyColumn";
		}
		@Override
		public String tableName(String tableName) {
			return "BarFooTable";
		}
		@Override
		public String joinKeyColumnName(String joinedColumn, String joinedTable) {
			return "FooBarJoinKeyColumnName";
		}
		@Override
		public String classToTableName(String className) {
			return "FooBarClassTable";
		}
	}

}
