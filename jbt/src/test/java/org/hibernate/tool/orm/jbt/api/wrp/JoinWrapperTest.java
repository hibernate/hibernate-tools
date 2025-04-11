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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.internal.factory.JoinWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinWrapperTest {
	
	private JoinWrapper joinWrapper = null;
	private Join wrappedJoin = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedJoin = new Join();
		joinWrapper = JoinWrapperFactory.createJoinWrapper(wrappedJoin);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedJoin);
		assertNotNull(joinWrapper);
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<PropertyWrapper> propertyIterator = joinWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property property = new Property();
		wrappedJoin.addProperty(property);
		propertyIterator = joinWrapper.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		PropertyWrapper p = propertyIterator.next();
		assertSame(p.getWrappedObject(), property);
	}

}
