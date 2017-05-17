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

	static final String[] CREATE_SQL = new String[] {
                "CREATE TABLE SIMPLE_LINE_ITEM (LINE_ITEM_ID VARCHAR(256) NOT NULL, CUSTOMER_ORDER_ID_REF VARCHAR(256), PRODUCT_ID VARCHAR(256) NOT NULL, EXTRA_ID VARCHAR(256) NOT NULL, QUANTITY FLOAT, PRIMARY KEY (LINE_ITEM_ID))",
                "CREATE TABLE PRODUCT (PRODUCT_ID VARCHAR(256) NOT NULL, EXTRA_ID VARCHAR(256) NOT NULL, DESCRIPTION VARCHAR(256) NOT NULL, PRICE FLOAT, NUMBER_AVAILABLE FLOAT, PRIMARY KEY (PRODUCT_ID, EXTRA_ID))",
                "CREATE TABLE CUSTOMER (CUSTOMER_ID VARCHAR(256) NOT NULL, NAME VARCHAR(256) NOT NULL, ADDRESS VARCHAR(256) NOT NULL, PRIMARY KEY (CUSTOMER_ID))",
                "CREATE TABLE SIMPLE_CUSTOMER_ORDER (CUSTOMER_ORDER_ID VARCHAR(256) NOT NULL, CUSTOMER_ID VARCHAR(256) NOT NULL, ORDER_NUMBER FLOAT NOT NULL, ORDER_DATE DATE NOT NULL, PRIMARY KEY (CUSTOMER_ORDER_ID))",
                "ALTER TABLE SIMPLE_LINE_ITEM ADD CONSTRAINT TO_SIMPLE_CUSTOMER_ORDER FOREIGN KEY (CUSTOMER_ORDER_ID_REF) REFERENCES SIMPLE_CUSTOMER_ORDER(CUSTOMER_ORDER_ID)",
                "ALTER TABLE SIMPLE_LINE_ITEM ADD CONSTRAINT FROM_SIMPLE_TO_PRODUCT FOREIGN KEY (PRODUCT_ID, EXTRA_ID) REFERENCES PRODUCT(PRODUCT_ID, EXTRA_ID)",
                "ALTER TABLE SIMPLE_CUSTOMER_ORDER ADD CONSTRAINT FROM_SIMPLE_TO_CUSTOMER FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID)",
                "CREATE TABLE LINE_ITEM (CUSTOMER_ID_REF VARCHAR(256) NOT NULL, ORDER_NUMBER FLOAT NOT NULL, PRODUCT_ID VARCHAR(256) NOT NULL, EXTRA_PROD_ID VARCHAR(256) NOT NULL, QUANTITY FLOAT, PRIMARY KEY (CUSTOMER_ID_REF, ORDER_NUMBER, PRODUCT_ID, EXTRA_PROD_ID))",
                "CREATE TABLE CUSTOMER_ORDER (CUSTOMER_ID VARCHAR(256) NOT NULL, ORDER_NUMBER FLOAT NOT NULL, ORDER_DATE DATE, PRIMARY KEY (CUSTOMER_ID, ORDER_NUMBER))",                
                "ALTER TABLE LINE_ITEM ADD CONSTRAINT TO_CUSTOMER_ORDER FOREIGN KEY (CUSTOMER_ID_REF, ORDER_NUMBER) REFERENCES CUSTOMER_ORDER(CUSTOMER_ID, ORDER_NUMBER)",
                "ALTER TABLE LINE_ITEM ADD CONSTRAINT TO_PRODUCT FOREIGN KEY (PRODUCT_ID, EXTRA_PROD_ID) REFERENCES PRODUCT(PRODUCT_ID, EXTRA_ID)",
                "ALTER TABLE CUSTOMER_ORDER ADD CONSTRAINT TO_CUSTOMER FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID)",                
        };
    
	static final String[] DATA_SQL = new String[]  {
                "INSERT INTO PRODUCT (PRODUCT_ID, EXTRA_ID, DESCRIPTION, PRICE, NUMBER_AVAILABLE) VALUES ('PC', '0', 'My PC', 100.0, 23)",
                "INSERT INTO PRODUCT (PRODUCT_ID, EXTRA_ID, DESCRIPTION, PRICE, NUMBER_AVAILABLE) VALUES ('MS', '1', 'My Mouse', 101.0, 23)",
                "INSERT INTO CUSTOMER (CUSTOMER_ID, NAME, ADDRESS) VALUES ('MAX', 'Max Rydahl Andersen', 'Neuchatel')",
                "INSERT INTO CUSTOMER_ORDER (CUSTOMER_ID, ORDER_NUMBER, ORDER_DATE) values ('MAX', 1, null)", 
                "INSERT INTO LINE_ITEM (CUSTOMER_ID_REF, ORDER_NUMBER, PRODUCT_ID, EXTRA_PROD_ID, QUANTITY) VALUES ('MAX', 1, 'PC', '0', 10)",
                "INSERT INTO LINE_ITEM (CUSTOMER_ID_REF, ORDER_NUMBER, PRODUCT_ID, EXTRA_PROD_ID, QUANTITY) VALUES ('MAX', 1, 'MS', '1', 12)",
        };

	static final String[] DROP_SQL = new String[]  {
                "ALTER TABLE LINE_ITEM DROP CONSTRAINT TO_CUSTOMER_ORDER",
                "ALTER TABLE LINE_ITEM DROP CONSTRAINT TO_PRODUCT",
                "ALTER TABLE CUSTOMER_ORDER DROP CONSTRAINT TO_CUSTOMER",
                "ALTER TABLE SIMPLE_LINE_ITEM DROP CONSTRAINT TO_SIMPLE_CUSTOMER_ORDER",
                "ALTER TABLE SIMPLE_LINE_ITEM DROP CONSTRAINT FROM_SIMPLE_TO_PRODUCT",
                "ALTER TABLE SIMPLE_CUSTOMER_ORDER DROP CONSTRAINT FROM_SIMPLE_TO_CUSTOMER",
                "DROP TABLE SIMPLE_LINE_ITEM ",
                "DROP TABLE PRODUCT ",
                "DROP TABLE CUSTOMER ",
                "DROP TABLE SIMPLE_CUSTOMER_ORDER ",
                "DROP TABLE CUSTOMER_ORDER ",                
                "DROP TABLE LINE_ITEM ",                           
        };
     
	private JDBCMetaDataConfiguration jmdcfg = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);;
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
    public void testMultiColumnForeignKeys() {
        Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "LINE_ITEM") );
        Assert.assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(
        		table, 
        		JdbcUtil.toIdentifier(this, "TO_CUSTOMER_ORDER") );     
        Assert.assertNotNull(foreignKey);                
        Assert.assertEquals(
        		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))),
        		foreignKey.getReferencedEntityName() );
        Assert.assertEquals(
        		JdbcUtil.toIdentifier(this, "LINE_ITEM"), 
        		foreignKey.getTable().getName() );       
        Assert.assertEquals(2,foreignKey.getColumnSpan() );
        Assert.assertEquals(foreignKey.getColumn(0).getName(), "CUSTOMER_ID_REF");
        Assert.assertEquals(foreignKey.getColumn(1).getName(), "ORDER_NUMBER");      
        Table tab = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"));
        Assert.assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMER_ID");
        Assert.assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDER_NUMBER");     
        PersistentClass lineMapping = jmdcfg.getMetadata().getEntityBinding(
        		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "LINE_ITEM"))));       
        Assert.assertEquals(4,lineMapping.getIdentifier().getColumnSpan() );
        Iterator<?> columnIterator = lineMapping.getIdentifier().getColumnIterator();
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "CUSTOMER_ID_REF");
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "ORDER_NUMBER");
     }
     
	@Test
    public void testPossibleKeyManyToOne() {
         PersistentClass product = jmdcfg.getMetadata().getEntityBinding( 
         		jmdcfg.getReverseEngineeringStrategy().tableToClassName(
        				new TableIdentifier(
        						null, 
        						null, 
        						JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"))));         
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
						"CUSTOMER_ID"), 
				id.getName() );
         Assert.assertEquals(
 				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"ORDER_NUMBER"), 
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
        						JdbcUtil.toIdentifier(this, "PRODUCT"))));                 
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
						"PRODUCT_ID"), 
        		id.getName() );
        Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().columnToPropertyName(
						null, 
						"EXTRA_ID"), 
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
        JdbcUtil.populateDatabase(this);
        session.createQuery("from LineItem").getResultList();
        List<?> list = session.createQuery("from Product").getResultList();
        Assert.assertEquals(2,list.size() );
        list = session
        		.createQuery("select li.customerOrder.id from LineItem as li")
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
     

