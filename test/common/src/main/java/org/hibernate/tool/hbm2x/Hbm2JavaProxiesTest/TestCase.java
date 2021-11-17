/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
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

package org.hibernate.tool.hbm2x.Hbm2JavaProxiesTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.java.Cfg2JavaTool;
import org.hibernate.tool.internal.export.java.EntityPOJOClass;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author karlvr
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"ManyToOneProxies.hbm.xml",
	};
	
	@TempDir
	public File outputDir = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	private File scDir = null;
	private File resourcesDir = null;
	private Metadata metadata = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		scDir = new File(outputDir, "output");
		scDir.mkdir();
		resourcesDir = new File(outputDir, "resources");
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	/**
	 * Demonstrate failure using the previous hbm2java generation method that used the
	 * concrete entity type instead of its proxy in a many to one relationship.
	 * <p>
	 * This test uses hand-crafted entities in order to demonstrate the issue.
	 */
	@Test
	public void testFailWhenManyToOnePropertyTypeIsntProxy() {
		final Session session = manyToOneProxiesFactory().openSession();
		try {
			final Transaction tx = session.beginTransaction();

			/* Create some objects in the database to later retrieve */
			ClassA a = new ClassA();
			a.setId(1);
			
			ClassB b = new ClassBSubA();
			b.setId(1);
			a.setMyClassB(b);
			
			session.save(b);
			session.save(a);
			
			/* Clear the session so we can retrieve */
			tx.commit();
			session.clear();
	
			/* Retrieve the object. Throws an IllegalArgumentException as it tries to set the lazy proxy to the
			 * property with the concrete basetype.
			 */
			try {
				a = (ClassA) session.get(ClassA.class, 1);
			} catch (HibernateException e) {
				assertTrue(e.getCause() instanceof IllegalArgumentException, "Unexpected exception type thrown: " + e.getCause());
				return;
			}
			
			fail("An exception was expected to be thrown.");
		} finally {
			session.close();
		}
	}

	/**
	 * Demonstrate the success of the new hbm2java generation method that uses the
	 * entity's proxy instead of its concrete type in a many to one relationship.
	 * <p>
	 * This test uses hand-crafted entities but it would be nice if it could use generated classes.
	 */
	@Test
	public void testSuccessWhenManyToOnePropertyTypeIsProxy() {
		final Session session = manyToOneProxiesFactory().openSession();
		try {
			final Transaction tx = session.beginTransaction();

			/* Create some objects in the database to later retrieve */
			ClassC c = new ClassC();
			c.setId(2);
			
			ClassB b = new ClassBSubA();
			b.setId(2);
			c.setMyClassB(b);
			
			session.save(b);
			session.save(c);
			
			/* Clear the session so we can retrieve */
			tx.commit();
			session.clear();
	
			/* Retrieve the object. This succeeds as ClassC uses the proxy interface correctly.
			 */
			c = (ClassC) session.get(ClassC.class, 2);
		} finally {
			session.close();
		}
	}
	
	/**
	 * Test that the {@link BasicPOJOClass#getJavaTypeName(Property, boolean)} now returns the proxy
	 * instead of the entity's concrete class.
	 */
	@Test
	public void testProxies() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaProxiesTest.ClassA");
		Property property = classMapping.getProperty("myClassB");
		
		EntityPOJOClass pj = new EntityPOJOClass(classMapping, c2j);
		String javaTypeName = pj.getJavaTypeName(property, true);
		assertEquals("ProxyB", javaTypeName);
	}

	private SessionFactory manyToOneProxiesFactory() {
		SessionFactory factory = MetadataDescriptorFactory
			.createNativeDescriptor(null, new File[] { new File(resourcesDir, "ManyToOneProxies.hbm.xml") }, null)
			.createMetadata()
			.buildSessionFactory();
		return factory;
	}
	
}
