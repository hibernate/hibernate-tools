package org.hibernate.tool.hbm2x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.test.TestHelper;

public class Hbm2JavaEqualsTest extends NonReflectiveTestCase {
	
	private static final String TEST_ENTITY_HBM_XML = 
	        "<?xml version='1.0'?>                                       "+
	        "<!DOCTYPE hibernate-mapping PUBLIC                          "+
	        "	'-//Hibernate/Hibernate Mapping DTD 3.0//EN'             "+
	        "   'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>"+
			"<hibernate-mapping package='org.hibernate.tool.hbm2x'>      "+
	        "  <class name='UnProxiedTestEntity'>                        "+
		    "    <id name='id' type='int'>                               "+
		    "      <meta attribute='use-in-equals'>true</meta>           "+
		    "    </id>                                                   "+
	        "  </class>                                                  "+
	        "  <class name='ProxiedTestEntity' proxy='TestEntityProxy'>  "+
		    "    <id name='id' type='int'>                               "+
		    "      <meta attribute='use-in-equals'>true</meta>           "+
		    "    </id>                                                   "+
	        "  </class>                                                  "+
            "</hibernate-mapping>                                        ";	
	
	private static final String TEST_ENTITY_PROXY_JAVA = 
			"package org.hibernate.tool.hbm2x;"+ System.lineSeparator() +
	        "interface TestEntityProxy {      "+ System.lineSeparator() +
			"  int getId();                   "+ System.lineSeparator() +
	        "}                                ";

	public Hbm2JavaEqualsTest(String name) {
		super(name, "hbm2javaoutput");
	}

	protected void setUp() throws Exception {
		// create output folder
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}		
		// export class ProxiedTestEntity.java and UnProxiedTestEntity
		Configuration cfg = new Configuration();
		cfg.addInputStream(new ByteArrayInputStream(TEST_ENTITY_HBM_XML.getBytes()));
		cfg.buildMappings();
		setCfg(cfg);
		new POJOExporter(cfg, getOutputDir()).start();
		// copy interface EntityProxy.java
		File file = new File(getOutputDir(), "org/hibernate/tool/hbm2x/TestEntityProxy.java");
		FileWriter writer = new FileWriter(file);
		writer.write(TEST_ENTITY_PROXY_JAVA);
		writer.close();
		// compile the files
		TestHelper.compile(getOutputDir(), getOutputDir());
	}	
	
	public void testEqualsWithoutProxy() throws Exception {
		// load the entity class and lookup the setId method
        URL[] urls = new URL[] { getOutputDir().toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
        Class<?> entityClass = ucl.loadClass("org.hibernate.tool.hbm2x.UnProxiedTestEntity");
        Method setId = entityClass.getMethod("setId", new Class[] { int.class });

        // create a first entity and check the 'normal' behavior: 
        // - 'true' when comparing against itself
        // - 'false' when comparing against null
        // - 'false' when comparing against an object of a different class
        Object firstEntity = entityClass.newInstance();
        setId.invoke(firstEntity, new Object[] { Integer.MAX_VALUE });
        assertTrue(firstEntity.equals(firstEntity));
        assertFalse(firstEntity.equals(null));
        assertFalse(firstEntity.equals(new Object()));

        // create a second entity and check the 'normal behavior
        // - 'true' if the id property is the same
        // - 'false' if the id property is different
        Object secondEntity = entityClass.newInstance();
        setId.invoke(secondEntity, new Object[] { Integer.MAX_VALUE });
        assertTrue(firstEntity.equals(secondEntity));
        assertTrue(secondEntity.equals(firstEntity));
        setId.invoke(secondEntity, new Object[] { Integer.MIN_VALUE });
        assertFalse(firstEntity.equals(secondEntity));
        assertFalse(secondEntity.equals(firstEntity));

        ucl.close();
	}

	public void testEqualsWithProxy() throws Exception {

		// load the entity and proxy classes, lookup the setId method and create a proxy object
        URL[] urls = new URL[] { getOutputDir().toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
        Class<?> entityClass = ucl.loadClass("org.hibernate.tool.hbm2x.ProxiedTestEntity");
        Class<?> entityProxyInterface = ucl.loadClass("org.hibernate.tool.hbm2x.TestEntityProxy");
        Method setId = entityClass.getMethod("setId", new Class[] { int.class });
        TestEntityProxyInvocationHandler handler = new TestEntityProxyInvocationHandler();
        Object testEntityProxy = Proxy.newProxyInstance(
        		ucl, 
        		new Class[] { entityProxyInterface }, 
        		handler);
        
        // create a first proxied entity and check the 'normal' behavior: 
        // - 'true' when comparing against itself
        // - 'false' when comparing against null
        // - 'false' when comparing against an object of a different class (that is not the proxy class)
        Object firstEntity = entityClass.newInstance();
        setId.invoke(firstEntity, new Object[] { Integer.MAX_VALUE });
        assertTrue(firstEntity.equals(firstEntity));
        assertFalse(firstEntity.equals(null));
        assertFalse(firstEntity.equals(new Object()));

        // create a second proxied entity and check the 'normal behavior
        // - 'true' if the id property is the same
        // - 'false' if the id property is different
        Object secondEntity = entityClass.newInstance();
        setId.invoke(secondEntity, new Object[] { Integer.MAX_VALUE });
        assertTrue(firstEntity.equals(secondEntity));
        assertTrue(secondEntity.equals(firstEntity));
        setId.invoke(secondEntity, new Object[] { Integer.MIN_VALUE });
        assertFalse(firstEntity.equals(secondEntity));
        assertFalse(secondEntity.equals(firstEntity));

        // compare both proxied entities with the proxy
        handler.id = Integer.MAX_VALUE;
        assertTrue(firstEntity.equals(testEntityProxy));
        assertFalse(secondEntity.equals(testEntityProxy));        
        handler.id = Integer.MIN_VALUE;
        assertFalse(firstEntity.equals(testEntityProxy));
        assertTrue(secondEntity.equals(testEntityProxy));
        
        ucl.close();
	}

	@Override protected String[] getMappings() {
		return null;
	}
	
	private class TestEntityProxyInvocationHandler implements InvocationHandler {
		public int id = 0;
		@Override public Object invoke(
				Object proxy, 
				Method method, 
				Object[] args) throws Throwable {
			if ("getId".equals(method.getName())) {
				return id;
			}
			return null;
		}		
	}
	
}
