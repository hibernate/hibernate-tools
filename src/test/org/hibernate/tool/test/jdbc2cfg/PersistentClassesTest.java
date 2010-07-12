/*
 * Created on 27-Nov-2004
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.classic.Session;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;


/**
 * @author max
 *
 */
public class PersistentClassesTest extends JDBCMetaDataBinderTestCase {

	private static final String PACKAGE_NAME = "org.hibernate.tool.test.jdbc2cfg";



	protected String[] getCreateSQL() {
		
		return new String[] {
                "create table orders ( id numeric(10,0) not null, name varchar(20), primary key (id) )",
				"create table item  ( child_id numeric(10,0) not null, name varchar(50), order_id numeric(10,0), related_order_id numeric(10,0), primary key (child_id), foreign key (order_id) references orders(id), foreign key (related_order_id) references orders(id) )"
				// todo - link where pk is fk to something
				
		};
	}

    protected void configure(JDBCMetaDataConfiguration cfgToConfigure) {        
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDefaultPackageName(PACKAGE_NAME));
        cfgToConfigure.setReverseEngineeringStrategy(c);
    }
	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table item",
				"drop table orders",				
		};
	}

	public void testCreatePersistentClasses() {
		cfg.buildMappings();
		PersistentClass classMapping = cfg.getClassMapping(toClassName("orders") );
		
		assertNotNull("class not found", classMapping);		
		
		KeyValue identifier = classMapping.getIdentifier();
		
		assertNotNull(identifier);
		
	}
	
	public void testCreateManyToOne() {
		cfg.buildMappings();
		PersistentClass classMapping = cfg.getClassMapping(toClassName("item") );
		
		assertNotNull(classMapping);		
		
		KeyValue identifier = classMapping.getIdentifier();
		
		assertNotNull(identifier);	
		
		assertEquals(3,classMapping.getPropertyClosureSpan() );
		
		Property property = classMapping.getProperty("ordersByRelatedOrderId");		
		assertNotNull(property);
		
		property = classMapping.getProperty("ordersByOrderId");		
		assertNotNull(property);
		
		
		
	}
	
	public void testCreateOneToMany() {
		cfg.buildMappings();
        
		PersistentClass orders = cfg.getClassMapping(toClassName("orders") );
		
		Property itemset = orders.getProperty("itemsForRelatedOrderId");
		
		Collection col = (Collection) itemset.getValue();
         
		OneToMany otm = (OneToMany) col.getElement();
        assertEquals(otm.getReferencedEntityName(), toClassName("item") );
        assertEquals(otm.getAssociatedClass().getClassName(), toClassName("item") );
        assertEquals(otm.getTable().getName(), identifier("orders") );
        
		assertNotNull(itemset);
		
		assertTrue(itemset.getValue() instanceof Set);
		
	}
	
	public void testBinding() throws HibernateException, SQLException {
		
		SessionFactory sf = cfg.buildSessionFactory();
		
		
		
		Session session = sf.openSession();
        Transaction t = session.beginTransaction();
	
        Orders order = new Orders();
		order.setId(new Long(1) );
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
		
		Item loadeditem = (Item) session.get(toClassName("item"),new Long(42) );
		
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
        
		order = (Orders) session.load(Orders.class, new Long(1) );
        assertFalse(Hibernate.isInitialized(order) );
        assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
        
        order = (Orders) session.createQuery("from " + PACKAGE_NAME + ".Orders").uniqueResult();
        
        assertFalse(Hibernate.isInitialized(order.getItemsForOrderId() ) );
		t.commit();
        session.close();
        sf.close();
	}

    /**
     * @param m
     * @param itemid TODO
     * @return
     */
    private Item addItem(Orders m, int itemid, String name) {
        Item item = new Item();
        item.setChildId(new Long(itemid) );
        item.setOrderId(m);
        item.setName(name);
        m.getItemsForOrderId().add(item);
        return item;
    }
	
	
	
	public static Test suite() {
		return new TestSuite(PersistentClassesTest.class);
	}
}
