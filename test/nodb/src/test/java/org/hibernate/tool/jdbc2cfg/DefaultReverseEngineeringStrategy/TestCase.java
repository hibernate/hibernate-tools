/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.DefaultReverseEngineeringStrategy;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.tool.api.reveng.DefaultRevengStrategy;
import org.hibernate.tool.api.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.api.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.DelegatingReverseEngineeringStrategy;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author max
 * @author koen
 *
 */
public class TestCase {
	
	ReverseEngineeringStrategy rns = new DefaultRevengStrategy();
	
	@Test
	public void testColumnKeepCase() {
		Assert.assertEquals("name", rns.columnToPropertyName(null, "name") );		
		Assert.assertEquals("nameIsValid", rns.columnToPropertyName(null, "nameIsValid") );
	}
	
	@Test
	public void testColumnUpperToLower() {
		Assert.assertEquals("name", rns.columnToPropertyName(null, "NAME") );
		Assert.assertEquals("name", rns.columnToPropertyName(null, "Name") );
	}
	
	@Test
	public void testColumnRemoveChars() {
		Assert.assertEquals("name", rns.columnToPropertyName(null, "_Name") );
		Assert.assertEquals("name", rns.columnToPropertyName(null, "_name") );
		Assert.assertEquals("name", rns.columnToPropertyName(null, "_name") );
	}
	
	@Test
	public void testColumnToCamelCase() {
		Assert.assertEquals("labelForField", rns.columnToPropertyName(null, "LABEL_FOR_FIELD") );
		Assert.assertEquals("nameToMe", rns.columnToPropertyName(null, "_name-To-Me") );
	}
	
	@Test
	public void testColumnChangeCamelCase() {
		Assert.assertEquals("labelForField", rns.columnToPropertyName(null, "LabelForField") );	
	}
	
	@Test
	public void testTableKeepCase() {
		Assert.assertEquals("SickPatients", rns.tableToClassName(TableIdentifier.create(null, null, "SickPatients") ) );
	}
	
	@Test
	public void testTableUpperToLower() {
		Assert.assertEquals("Patients", rns.tableToClassName(TableIdentifier.create(null, null, "PATIENTS") ) );
		Assert.assertEquals("Patients", rns.tableToClassName(TableIdentifier.create(null, null, "patients") ) );
	}
	
	@Test
	public void testTableRemoveChars() {
		Assert.assertEquals("Patients", rns.tableToClassName(TableIdentifier.create(null, null, "_Patients") ) );
		Assert.assertEquals("Patients", rns.tableToClassName(TableIdentifier.create(null, null, "_patients") ) );
		Assert.assertEquals("Patients", rns.tableToClassName(TableIdentifier.create(null, null, "_patients") ) );
		Assert.assertEquals("PatientInterventions", rns.tableToClassName(TableIdentifier.create(null, null, "_PATIENT_INTERVENTIONS") ) );
	}
	
	@Test
	public void testTableToCamelCase() {
		Assert.assertEquals("SickPatients", rns.tableToClassName(TableIdentifier.create(null, null, "Sick_Patients") ) );
		Assert.assertEquals("SickPatients", rns.tableToClassName(TableIdentifier.create(null, null, "_Sick-Patients") ) );
	}
	
	@Test
	public void testTableKeepCamelCase() {
		Assert.assertEquals("SickPatients", rns.tableToClassName(TableIdentifier.create(null, null, "SickPatients") ) );
	}
    
	@Test
    public void testBasicForeignKeyNames() {
        Assert.assertEquals("products", rns.foreignKeyToCollectionName("something", TableIdentifier.create(null, null, "product"), null, TableIdentifier.create(null, null, "order"), null, true ) );
        Assert.assertEquals("willies", rns.foreignKeyToCollectionName("something", TableIdentifier.create(null, null, "willy"), null, TableIdentifier.create(null, null, "order"), null, true ) );
		Assert.assertEquals("boxes", rns.foreignKeyToCollectionName("something", TableIdentifier.create(null, null, "box"), null, TableIdentifier.create(null, null, "order"), null, true ) );
        Assert.assertEquals("order", rns.foreignKeyToEntityName("something", TableIdentifier.create(null, null, "product"), null, TableIdentifier.create(null, null, "order"), null, true ) );
    }
	
	@Test
    public void testCustomClassNameStrategyWithCollectionName() {
    	
    	ReverseEngineeringStrategy custom = new DelegatingReverseEngineeringStrategy(new DefaultRevengStrategy()) {
    		public String tableToClassName(TableIdentifier tableIdentifier) {
    			return super.tableToClassName( tableIdentifier ) + "Impl";
    		}
    	};

    	custom.setSettings( new ReverseEngineeringSettings(custom) );
    	
    	TableIdentifier productTable = TableIdentifier.create(null, null, "product");
		Assert.assertEquals("ProductImpl", custom.tableToClassName( productTable ));
    	
        Assert.assertEquals("productImpls", custom.foreignKeyToCollectionName("something", productTable, null, TableIdentifier.create(null, null, "order"), null, true ) );
        /*assertEquals("willies", custom.foreignKeyToCollectionName("something", new TableIdentifier("willy"), null, new TableIdentifier("order"), null, true ) );
		assertEquals("boxes", custom.foreignKeyToCollectionName("something", new TableIdentifier("box"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("order", custom.foreignKeyToEntityName("something", productTable, null, new TableIdentifier("order"), null, true ) );*/
    }
    
	@Test
    public void testForeignKeyNamesToPropertyNames() {
    	
    	String fkName = "something";
		TableIdentifier fromTable = TableIdentifier.create(null, null, "company");
		List<Column> fromColumns = new ArrayList<Column>();
		
		TableIdentifier toTable = TableIdentifier.create(null, null, "address");
		List<Column> toColumns = new ArrayList<Column>();
		
		Assert.assertEquals("address", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		Assert.assertEquals("companies", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		
		fkName = "billing";
		fromColumns.clear();		
		fromColumns.add(new Column("bill_adr") );
		Assert.assertEquals("addressByBillAdr", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		Assert.assertEquals("companiesForBillAdr", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		
		fromColumns.add(new Column("bill_adrtype") );
		Assert.assertEquals("addressByBilling", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		Assert.assertEquals("companiesForBilling", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
    }

	@Test
    public void testPreferredTypes() {
    	Assert.assertEquals("int",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, false, false));
    	Assert.assertEquals("because nullable it should not be int", "java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false));
    	Assert.assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.NUMERIC,0,9,0, true, false));
       	Assert.assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false));			
       	Assert.assertEquals("serializable",rns.columnToHibernateTypeName(TableIdentifier.create(null, null, "sdf"), "bogus",-567,0,0,0, false, false));
       	
       	Assert.assertEquals("string",rns.columnToHibernateTypeName(TableIdentifier.create(null, null, "sdf"), "bogus",12,0,0,0, false, false));
    }
    
	@Test
    public void testReservedKeywordsHandling() {
    	Assert.assertEquals("class_", rns.columnToPropertyName(TableIdentifier.create(null, null, "blah"), "class"));    	
    }
     
}
