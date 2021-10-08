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
package org.hibernate.tool.jdbc2cfg.CompositeId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
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

	private MetadataDescriptor metadataDescriptor = null;
	private ReverseEngineeringStrategy reverseEngineeringStrategy = null;
	
	@TempDir
	public File temporaryFolder = new File("temp");

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
		metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(reverseEngineeringStrategy, null, true);
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
        		JdbcUtil.toIdentifier(this, "LINE_ITEM") );
        assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(
        		table, 
        		JdbcUtil.toIdentifier(this, "TO_CUSTOMER_ORDER") );     
        assertNotNull(foreignKey);                
        assertEquals(
        		reverseEngineeringStrategy.tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))),
        		foreignKey.getReferencedEntityName() );
        assertEquals(
        		JdbcUtil.toIdentifier(this, "LINE_ITEM"), 
        		foreignKey.getTable().getName() );       
        assertEquals(2,foreignKey.getColumnSpan() );
        assertEquals(foreignKey.getColumn(0).getName(), "CUSTOMER_ID_REF");
        assertEquals(foreignKey.getColumn(1).getName(), "ORDER_NUMBER");      
        Table tab = HibernateUtil.getTable(
        		metadata, 
        		JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"));
        assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMER_ID");
        assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDER_NUMBER");     
        PersistentClass lineMapping = metadata.getEntityBinding(
        		reverseEngineeringStrategy.tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "LINE_ITEM"))));       
        assertEquals(4,lineMapping.getIdentifier().getColumnSpan() );
        Iterator<?> columnIterator = lineMapping.getIdentifier().getColumnIterator();
        assertEquals(((Column)(columnIterator.next())).getName(), "CUSTOMER_ID_REF");
        assertEquals(((Column)(columnIterator.next())).getName(), "ORDER_NUMBER");
     }
     
	@Test
    public void testPossibleKeyManyToOne() {
         PersistentClass product = metadataDescriptor.createMetadata().getEntityBinding( 
         		reverseEngineeringStrategy.tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))));         
         Property identifierProperty = product.getIdentifierProperty();         
         assertTrue(identifierProperty.getValue() instanceof Component);         
         Component cmpid = (Component) identifierProperty.getValue();        
         assertEquals(2, cmpid.getPropertySpan() );         
         Iterator<?> iter = cmpid.getPropertyIterator();
         Property id = (Property) iter.next();
         Property extraId = (Property) iter.next();         
 		 assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"CUSTOMER_ID"), 
				id.getName() );
         assertEquals(
 				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"ORDER_NUMBER"), 
        		 extraId.getName() );         
         assertFalse(id.getValue() instanceof ManyToOne);
         assertFalse(extraId.getValue() instanceof ManyToOne);
     }
     
	@Test
    public void testKeyProperty() {
        PersistentClass product = metadataDescriptor.createMetadata().getEntityBinding( 
         		reverseEngineeringStrategy.tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "PRODUCT"))));                 
        Property identifierProperty = product.getIdentifierProperty();        
        assertTrue(identifierProperty.getValue() instanceof Component);       
        Component cmpid = (Component) identifierProperty.getValue();        
        assertEquals(2, cmpid.getPropertySpan() );       
        Iterator<?> iter = cmpid.getPropertyIterator();
        Property id = (Property) iter.next();
        Property extraId = (Property) iter.next();     
        assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"PRODUCT_ID"), 
        		id.getName() );
        assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"EXTRA_ID"), 
        		extraId.getName() );        
        assertFalse(id.getValue() instanceof ManyToOne);
        assertFalse(extraId.getValue() instanceof ManyToOne);
    }
     
    @Test
    public void testGeneration() throws Exception {
        Exporter exporter = new HibernateMappingExporter();	
        exporter.setMetadataDescriptor(metadataDescriptor);
        exporter.setOutputDirectory(temporaryFolder);
        Exporter javaExp = new POJOExporter();
        javaExp.setMetadataDescriptor(metadataDescriptor);
        javaExp.setOutputDirectory(temporaryFolder);
        exporter.start();
        javaExp.start();      
        JavaUtil.compile(temporaryFolder);        
        URL[] urls = new URL[] { temporaryFolder.toURI().toURL() };
        URLClassLoader ucl = new URLClassLoader(
        		urls, 
        		Thread.currentThread().getContextClassLoader());
        File[] files = new File[6];
        files[0] = new File(temporaryFolder, "SimpleCustomerOrder.hbm.xml");
        files[1] = new File(temporaryFolder, "SimpleLineItem.hbm.xml");
        files[2] = new File(temporaryFolder, "Product.hbm.xml");
        files[3] = new File(temporaryFolder, "Customer.hbm.xml");
        files[4] = new File(temporaryFolder, "LineItem.hbm.xml");
        files[5] = new File(temporaryFolder, "CustomerOrder.hbm.xml");
        Thread.currentThread().setContextClassLoader(ucl);
        SessionFactory factory = MetadataDescriptorFactory
        		.createNativeDescriptor(null, files, null)
        		.createMetadata()
        		.buildSessionFactory();
        Session session = factory.openSession();        
        JdbcUtil.populateDatabase(this);
        session.createQuery("from LineItem").getResultList();
        List<?> list = session.createQuery("from Product").getResultList();
        assertEquals(2,list.size() );
        list = session
        		.createQuery("select li.customerOrder.id from LineItem as li")
        		.getResultList();
        assertTrue(list.size()>0);     
        Class<?> productIdClass = ucl.loadClass("ProductId");
        Object object = productIdClass.newInstance();
        int hash = -1;
        try {
        	hash = object.hashCode();
        } catch(Throwable t) {
        	fail("Hashcode on new instance should not fail " + t);
        }
        assertFalse( 
        		hash==System.identityHashCode(object),
        		"hashcode should be different from system");        
        factory.close();
        Thread.currentThread().setContextClassLoader(ucl.getParent() );        
    }
	 
}
     

