package org.hibernate.tool.hbm2x;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

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
		return new String[] { "Proxies.hbm.xml" };
	}
	
	public void testProxies() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass classMapping = getCfg().getClassMapping( "proxies.ClassA" );
		Property property = classMapping.getProperty("myClassB");
		
		EntityPOJOClass pj = new EntityPOJOClass(classMapping, c2j);
		String javaTypeName = pj.getJavaTypeName(property, true);
		assertEquals("ProxyB", javaTypeName);
	}
	
	
}
