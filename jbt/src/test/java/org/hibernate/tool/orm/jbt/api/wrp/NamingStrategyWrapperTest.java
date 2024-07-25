package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.tool.orm.jbt.internal.factory.NamingStrategyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamingStrategyWrapperTest {

	private NamingStrategyWrapper namingStrategyWrapper = null;
	private Object wrappedNamingStrategy = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedNamingStrategy = new TestNamingStrategy();
		namingStrategyWrapper = NamingStrategyWrapperFactory.createNamingStrategyWrapper(TestNamingStrategy.class.getName());
		wrappedNamingStrategy = ((Wrapper)namingStrategyWrapper).getWrappedObject();
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
		assertEquals("FooBarColumnName", namingStrategyWrapper.propertyToColumnName("bar"));
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
		assertEquals("BarFooTable", namingStrategyWrapper.classToTableName("foobar"));
	}
	
	@Test
	public void testGetStrategyClassName() {
		assertEquals(TestNamingStrategy.class.getName(), namingStrategyWrapper.getStrategyClassName());
	}
	
	@SuppressWarnings("serial")
	public static class TestNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
		@Override 
		public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
			return Identifier.toIdentifier("FooBarCollectionTableName");
		}
		@Override
		public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
			return Identifier.toIdentifier("FooBarColumnName");
		}
		@Override
		public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
			return Identifier.toIdentifier("BarFooTable");
		}
		@Override
		public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
			return Identifier.toIdentifier("FooBarJoinKeyColumnName");
		}
	}
	
}
