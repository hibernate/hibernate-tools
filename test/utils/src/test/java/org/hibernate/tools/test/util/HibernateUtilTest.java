/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tools.test.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.internal.ConnectionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HibernateUtilTest {
	
	@TempDir
	public File outputFolder = new File("output");
	
	@Test
	public void testGetForeignKey() {
		Table table = new Table();
		assertNull(HibernateUtil.getForeignKey(table, "foo"));
		assertNull(HibernateUtil.getForeignKey(table, "bar"));
		table.createForeignKey("foo", Collections.emptyList(), "Bar", null);
		assertNotNull(HibernateUtil.getForeignKey(table, "foo"));
		assertNull(HibernateUtil.getForeignKey(table, "bar"));
	}
	
	@Test
	public void testDialectInstantiation() {
		assertNotNull(new HibernateUtil.Dialect());
	}
	
	@Test
	public void testInitializeConfiguration() {
		Metadata metadata = HibernateUtil
				.initializeMetadataDescriptor(
						this, 
						new String[] { "HelloWorld.hbm.xml" },
						outputFolder)
				.createMetadata();
		assertSame(
				HibernateUtil.Dialect.class, 
				metadata.getDatabase().getDialect().getClass());
		assertNotNull(metadata.getEntityBinding("HelloWorld"));
	}
	
	@Test
	public void testAddAnnotatedClass() {
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		properties.put(AvailableSettings.CONNECTION_PROVIDER, ConnectionProvider.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, null, properties);
		assertNull(metadataDescriptor
				.createMetadata()
				.getEntityBinding(
						"org.hibernate.tools.test.util.HibernateUtilTest$Dummy"));
		HibernateUtil.addAnnotatedClass(metadataDescriptor, Dummy.class);
		assertNotNull(metadataDescriptor
				.createMetadata()
				.getEntityBinding(
						"org.hibernate.tools.test.util.HibernateUtilTest$Dummy"));
	}
	
	@Entity
	private class Dummy {
		@Id public int id;
	}

}
