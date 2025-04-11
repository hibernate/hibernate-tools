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

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.internal.factory.Cfg2HbmToolWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cfg2HbmToolWrapperTest {
	
	private Cfg2HbmToolWrapper wrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrapper = Cfg2HbmToolWrapperFactory.createCfg2HbmToolWrapper();
	}

	@Test
	public void testGetTagPersistentClass() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createPersistentClassWrapper(
				new RootClass(DummyMetadataBuildingContext.INSTANCE));
		assertEquals("class", wrapper.getTag(persistentClassWrapper));
	}

	@Test
	public void testGetTagProperty() throws Exception {
		PropertyWrapper propertyWrapper = PropertyWrapperFactory.createPropertyWrapper();
		Property property = (Property)propertyWrapper.getWrappedObject();
		RootClass rc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		BasicValue basicValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		basicValue.setTypeName("foobar");
		property.setValue(basicValue);
		property.setPersistentClass(rc);
		rc.setVersion(property);
		assertEquals("version", wrapper.getTag(propertyWrapper));
	}
	
}
