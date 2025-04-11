/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2022-2025 Red Hat, Inc.
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
package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

public class MetadataHelperTest {

	@Test
	public void testGetMetadataSources() {
		MetadataSources mds1 = new MetadataSources();
		Configuration configuration = new Configuration();
		MetadataSources mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertNotSame(mds1, mds2);
		configuration = new Configuration(mds1);
		mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertSame(mds1, mds2);
	}
	
	@Test
	public void testGetMetadata() {
		 Configuration methodConfiguration = new MetadataMethodConfiguration();
	     assertSame(
	    		 MetadataMethodConfiguration.METADATA, 
	    		 MetadataHelper.getMetadata(methodConfiguration));
	     Configuration fieldConfiguration = new MetadataFieldConfiguration();
	     assertSame(
	    		 MetadataFieldConfiguration.METADATA, 
	    		 MetadataHelper.getMetadata(fieldConfiguration));
	     MetadataSources metadataSources = new MetadataSources();
	     metadataSources.addInputStream(new ByteArrayInputStream(TEST_HBM_XML_STRING.getBytes()));
	     Configuration configuration = new Configuration(metadataSources);
	     configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
	     configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
	     Metadata metadata = MetadataHelper.getMetadata(configuration);
	     assertNotNull(metadata.getEntityBinding("org.hibernate.tool.orm.jbt.internal.util.MetadataHelperTest$Foo"));
	}
	
	private static class MetadataMethodConfiguration extends Configuration {
		static Metadata METADATA = createMetadata();
		@SuppressWarnings("unused")
		public Metadata getMetadata() {
			return METADATA;
		}
	}
	
	private static class MetadataFieldConfiguration extends Configuration {
		static Metadata METADATA = createMetadata();
		@SuppressWarnings("unused")
		private Metadata metadata = METADATA;
	}
	
	private static Metadata createMetadata() {
		Metadata result = null;
		result = (Metadata) Proxy.newProxyInstance(
				MetadataHelperTest.class.getClassLoader(), 
				new Class[] { Metadata.class },  
				new InvocationHandler() {				
					@Override
					public Object invoke(
							Object proxy, 
							Method method, 
							Object[] args) throws Throwable {
						return null;
					}
				});
		return result;
	}
	
	@SuppressWarnings("unused")
	private static class Foo {
		public String id;
	}
	
	private static final String TEST_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.internal.util'>" +
			"  <class name='MetadataHelperTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";

}
