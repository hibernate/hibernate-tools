/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.PersistentClasses;

import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private static final String PACKAGE_NAME = "org.hibernate.tool.jdbc2cfg.PersistentClasses";

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDefaultPackageName(PACKAGE_NAME));
        metadata = MetadataSourcesFactory
        		.createJdbcSources(c, null)
        		.buildMetadata();
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testCreatePersistentClasses() {
		PersistentClass classMapping = metadata.getEntityBinding(PACKAGE_NAME + ".Orders");
		Assert.assertNotNull("class not found", classMapping);		
		KeyValue identifier = classMapping.getIdentifier();
		Assert.assertNotNull(identifier);		
	}
		
	@Test
	public void testCreateManyToOne() {
		PersistentClass classMapping = metadata.getEntityBinding(PACKAGE_NAME + ".Item");
		Assert.assertNotNull(classMapping);		
		KeyValue identifier = classMapping.getIdentifier();
		Assert.assertNotNull(identifier);	
		Assert.assertEquals(3,classMapping.getPropertyClosureSpan() );	
		Property property = classMapping.getProperty("ordersByRelatedOrderId");		
		Assert.assertNotNull(property);	
		property = classMapping.getProperty("ordersByOrderId");		
		Assert.assertNotNull(property);
	}
	
	@Test
	public void testCreateOneToMany() {
		PersistentClass orders = metadata.getEntityBinding(PACKAGE_NAME + ".Orders");		
		Property itemset = orders.getProperty("itemsForRelatedOrderId");	
		Collection col = (Collection) itemset.getValue();         
		OneToMany otm = (OneToMany) col.getElement();
        Assert.assertEquals(otm.getReferencedEntityName(), PACKAGE_NAME + ".Item");
        Assert.assertEquals(otm.getAssociatedClass().getClassName(), PACKAGE_NAME + ".Item");
        Assert.assertEquals(otm.getTable().getName(), "ORDERS");        
		Assert.assertNotNull(itemset);		
		Assert.assertTrue(itemset.getValue() instanceof Set);		
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
		order.setId(new Integer(1) );
		order.setName("Mickey");
		
		session.save(order);
		
		Item item = addItem(order, 42, "item 42");
        session.save(item);
		session.save(addItem(order, 43, "x") );
        session.save(addItem(order, 44, "y") );
        session.save(addItem(order, 45, "z") );
        session.save(addItem(order, 46, "w") );
        
		t.commit();
		session.close();
				
		session = sf.openSession();
		t = session.beginTransaction();
		
		Item loadeditem = (Item) session.get(PACKAGE_NAME + ".Item", new Integer(42) );
		
		Assert.assertEquals(item.getName(),loadeditem.getName() );
        Assert.assertEquals(item.getChildId(),loadeditem.getChildId() );
        Assert.assertEquals(item.getOrderId().getId(),loadeditem.getOrderId().getId() );
		
        Assert.assertTrue(loadeditem.getOrderId().getItemsForOrderId().contains(loadeditem) );
        Assert.assertTrue(item.getOrderId().getItemsForOrderId().contains(item) );
        
        Assert.assertEquals(5,item.getOrderId().getItemsForOrderId().size() );
        Assert.assertEquals(5,loadeditem.getOrderId().getItemsForOrderId().size() );
		
		t.commit();
        session.close();
		
        session = sf.openSession();
		t = session.beginTransaction();
        
		order = (Orders) session.load(Orders.class, new Integer(1) );
        Assert.assertFalse(Hibernate.isInitialized(order) );
        Assert.assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
        
        order = (Orders) session.createQuery("from " + PACKAGE_NAME + ".Orders").getSingleResult();
         
        Assert.assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
		t.commit();
        session.close();
        sf.close();
	}

	private Item addItem(Orders m, int itemid, String name) {
        Item item = new Item();
        item.setChildId(new Integer(itemid) );
        item.setOrderId(m);
        item.setName(name);
        m.getItemsForOrderId().add(item);
        return item;
    }
	
}
