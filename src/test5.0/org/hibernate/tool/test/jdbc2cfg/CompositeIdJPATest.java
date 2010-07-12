/*
 * Created on 13-Jan-2005
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

/**
 * @author max
 *
 */
public class CompositeIdJPATest extends JDBCMetaDataBinderTestCase {

	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		super.configure( configuration );		
	}
    protected String[] getCreateSQL() {
        
        return new String[] {
        		"CREATE TABLE Person ( id identity, name varchar(100) NOT NULL, address varchar(255), city varchar(20) default NULL, PRIMARY KEY  (id))" 
        		,
        		"create table modelyear ( make varchar(20), model varchar(30), year int, name varchar(30), primary key (make, model, year))",
    		    "CREATE TABLE Vehicle ( " + 
    		    "    		state varchar(2) NOT NULL, " + 
    		    "    		registration varchar(8) NOT NULL, " + 
    		    "    	    v_make varchar(20) , " + 
    		    "    		v_model varchar(30) , " + 
    		    "    		v_year int , " + 
    		    "    			  owner int , " + 
    		    "    	    PRIMARY KEY  (registration), " + 
    		    //"    		KEY  (make,model,year), " + 
    		    //"    			  KEY  (owner), " + 
    		    "    			  constraint vehicle_owner FOREIGN KEY (owner) REFERENCES person (id), " + 
    		    "    			  constraint vehicle_modelyear FOREIGN KEY (v_make, v_model, v_year) REFERENCES modelyear (make, model, year) " + 
    		    "    			)" ,
    		       };
    }
    
      protected String[] getDropSQL() {
        return new String[] {   
        		"drop table Vehicle",
        		"drop table modelyear",    
        		"drop table Person",
        		
                            
        };
    }
     
     public void testMultiColumnForeignKeys() {
        
    	 Table vehicleTable = getTable(identifier("Vehicle"));
    	 
    	 Iterator foreignKeyIterator = vehicleTable.getForeignKeyIterator();
    	 assertHasNext(2, foreignKeyIterator);

    	 ForeignKey foreignKey = getForeignKey(vehicleTable, identifier("vehicle_owner"));
    	 assertEquals(foreignKey.getColumnSpan(), 1);
    	 
    	 foreignKey = getForeignKey(vehicleTable, identifier("vehicle_modelyear"));
    	 assertEquals(foreignKey.getColumnSpan(), 3);
    	 
    	 PersistentClass vehicle = getConfiguration().getClassMapping("Vehicle");
    	 EntityPOJOClass vechiclePojo = new EntityPOJOClass(vehicle, new Cfg2JavaTool());
    	 assertNotNull(vechiclePojo.getDecoratedObject());
    	 
    	 Property property = vehicle.getProperty("modelyear");
    	 assertNotNull(property);
    	 String generateJoinColumnsAnnotation = vechiclePojo.generateJoinColumnsAnnotation(property, cfg);
    	 assertTrue(generateJoinColumnsAnnotation.indexOf("referencedColumnName=\"MAKE\"")>0);
    	
    	 
     }
     
    public void testJPAGeneration() throws Exception {
    	getConfiguration().buildMappings();
    	 POJOExporter exporter = new POJOExporter(getConfiguration(), getOutputDir());
    	 Properties p = new Properties();
    	 p.setProperty("jdk5", "true");
    	 p.setProperty("ejb3", "true");
    	 
    	 exporter.setProperties(p);
    	 exporter.start();
    	 
    	 File file = new File( "ejb3compilable" );
 		file.mkdir();

 		ArrayList list = new ArrayList();
 		List jars = new ArrayList();
 		addAnnotationJars(jars);
 		
 		new ExecuteContext(getOutputDir(), file, jars) {

 			protected void execute() throws Exception {
 				AnnotationConfiguration configuration = new AnnotationConfiguration();
 				configuration.addAnnotatedClass( getUcl().loadClass( "Vehicle" ) );
 				configuration.addAnnotatedClass( getUcl().loadClass( "Person" ) );
 				configuration.addAnnotatedClass( getUcl().loadClass( "Modelyear" ) );

 				SessionFactory sf = configuration.buildSessionFactory();
 				Session s = sf.openSession();
                 Query createQuery = s.createQuery("from java.lang.Object");
                 createQuery.list();
 				s.close();
 				sf.close();
 				
 			}
 			
 		}.run();
     }
     
    private void addAnnotationJars(List jars) {
		jars.add( "ejb3-persistence.jar" );
		jars.add( "hibernate-annotations.jar" );
		jars.add( "hibernate-commons-annotations.jar" );
		jars.add( "hibernate3.jar" );
		jars.add( "dom4j-1.6.1.jar" );
		jars.add( "commons-logging-1.0.4.jar" );
		
	}

     
     protected void tearDown() throws Exception {
    	// TODO Auto-generated method stub
    	super.tearDown();
    }
	 public static Test suite() {
			return new TestSuite(CompositeIdJPATest.class);
		}
}
     

