/*
 * Created on 13-Jan-2005
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.classic.Session;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.XMLPrettyPrinter;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 *
 */
public class CompositeIdTest extends JDBCMetaDataBinderTestCase {

	protected void configure(JDBCMetaDataConfiguration configuration) {
		super.configure( configuration );		
	}
    protected String[] getCreateSQL() {
        
        return new String[] {
                "create table SimpleLineItem (\r\n" +
                "   lineItemId varchar not null,\r\n" +
                "   customerOrderIdRef varchar,\r\n" +
                "   productId varchar not null,\r\n" +
                "   extraId varchar not null,\r\n" +
                "   quantity double,\r\n" + 
                "   primary key (lineItemId)\r\n" + 
                ")",
                "create table Product (\r\n" + 
                "   productId varchar not null,\r\n" +                
                "   extraId varchar not null,\r\n" +
                "   description varchar not null,\r\n" + 
                "   price double,\r\n" + 
                "   numberAvailable double,\r\n" + 
                "   primary key (productId, extraId)\r\n" + 
                ")",
                "create table Customer (\r\n" + 
                "   customerId varchar not null,\r\n" + 
                "   name varchar not null,\r\n" + 
                "   address varchar not null,\r\n" + 
                "   primary key (customerId)\r\n" + 
                ")",
                "create table SimpleCustomerOrder (\r\n" + 
                "   customerOrderId varchar not null,\r\n" +
                "   customerId varchar not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   orderDate date not null,\r\n" + 
                "   primary key (customerOrderId)\r\n" + 
                ")",
                "alter table SimpleLineItem add constraint toSimpleCustomerOrder foreign key (customerOrderIdRef) references SimpleCustomerOrder",
                "alter table SimpleLineItem add constraint fromSimpletoProduct foreign key (productId,extraId) references Product",
                "alter table SimpleCustomerOrder add constraint fromSimpletoCustomer foreign key (customerId) references Customer",
                "create table LineItem (\r\n" + 
                "   customerIdRef varchar not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   productId varchar not null,\r\n" +
                "   extraProdId varchar not null,\r\n" +
                "   quantity double,\r\n" + 
                "   primary key (customerIdRef, orderNumber, productId, extraProdId)\r\n" + 
                ")",
                
                "create table CustomerOrder (\r\n" + 
                "   customerId varchar not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   orderDate date not null,\r\n" + 
                "   primary key (customerId, orderNumber)\r\n" + 
                ")",
                
                "alter table LineItem add constraint toCustomerOrder foreign key (customerIdRef, orderNumber) references CustomerOrder",
                "alter table LineItem add constraint toProduct foreign key (productId,extraProdId) references Product",
                "alter table CustomerOrder add constraint toCustomer foreign key (customerId) references Customer",                
        };
    }
    
