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
package org.hibernate.tool.jdbc2cfg.PersistentClasses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private static final String PACKAGE_NAME = "org.hibernate.tool.jdbc2cfg.PersistentClasses";

	private Metadata metadata = null;

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
        AbstractStrategy c = new DefaultStrategy();
        c.setSettings(new RevengSettings(c).setDefaultPackageName(PACKAGE_NAME));
        metadata = MetadataDescriptorFactory
        		.createReverseEngineeringDescriptor(c, null)
        		.createMetadata();
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testCreatePersistentClasses() {
		PersistentClass classMapping = metadata.getEntityBinding(PACKAGE_NAME + ".Orders");
		assertNotNull(classMapping, "class not found");		
		KeyValue identifier = classMapping.getIdentifier();
		assertNotNull(identifier);		
	}
		
	@Test
	public void testCreateManyToOne() {
		PersistentClass classMapping = metadata.getEntityBinding(PACKAGE_NAME + ".Item");
		assertNotNull(classMapping);		
		KeyValue identifier = classMapping.getIdentifier();
		assertNotNull(identifier);	
		assertEquals(3,classMapping.getPropertyClosureSpan() );	
		Property property = classMapping.getProperty("ordersByRelatedOrderId");		
		assertNotNull(property);	
		property = classMapping.getProperty("ordersByOrderId");		
		assertNotNull(property);
	}
	
	@Test
	public void testCreateOneToMany() {
		PersistentClass orders = metadata.getEntityBinding(PACKAGE_NAME + ".Orders");		
		Property itemset = orders.getProperty("itemsForRelatedOrderId");	
		Collection col = (Collection) itemset.getValue();         
		OneToMany otm = (OneToMany) col.getElement();
        assertEquals(otm.getReferencedEntityName(), PACKAGE_NAME + ".Item");
        assertEquals(otm.getAssociatedClass().getClassName(), PACKAGE_NAME + ".Item");
        assertEquals(otm.getTable().getName(), "ORDERS");        
		assertNotNull(itemset);		
		assertTrue(itemset.getValue() instanceof Set);		
	}
	
	@Test
	public void testBinding() throws HibernateException, SQLException {	
		
		String schemaToUse = Environment
				.getProperties()
				.getProperty(AvailableSettings.DEFAULT_SCHEMA);
		PersistentClass orders = metadata.getEntityBinding(PACKAGE_NAME + ".Orders");	
		orders.getTable().setSchema(schemaToUse);
		PersistentClass items = metadata.getEntityBinding(PACKAGE_NAME + ".Item");
		items.getTable().setSchema(schemaToUse);
		
		SessionFactory sf = metadata.buildSessionFactory();
		Session session = sf.openSession();
        Transaction t = session.beginTransaction();
	
        Orders order = new Orders();
		order.setId(Integer.valueOf(1) );
		order.setName("Mickey");
		
		session.merge(order);
		
		Item item = addItem(order, 42, "item 42");
        session.merge(item);
		session.merge(addItem(order, 43, "x") );
        session.merge(addItem(order, 44, "y") );
        session.merge(addItem(order, 45, "z") );
        session.merge(addItem(order, 46, "w") );
        
		t.commit();
		session.close();
				
		session = sf.openSession();
		t = session.beginTransaction();
		
		Item loadeditem = (Item) session.get(PACKAGE_NAME + ".Item", Integer.valueOf(42) );
		
		assertEquals(item.getName(),loadeditem.getName() );
        assertEquals(item.getChildId(),loadeditem.getChildId() );
        assertEquals(item.getOrderId().getId(),loadeditem.getOrderId().getId() );
		
        assertTrue(loadeditem.getOrderId().getItemsForOrderId().contains(loadeditem) );
        assertTrue(item.getOrderId().getItemsForOrderId().contains(item) );
        
        assertEquals(5,item.getOrderId().getItemsForOrderId().size() );
        assertEquals(5,loadeditem.getOrderId().getItemsForOrderId().size() );
		
		t.commit();
        session.close();
		
        session = sf.openSession();
		t = session.beginTransaction();
        
		order = (Orders) session.getReference(Orders.class, Integer.valueOf(1) );
        assertFalse(Hibernate.isInitialized(order) );
        assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
        
        order = (Orders) session.createQuery("from " + PACKAGE_NAME + ".Orders", null).getSingleResult();
         
        assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
		t.commit();
        session.close();
        sf.close();
	}

	private Item addItem(Orders m, int itemid, String name) {
        Item item = new Item();
        item.setChildId(Integer.valueOf(itemid) );
        item.setOrderId(m);
        item.setName(name);
        item.setOrdersByRelatedOrderId(m);
        m.getItemsForOrderId().add(item);
        return item;
    }
	
}
