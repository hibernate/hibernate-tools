/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
