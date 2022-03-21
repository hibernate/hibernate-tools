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
package org.hibernate.tool.jdbc2cfg.KeyPropertyCompositeId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	@TempDir
	public File outputDir = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	private RevengStrategy reverseEngineeringStrategy = null;

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		reverseEngineeringStrategy = new DefaultStrategy();
		Properties properties = new Properties();
		properties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, false);
		metadataDescriptor = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(reverseEngineeringStrategy, properties);
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
	public void testMultiColumnForeignKeys() {
		Metadata metadata = metadataDescriptor.createMetadata();
		Table table = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "LINE_ITEM"));
		assertNotNull(table);
		ForeignKey foreignKey = HibernateUtil.getForeignKey(table, JdbcUtil.toIdentifier(this, "TO_CUSTOMER_ORDER"));
		assertNotNull(foreignKey);
		assertEquals(
				reverseEngineeringStrategy.tableToClassName(
						TableIdentifier.create(null, null, JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))),
				foreignKey.getReferencedEntityName());
		assertEquals(JdbcUtil.toIdentifier(this, "LINE_ITEM"), foreignKey.getTable().getName());
		assertEquals(2, foreignKey.getColumnSpan());
		assertEquals(foreignKey.getColumn(0).getName(), "CUSTOMER_ID_REF");
		assertEquals(foreignKey.getColumn(1).getName(), "ORDER_NUMBER");
		Table tab = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"));
		assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMER_ID");
		assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDER_NUMBER");
		PersistentClass lineMapping = metadata.getEntityBinding(
				reverseEngineeringStrategy
					.tableToClassName(TableIdentifier.create(null, null, JdbcUtil.toIdentifier(this, "LINE_ITEM"))));
		assertEquals(4, lineMapping.getIdentifier().getColumnSpan());
		Iterator<Column> columnIterator = lineMapping.getIdentifier().getColumns().iterator();
		assertEquals(((Column) (columnIterator.next())).getName(), "CUSTOMER_ID_REF");
		assertEquals(((Column) (columnIterator.next())).getName(), "ORDER_NUMBER");
	}

	@Test
	public void testPossibleKeyManyToOne() {
		PersistentClass product = metadataDescriptor.createMetadata().getEntityBinding(
				reverseEngineeringStrategy
					.tableToClassName(TableIdentifier.create(null, null, JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))));
		Property identifierProperty = product.getIdentifierProperty();
		assertTrue(identifierProperty.getValue() instanceof Component);
		Component cmpid = (Component) identifierProperty.getValue();
		assertEquals(2, cmpid.getPropertySpan());
		Iterator<?> iter = cmpid.getPropertyIterator();
		Property id = (Property) iter.next();
		Property extraId = (Property) iter.next();
		assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"customer"),
				id.getName());
		assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"orderNumber"),
				extraId.getName());
		assertTrue(id.getValue() instanceof ManyToOne);
		assertFalse(extraId.getValue() instanceof ManyToOne);
	}

	@Test
	public void testKeyProperty() {
		PersistentClass product = metadataDescriptor.createMetadata().getEntityBinding(
				reverseEngineeringStrategy
					.tableToClassName(TableIdentifier.create(null, null, JdbcUtil.toIdentifier(this, "PRODUCT"))));
		Property identifierProperty = product.getIdentifierProperty();
		assertTrue(identifierProperty.getValue() instanceof Component);
		Component cmpid = (Component) identifierProperty.getValue();
		assertEquals(2, cmpid.getPropertySpan());
		Iterator<?> iter = cmpid.getPropertyIterator();
		Property id = (Property)iter.next();
		Property extraId = (Property)iter.next();
		if ("extraId".equals(id.getName())) {
			Property temp = id;
			id = extraId;
			extraId = temp;
		}
		assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"productId"),
				id.getName());
		assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"extraId"),
				extraId.getName());
		assertFalse(id.getValue() instanceof ManyToOne);
		assertFalse(extraId.getValue() instanceof ManyToOne);
	}

	@Test
	public void testGeneration() throws Exception {
		Exporter exporter = new HbmExporter();
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		Exporter javaExp = ExporterFactory.createExporter(ExporterType.JAVA);
		javaExp.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		javaExp.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.start();
		javaExp.start();
		JavaUtil.compile(outputDir);
		URL[] urls = new URL[] { outputDir.toURI().toURL() };
		URLClassLoader ucl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		File[] files = new File[6];
		files[0] = new File(outputDir, "SimpleCustomerOrder.hbm.xml");
		files[1] = new File(outputDir, "SimpleLineItem.hbm.xml");
		files[2] = new File(outputDir, "Product.hbm.xml");
		files[3] = new File(outputDir, "Customer.hbm.xml");
		files[4] = new File(outputDir, "LineItem.hbm.xml");
		files[5] = new File(outputDir, "CustomerOrder.hbm.xml");
		Thread.currentThread().setContextClassLoader(ucl);
		SessionFactory factory = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata()
				.buildSessionFactory();
		Session session = factory.openSession();
		JdbcUtil.populateDatabase(this);;
		session.createQuery("from LineItem", null).getResultList();
		List<?> list = session.createQuery("from Product", null).getResultList();
		assertEquals(2, list.size());
		list = session
				.createQuery("select li.id.customerOrder.id from LineItem as li", null)
				.getResultList();
		assertTrue(list.size() > 0);
		Class<?> productIdClass = ucl.loadClass("ProductId");
		Constructor<?> productIdConstructor = productIdClass.getConstructor(new Class[] {});
		Object object = productIdConstructor.newInstance();
		int hash = -1;
		try {
			hash = object.hashCode();
		} catch (Throwable t) {
			fail("Hashcode on new instance should not fail " + t);
		}
		assertFalse(hash == System.identityHashCode(object), "hashcode should be different from system");
		factory.close();
		Thread.currentThread().setContextClassLoader(ucl.getParent());
	}

}