    protected String[] getGenDataSQL() {
        return new String[] {
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('PC', '0', 'My PC', 100.0, 23)",
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('MS', '1', 'My Mouse', 101.0, 23)",
                "insert into CUSTOMER (customerId, name, address) values('MAX', 'Max Rydahl Andersen', 'Neuchatel')",
                "insert into CUSTOMERORDER (customerId, orderNumber, orderDate) values ('MAX', 1, '2005-11-11')", 
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'PC', '0', 10)",
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'MS', '1', 12)",
        };
    }

     protected String[] getDropSQL() {
        return new String[] {   
                "alter table LINEITEM drop constraint toCustomerOrder",
                "alter table LINEITEM drop constraint toProduct",
                "alter table CustomerOrder drop constraint toCustomer",
                "alter table SimpleLineItem drop constraint toSimpleCustomerOrder",
                "alter table SimpleLineItem drop constraint fromSimpletoProduct",
                "alter table SimpleCustomerOrder drop constraint fromSimpletoCustomer",
                "drop table SimpleLineItem ",
                "drop table Product ",
                "drop table Customer ",
                "drop table SimpleCustomerOrder ",
                "drop table CustomerOrder ",                
                "drop table LineItem ",                           
        };
    }
     
     public void testMultiColumnForeignKeys() {
        Table table = getTable(identifier("LineItem") );
        assertNotNull(table);
        ForeignKey foreignKey = getForeignKey(table, identifier("toCustomerOrder") );     
        assertNotNull(foreignKey);
                
        assertEquals(toClassName(identifier("CustomerOrder") ), foreignKey.getReferencedEntityName() );
        assertEquals(identifier("LineItem"), foreignKey.getTable().getName() );
        
        assertEquals(2,foreignKey.getColumnSpan() );
        assertEquals(foreignKey.getColumn(0).getName(), "CUSTOMERIDREF");
        assertEquals(foreignKey.getColumn(1).getName(), "ORDERNUMBER");
        
        Table tab = getTable(identifier("CUSTOMERORDER"));
        assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMERID");
        assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDERNUMBER");
        
        cfg.buildMappings();
        
        PersistentClass lineMapping = cfg.getClassMapping(toClassName(identifier("LineItem") ) );
        
        assertEquals(4,lineMapping.getIdentifier().getColumnSpan() );
        Iterator columnIterator = lineMapping.getIdentifier().getColumnIterator();
        assertEquals(((Column)(columnIterator.next())).getName(), "CUSTOMERIDREF");
        assertEquals(((Column)(columnIterator.next())).getName(), "ORDERNUMBER");
        
        
/*        Property productproperty = lineMapping.getProperty("Product");        
        assertNotNull(productproperty);*/
        
        
     }
     
     public void testPossibleKeyManyToOne() {
         cfg.buildMappings();
         
         PersistentClass product = cfg.getClassMapping( toClassName(identifier("CustomerOrder") ) );
         
         Property identifierProperty = product.getIdentifierProperty();
         
         assertTrue(identifierProperty.getValue() instanceof Component);
         
         Component cmpid = (Component) identifierProperty.getValue();
         
         assertEquals(2, cmpid.getPropertySpan() );
         
         Iterator iter = cmpid.getPropertyIterator();
         Property id = (Property) iter.next();
         Property extraId = (Property) iter.next();
         
         checkKeyProperties( id, extraId );
         
     }
	protected void checkKeyProperties(Property id, Property extraId) {
		assertEquals(toPropertyName("customerid"), id.getName() );
         assertEquals(toPropertyName("ordernumber"), extraId.getName() );
         
         assertFalse(id.getValue() instanceof ManyToOne);
         assertFalse(extraId.getValue() instanceof ManyToOne);
	}
     
     public void testKeyProperty() {
        cfg.buildMappings();
        
        PersistentClass product = cfg.getClassMapping( toClassName(identifier("Product") ) );
        
        Property identifierProperty = product.getIdentifierProperty();
        
        assertTrue(identifierProperty.getValue() instanceof Component);
        
        Component cmpid = (Component) identifierProperty.getValue();
        
        assertEquals(2, cmpid.getPropertySpan() );
        
        Iterator iter = cmpid.getPropertyIterator();
        Property id = (Property) iter.next();
        Property extraId = (Property) iter.next();
        
        assertEquals(toPropertyName("productid"), id.getName() );
        assertEquals(toPropertyName("extraid"), extraId.getName() );
        
        assertFalse(id.getValue() instanceof ManyToOne);
        assertFalse(extraId.getValue() instanceof ManyToOne);
     }
     
     
     public void testGeneration() throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        final File outputdir = new File("reverseoutput");
        outputdir.mkdirs();
        
        cfg.buildMappings();
         
        Exporter exporter = new HibernateMappingExporter(cfg, outputdir);
		
        Exporter javaExp = new POJOExporter(cfg, outputdir);
        exporter.start();
        javaExp.start();
        
        XMLPrettyPrinter.prettyPrintDirectory(outputdir,".hbm.xml", false);
        TestHelper.compile(outputdir, outputdir);
        
        Configuration derived = new Configuration();
        
        derived.addFile(new File(outputdir, "Simplecustomerorder.hbm.xml") );
        derived.addFile(new File(outputdir, "Simplelineitem.hbm.xml") );
        derived.addFile(new File(outputdir, "Product.hbm.xml") );
        derived.addFile(new File(outputdir, "Customer.hbm.xml") );
        derived.addFile(new File(outputdir, "Lineitem.hbm.xml") );
        derived.addFile(new File(outputdir, "Customerorder.hbm.xml") );
        
        derived.buildMappings();        
        
        /*assertNotNull(derived.getClassMapping("org.reveng.Child") );
        assertNotNull(derived.getClassMapping("org.reveng.Master") );*/
        URL[] urls = new URL[] { outputdir.toURL() };
        URLClassLoader ucl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader() );
        Thread.currentThread().setContextClassLoader(ucl);
        SessionFactory factory = derived.buildSessionFactory();
        Session session = factory.openSession();
        
        executeDDL(getGenDataSQL(), false);
        session.createQuery("from Lineitem").list();
        List list = session.createQuery("from Product").list();
        assertEquals(2,list.size() );
        
        list = session.createQuery(getCustomerOrderQuery()).list();
        assertTrue(list.size()>0);
        
        Class productIdClass = ucl.loadClass("ProductId");
        Object object = productIdClass.newInstance();
        int hash = -1;
        try {
        	hash = object.hashCode();
        } catch(Throwable t) {
        	fail("Hashcode on new instance should not fail " + t);
        }
        assertFalse("hashcode should be different from system", hash==System.identityHashCode(object));
        
        factory.close();
        Thread.currentThread().setContextClassLoader(ucl.getParent() );
        
        
        TestHelper.deleteDir(outputdir);
        }
	protected String getCustomerOrderQuery() {
		return "select li.customerorder.id from Lineitem as li";
	}

	 public static Test suite() {
			return new TestSuite(CompositeIdTest.class);
		}
}
     

