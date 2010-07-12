/*
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;
import org.hibernate.util.DTDEntityResolver;
import org.hibernate.util.StringHelper;

/**
 * @author max
 *
 */
public class GenerateFromJDBCTest extends JDBCMetaDataBinderTestCase {

	public GenerateFromJDBCTest() {
		super("genfromjdbc");
	}

	
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table master ( id char not null, name varchar(20), primary key (id) )",
				"create table child  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references master(id) )"			
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table child",
				"drop table master",				
		};
	}
	
	protected void configure(JDBCMetaDataConfiguration cfg2configure) {
		
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		cfg2configure.setReverseEngineeringStrategy(configurableNamingStrategy);
	}
	
	public void testGenerateJava() throws SQLException, ClassNotFoundException {
	
		POJOExporter exporter = new POJOExporter(cfg,getOutputDir());		
		exporter.start();
		
		exporter = new POJOExporter(cfg,getOutputDir());				
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
		
		TestHelper.deleteDir(getOutputDir());
	}
	
	public void testGenerateMappings() {
		
		cfg.buildMappings();
		TestHelper.deleteDir(getOutputDir());
		
		Exporter exporter = new HibernateMappingExporter(cfg, getOutputDir());		
		exporter.start();
		
		assertFileAndExists(new File(getOutputDir(), "org/reveng/Child.hbm.xml"));
		
		File file = new File(getOutputDir(), "GeneralHbmSettings.hbm.xml");
		assertTrue(file + " should not exist", !file.exists() );
		
		Configuration derived = new Configuration();
		
		derived.addFile(new File(getOutputDir(), "org/reveng/Child.hbm.xml") );
		derived.addFile(new File(getOutputDir(), "org/reveng/Master.hbm.xml") );
		
		derived.buildMappings();
		
		assertNotNull(derived.getClassMapping("org.reveng.Child") );
		assertNotNull(derived.getClassMapping("org.reveng.Master") );
		TestHelper.deleteDir(getOutputDir());
	}
	
	public void testGenerateCfgXml() throws DocumentException {
		
		Exporter exporter = new HibernateConfigurationExporter(cfg,getOutputDir());
		
		exporter.start();	
				
		assertFileAndExists(new File(getOutputDir(), "hibernate.cfg.xml"));
		
		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(new File(getOutputDir(), "hibernate.cfg.xml"));
		
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-configuration/session-factory/mapping");
		Element[] elements = (Element[]) xpath.selectNodes(document).toArray(new Element[0]);
		assertEquals(2,elements.length);
		
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			assertNotNull(element.attributeValue("resource"));
			assertNull(element.attributeValue("class"));
		}		
	}
	
	public void testGenerateAnnotationCfgXml() throws DocumentException {
		
		HibernateConfigurationExporter exporter = new HibernateConfigurationExporter(cfg,getOutputDir());
		
		exporter.getProperties().setProperty("ejb3", "true");
		
		exporter.start();	
				
		
		
		assertFileAndExists(new File(getOutputDir(), "hibernate.cfg.xml"));
		
		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(new File(getOutputDir(), "hibernate.cfg.xml"));
		
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-configuration/session-factory/mapping");
		Element[] elements = (Element[]) xpath.selectNodes(document).toArray(new Element[0]);
		assertEquals(2, elements.length);
		
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			assertNull(element.attributeValue("resource"));
			assertNotNull(element.attributeValue("class"));
		}		
	}
	
	private SAXReader getSAXReader() {
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setEntityResolver(new DTDEntityResolver() );
    	xmlReader.setValidation(true);
    	return xmlReader;
    }
	
	public void testGenerateDoc() {
		
		DocExporter exporter = new DocExporter(cfg,getOutputDir());
		
		exporter.start();
		
		TestHelper.deleteDir(getOutputDir());
		
	}
	
	public void testPackageNames() {
		Iterator iter = cfg.getClassMappings();
		while (iter.hasNext() ) {
			PersistentClass element = (PersistentClass) iter.next();
			assertEquals("org.reveng", StringHelper.qualifier(element.getClassName() ) );
		}
	}
}
