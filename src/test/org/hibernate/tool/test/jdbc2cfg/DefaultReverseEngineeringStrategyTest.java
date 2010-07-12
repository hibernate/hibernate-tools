/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;

/**
 * @author max
 *
 */
public class DefaultReverseEngineeringStrategyTest extends TestCase {
	
	ReverseEngineeringStrategy rns = new DefaultReverseEngineeringStrategy();
	
	public void testColumnKeepCase() {
		assertEquals("name", rns.columnToPropertyName(null, "name") );		
		assertEquals("nameIsValid", rns.columnToPropertyName(null, "nameIsValid") );
	}
	
	public void testColumnUpperToLower() {
		assertEquals("name", rns.columnToPropertyName(null, "NAME") );
		assertEquals("name", rns.columnToPropertyName(null, "Name") );
	}
	
	public void testColumnRemoveChars() {
		assertEquals("name", rns.columnToPropertyName(null, "_Name") );
		assertEquals("name", rns.columnToPropertyName(null, "_name") );
		assertEquals("name", rns.columnToPropertyName(null, "_name") );
	}
	
	public void testColumnToCamelCase() {
		assertEquals("labelForField", rns.columnToPropertyName(null, "LABEL_FOR_FIELD") );
		assertEquals("nameToMe", rns.columnToPropertyName(null, "_name-To-Me") );
	}
	
	public void testColumnChangeCamelCase() {
		assertEquals("labelForField", rns.columnToPropertyName(null, "LabelForField") );	
	}
	
	public void testTableKeepCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("SickPatients") ) );
	}
	
	public void testTableUpperToLower() {
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("PATIENTS") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("patients") ) );
	}
	
	public void testTableRemoveChars() {
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_Patients") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_patients") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_patients") ) );
		assertEquals("PatientInterventions", rns.tableToClassName(new TableIdentifier("_PATIENT_INTERVENTIONS") ) );
	}
	
	public void testTableToCamelCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("Sick_Patients") ) );
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("_Sick-Patients") ) );
	}
	
	public void testTableKeepCamelCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("SickPatients") ) );
	}
    
    public void testBasicForeignKeyNames() {
        assertEquals("products", rns.foreignKeyToCollectionName("something", new TableIdentifier("product"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("willies", rns.foreignKeyToCollectionName("something", new TableIdentifier("willy"), null, new TableIdentifier("order"), null, true ) );
		assertEquals("boxes", rns.foreignKeyToCollectionName("something", new TableIdentifier("box"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("order", rns.foreignKeyToEntityName("something", new TableIdentifier("product"), null, new TableIdentifier("order"), null, true ) );
    }
	
    public void testCustomClassNameStrategyWithCollectionName() {
    	
    	ReverseEngineeringStrategy custom = new DelegatingReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()) {
    		public String tableToClassName(TableIdentifier tableIdentifier) {
    			return super.tableToClassName( tableIdentifier ) + "Impl";
    		}
    	};

    	custom.setSettings( new ReverseEngineeringSettings(custom) );
    	
    	TableIdentifier productTable = new TableIdentifier("product");
		assertEquals("ProductImpl", custom.tableToClassName( productTable ));
    	
        assertEquals("productImpls", custom.foreignKeyToCollectionName("something", productTable, null, new TableIdentifier("order"), null, true ) );
        /*assertEquals("willies", custom.foreignKeyToCollectionName("something", new TableIdentifier("willy"), null, new TableIdentifier("order"), null, true ) );
		assertEquals("boxes", custom.foreignKeyToCollectionName("something", new TableIdentifier("box"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("order", custom.foreignKeyToEntityName("something", productTable, null, new TableIdentifier("order"), null, true ) );*/
    }
    
    public void testForeignKeyNamesToPropertyNames() {
    	
    	String fkName = "something";
		TableIdentifier fromTable = new TableIdentifier("company");
		List fromColumns = new ArrayList();
		
		TableIdentifier toTable = new TableIdentifier("address");
		List toColumns = new ArrayList();
		
		assertEquals("address", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		assertEquals("companies", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		
		fkName = "billing";
		fromColumns.clear();		
		fromColumns.add(new Column("bill_adr") );
		assertEquals("addressByBillAdr", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		assertEquals("companiesForBillAdr", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		
		fromColumns.add(new Column("bill_adrtype") );
		assertEquals("addressByBilling", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		assertEquals("companiesForBilling", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
    }

    public void testPreferredTypes() {
    	assertEquals("int",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, false, false));
    	assertEquals("because nullable it should not be int", "java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false));
    	assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.NUMERIC,0,9,0, true, false));
       	assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false));			
       	assertEquals("serializable",rns.columnToHibernateTypeName(new TableIdentifier("sdf"), "bogus",-567,0,0,0, false, false));
       	
       	assertEquals("string",rns.columnToHibernateTypeName(new TableIdentifier("sdf"), "bogus",12,0,0,0, false, false));
    }
    
    public void testReservedKeywordsHandling() {
    	assertEquals("class_", rns.columnToPropertyName(new TableIdentifier("blah"), "class"));    	
    }
     
	
	public static Test suite() {
		return new TestSuite(DefaultReverseEngineeringStrategyTest.class);
	}
}
