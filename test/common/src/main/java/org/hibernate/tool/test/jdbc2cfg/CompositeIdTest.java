/*
 * Created on 13-Jan-2005
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 *
 */
public class CompositeIdTest {

	private static final String[] CREATE_SQL = new String[] {
                "create table SimpleLineItem (\r\n" +
                "   lineItemId varchar(256) not null,\r\n" +
                "   customerOrderIdRef varchar(256),\r\n" +
                "   productId varchar(256) not null,\r\n" +
                "   extraId varchar(256) not null,\r\n" +
                "   quantity double,\r\n" + 
                "   primary key (lineItemId)\r\n" + 
                ")",
                "create table Product (\r\n" + 
                "   productId varchar(256) not null,\r\n" +                
                "   extraId varchar(256) not null,\r\n" +
                "   description varchar(256) not null,\r\n" + 
                "   price double,\r\n" + 
                "   numberAvailable double,\r\n" + 
                "   primary key (productId, extraId)\r\n" + 
                ")",
                "create table Customer (\r\n" + 
                "   customerId varchar(256) not null,\r\n" + 
                "   name varchar(256) not null,\r\n" + 
                "   address varchar(256) not null,\r\n" + 
                "   primary key (customerId)\r\n" + 
                ")",
                "create table SimpleCustomerOrder (\r\n" + 
                "   customerOrderId varchar(256) not null,\r\n" +
                "   customerId varchar(256) not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   orderDate date not null,\r\n" + 
                "   primary key (customerOrderId)\r\n" + 
                ")",
                "alter table SimpleLineItem add constraint toSimpleCustomerOrder foreign key (customerOrderIdRef) references SimpleCustomerOrder",
                "alter table SimpleLineItem add constraint fromSimpletoProduct foreign key (productId,extraId) references Product",
                "alter table SimpleCustomerOrder add constraint fromSimpletoCustomer foreign key (customerId) references Customer",
                "create table LineItem (\r\n" + 
                "   customerIdRef varchar(256) not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   productId varchar(256) not null,\r\n" +
                "   extraProdId varchar(256) not null,\r\n" +
                "   quantity double,\r\n" + 
                "   primary key (customerIdRef, orderNumber, productId, extraProdId)\r\n" + 
                ")",
                
                "create table CustomerOrder (\r\n" + 
                "   customerId varchar(256) not null,\r\n" + 
                "   orderNumber double not null,\r\n" + 
                "   orderDate date not null,\r\n" + 
                "   primary key (customerId, orderNumber)\r\n" + 
                ")",
                
                "alter table LineItem add constraint toCustomerOrder foreign key (customerIdRef, orderNumber) references CustomerOrder",
                "alter table LineItem add constraint toProduct foreign key (productId,extraProdId) references Product",
                "alter table CustomerOrder add constraint toCustomer foreign key (customerId) references Customer",                
        };
    
