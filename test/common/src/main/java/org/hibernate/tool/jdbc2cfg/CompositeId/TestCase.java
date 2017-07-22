/*
 * Created on 13-Jan-2005
 *
 */
package org.hibernate.tool.jdbc2cfg.CompositeId;

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
 */
public class TestCase {

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
        Exporter exporter = new HibernateMappingExporter();	
        exporter.setConfiguration(jmdcfg);
        exporter.setOutputDirectory(testFolder);
        Exporter javaExp = new POJOExporter();
        javaExp.setConfiguration(jmdcfg);
        javaExp.setOutputDirectory(testFolder);
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
     

