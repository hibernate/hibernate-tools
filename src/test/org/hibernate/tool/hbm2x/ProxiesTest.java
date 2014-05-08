/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.ImportContextImpl;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 * 
 */
public class ProxiesTest extends NonReflectiveTestCase {

	private ArtifactCollector artifactCollector;
	
	public ProxiesTest(String name) {
		super( name, "hbm2javaoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();

		Exporter exporter = new POJOExporter( getCfg(), getOutputDir() );
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}

	protected String[] getMappings() {
		return new String[] { "Customer.hbm.xml", "Order.hbm.xml",
				"LineItem.hbm.xml", "Product.hbm.xml", "HelloWorld.hbm.xml", "Train.hbm.xml", "Passenger.hbm.xml", "Proxies.hbm.xml" };
	}
	
	public void testProxies() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass classMapping = getCfg().getClassMapping( "proxies.ClassA" );
		Property property = classMapping.getProperty("myClassB");
		
		EntityPOJOClass pj = new EntityPOJOClass(classMapping, c2j);
		String javaTypeName = pj.getJavaTypeName(property, true);
		assertEquals("proxies.ProxyB", javaTypeName);
	}
	
	
}
