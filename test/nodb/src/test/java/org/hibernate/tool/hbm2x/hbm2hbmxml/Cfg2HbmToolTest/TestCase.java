/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2004-2025 Red Hat, Inc.
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

package org.hibernate.tool.hbm2x.hbm2hbmxml.Cfg2HbmToolTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.junit.jupiter.api.Test;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {
	
	MetadataBuildingContext foo;
	
	@Test
	public void testNeedsTable(){
		MetadataBuildingContext mdbc = createMetadataBuildingContext();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		PersistentClass pc = new RootClass(mdbc);
		assertTrue(c2h.needsTable(pc));
		assertTrue(c2h.needsTable(new JoinedSubclass(pc, mdbc)));
		assertTrue(c2h.needsTable(new UnionSubclass(pc, mdbc)));
		assertFalse(c2h.needsTable(new SingleTableSubclass(pc, mdbc)));
		assertFalse(c2h.needsTable(new Subclass(pc, mdbc)));			
	}
	
	private MetadataBuildingContext createMetadataBuildingContext() {
		return (MetadataBuildingContext)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { MetadataBuildingContext.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}
	
}