	private static final String[] GENERATE_DATA_SQL = new String[]  {
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('PC', '0', 'My PC', 100.0, 23)",
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('MS', '1', 'My Mouse', 101.0, 23)",
                "insert into CUSTOMER (customerId, name, address) values('MAX', 'Max Rydahl Andersen', 'Neuchatel')",
                "insert into CUSTOMERORDER (customerId, orderNumber, orderDate) values ('MAX', 1, '2005-11-11')", 
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'PC', '0', 10)",
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'MS', '1', 12)",
        };

	private static final String[] DROP_SQL = new String[]  {
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
     
	private JDBCMetaDataConfiguration jmdcfg = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeDDL(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeDDL(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
	}

	@Test
    public void testMultiColumnForeignKeys() {
        Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "LineItem") );
        Assert.assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(
        		table, 
        		JdbcUtil.toIdentifier(this, "toCustomerOrder") );     
        Assert.assertNotNull(foreignKey);                
        Assert.assertEquals(
        		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CustomerOrder"))),
        		foreignKey.getReferencedEntityName() );
        Assert.assertEquals(
        		JdbcUtil.toIdentifier(this, "LineItem"), 
        		foreignKey.getTable().getName() );       
        Assert.assertEquals(2,foreignKey.getColumnSpan() );
        Assert.assertEquals(foreignKey.getColumn(0).getName(), "CUSTOMERIDREF");
        Assert.assertEquals(foreignKey.getColumn(1).getName(), "ORDERNUMBER");      
        Table tab = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "CUSTOMERORDER"));
        Assert.assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMERID");
        Assert.assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDERNUMBER");     
        PersistentClass lineMapping = jmdcfg.getMetadata().getEntityBinding(
        		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "LineItem"))));       
        Assert.assertEquals(4,lineMapping.getIdentifier().getColumnSpan() );
        Iterator<?> columnIterator = lineMapping.getIdentifier().getColumnIterator();
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "CUSTOMERIDREF");
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "ORDERNUMBER");
     }
     
	@Test
    public void testPossibleKeyManyToOne() {
         PersistentClass product = jmdcfg.getMetadata().getEntityBinding( 
         		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CustomerOrder"))));         
         Property identifierProperty = product.getIdentifierProperty();         
         Assert.assertTrue(identifierProperty.getValue() instanceof Component);         
         Component cmpid = (Component) identifierProperty.getValue();        
         Assert.assertEquals(2, cmpid.getPropertySpan() );         
         Iterator<?> iter = cmpid.getPropertyIterator();
         Property id = (Property) iter.next();
         Property extraId = (Property) iter.next();         
 		Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"customerid"), 
				id.getName() );
         Assert.assertEquals(
 				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"ordernumber"), 
        		 extraId.getName() );         
         Assert.assertFalse(id.getValue() instanceof ManyToOne);
         Assert.assertFalse(extraId.getValue() instanceof ManyToOne);
     }
     
	@Test
    public void testKeyProperty() {
        PersistentClass product = jmdcfg.getMetadata().getEntityBinding( 
         		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "Product"))));                 
        Property identifierProperty = product.getIdentifierProperty();        
        Assert.assertTrue(identifierProperty.getValue() instanceof Component);       
        Component cmpid = (Component) identifierProperty.getValue();        
        Assert.assertEquals(2, cmpid.getPropertySpan() );       
        Iterator<?> iter = cmpid.getPropertyIterator();
        Property id = (Property) iter.next();
        Property extraId = (Property) iter.next();     
        Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"productid"), 
        		id.getName() );
        Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"extraid"), 
        		extraId.getName() );        
        Assert.assertFalse(id.getValue() instanceof ManyToOne);
        Assert.assertFalse(extraId.getValue() instanceof ManyToOne);
    }
     
    @Test
    public void testGeneration() throws Exception {
        final File testFolder = temporaryFolder.getRoot();        
        Exporter exporter = new HibernateMappingExporter(jmdcfg, testFolder);		
        Exporter javaExp = new POJOExporter(jmdcfg, testFolder);
        exporter.start();
        javaExp.start();      
        JavaUtil.compile(testFolder);        
        URL[] urls = new URL[] { testFolder.toURI().toURL() };
        URLClassLoader ucl = new URLClassLoader(
        		urls, 
        		Thread.currentThread().getContextClassLoader());
        BootstrapServiceRegistry bootstrapServiceRegistry =  
        		new BootstrapServiceRegistryBuilder()
        			.applyClassLoader(ucl)
        			.build();        
        Configuration derived = new Configuration(bootstrapServiceRegistry);        
        derived.addFile(new File(testFolder, "Simplecustomerorder.hbm.xml") );
        derived.addFile(new File(testFolder, "Simplelineitem.hbm.xml") );
        derived.addFile(new File(testFolder, "Product.hbm.xml") );
        derived.addFile(new File(testFolder, "Customer.hbm.xml") );
        derived.addFile(new File(testFolder, "Lineitem.hbm.xml") );
        derived.addFile(new File(testFolder, "Customerorder.hbm.xml") );       
        Thread.currentThread().setContextClassLoader(ucl);
        SessionFactory factory = derived.buildSessionFactory();
        Session session = factory.openSession();        
        JdbcUtil.executeDDL(this, GENERATE_DATA_SQL);
        session.createQuery("from Lineitem").getResultList();
        List<?> list = session.createQuery("from Product").getResultList();
        Assert.assertEquals(2,list.size() );
        list = session
        		.createQuery("select li.customerorder.id from Lineitem as li")
        		.getResultList();
        Assert.assertTrue(list.size()>0);     
        Class<?> productIdClass = ucl.loadClass("ProductId");
        Object object = productIdClass.newInstance();
        int hash = -1;
        try {
        	hash = object.hashCode();
        } catch(Throwable t) {
        	Assert.fail("Hashcode on new instance should not fail " + t);
        }
        Assert.assertFalse(
        		"hashcode should be different from system", 
        		hash==System.identityHashCode(object));        
        factory.close();
        Thread.currentThread().setContextClassLoader(ucl.getParent() );        
    }
	 
}
     

