/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.test.TestHelper;
;

/**
 * based on VersioningTest
 * requires a default package to be easily added to classloader
 * steps:
 * 1- build mappings from jdbc (see table used in VersioningTest, some has version, some timestamp,...
 * 2- use *annotated* pojo exporter
 * 3- check if generated classes compile, add them to classloader
 * 4- make a derived configuration from annotated classes
 * 5- test the derived configuration
 * 
 * @author anthony
 */
public class VersioningForJDK50Test extends VersioningTest {
    protected void configure(JDBCMetaDataConfiguration cfgToConfigure) {        
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c));
        cfgToConfigure.setReverseEngineeringStrategy(c);
    }
	
	public void testGenerateJPA() throws Exception{
        
        cfg.buildMappings();  
        
		POJOExporter exporter = new POJOExporter(cfg, getOutputDir() );
		exporter.setTemplatePath(new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
		
		File file = new File( "ejb3compilable" );
		file.mkdir();
		
		ArrayList list = new ArrayList();
		List jars = new ArrayList();
		jars.add("ejb3-persistence.jar");
		jars.add("hibernate-annotations.jar");
		TestHelper.compile(getOutputDir(), file, TestHelper.visitAllFiles(getOutputDir(), list), "1.5", TestHelper.buildClasspath(jars));
		
		URL[] urls = new URL[]{file.toURL()};
		Thread currentThread = Thread.currentThread();
		URLClassLoader ucl = new URLClassLoader( urls, currentThread.getContextClassLoader() );
		currentThread.setContextClassLoader( ucl );

		
		Class withversionClazz = ucl.loadClass("Withversion");
		Class withrealtimestampClazz = ucl.loadClass("Withrealtimestamp");
		Class noversionClazz = ucl.loadClass("Noversion");
		Class withfaketimestampClazz = ucl.loadClass("Withfaketimestamp");
		AnnotationConfiguration derived = new AnnotationConfiguration();
		derived.addAnnotatedClass( withversionClazz );
		derived.addAnnotatedClass( withrealtimestampClazz );
		derived.addAnnotatedClass( noversionClazz );
		derived.addAnnotatedClass( withfaketimestampClazz );
		
		testVersioningInDerivedCfg(  derived );
		
	}
	
	public static Test suite() {
		return new TestSuite(VersioningForJDK50Test.class);
	}
}
