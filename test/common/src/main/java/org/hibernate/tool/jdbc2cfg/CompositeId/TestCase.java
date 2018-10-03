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
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.export.hbm.HibernateMappingExporter;
import org.hibernate.tool.internal.reveng.DefaultReverseEngineeringStrategy;
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

	private MetadataDescriptor metadataDescriptor = null;
	private ReverseEngineeringStrategy reverseEngineeringStrategy = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
		metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(reverseEngineeringStrategy, null, true);
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
    public void testMultiColumnForeignKeys() {
		Metadata metadata = metadataDescriptor.createMetadata();
        Table table = HibernateUtil.getTable(
        		metadata, 
        		JdbcUtil.toIdentifier(this, "LINE_ITEM") );
        Assert.assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(
        		table, 
        		JdbcUtil.toIdentifier(this, "TO_CUSTOMER_ORDER") );     
        Assert.assertNotNull(foreignKey);                
        Assert.assertEquals(
        		reverseEngineeringStrategy.tableToClassName(
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
        Table tab = HibernateUtil.getTable(
        		metadata, 
        		JdbcUtil.toIdentifier(this, "CUSTOMER_ORDER"));
        Assert.assertEquals(tab.getPrimaryKey().getColumn(0).getName(), "CUSTOMER_ID");
        Assert.assertEquals(tab.getPrimaryKey().getColumn(1).getName(), "ORDER_NUMBER");     
        PersistentClass lineMapping = metadata.getEntityBinding(
        		reverseEngineeringStrategy.tableToClassName(
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
         PersistentClass product = metadataDescriptor.createMetadata().getEntityBinding( 
         		reverseEngineeringStrategy.tableToClassName(
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
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"CUSTOMER_ID"), 
				id.getName() );
         Assert.assertEquals(
 				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"ORDER_NUMBER"), 
        		 extraId.getName() );         
         Assert.assertFalse(id.getValue() instanceof ManyToOne);
         Assert.assertFalse(extraId.getValue() instanceof ManyToOne);
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
        Assert.assertTrue(identifierProperty.getValue() instanceof Component);       
        Component cmpid = (Component) identifierProperty.getValue();        
        Assert.assertEquals(2, cmpid.getPropertySpan() );       
        Iterator<?> iter = cmpid.getPropertyIterator();
        Property id = (Property) iter.next();
        Property extraId = (Property) iter.next();     
        Assert.assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
						null, 
						"PRODUCT_ID"), 
        		id.getName() );
        Assert.assertEquals(
				reverseEngineeringStrategy.columnToPropertyName(
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
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
        exporter.setOutputDirectory(testFolder);
        Exporter javaExp = ExporterFactory.createExporter(ExporterType.POJO);
		javaExp.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
        javaExp.setOutputDirectory(testFolder);
        exporter.start();
        javaExp.start();      
        JavaUtil.compile(testFolder);        
        URL[] urls = new URL[] { testFolder.toURI().toURL() };
        URLClassLoader ucl = new URLClassLoader(
        		urls, 
        		Thread.currentThread().getContextClassLoader());
        File[] files = new File[6];
        files[0] = new File(testFolder, "SimpleCustomerOrder.hbm.xml");
        files[1] = new File(testFolder, "SimpleLineItem.hbm.xml");
        files[2] = new File(testFolder, "Product.hbm.xml");
        files[3] = new File(testFolder, "Customer.hbm.xml");
        files[4] = new File(testFolder, "LineItem.hbm.xml");
        files[5] = new File(testFolder, "CustomerOrder.hbm.xml");
        Thread.currentThread().setContextClassLoader(ucl);
        SessionFactory factory = MetadataDescriptorFactory
        		.createNativeDescriptor(null, files, null)
        		.createMetadata()
        		.buildSessionFactory();
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
     

