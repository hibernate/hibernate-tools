/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.ForeignKeys;

import java.util.EnumSet;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;
	private RevengStrategy reverseEngineeringStrategy = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		reverseEngineeringStrategy = new DefaultStrategy();
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(reverseEngineeringStrategy, null)
				.createMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}	
	
	@Test
	public void testMultiRefs() {		
		Table table = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "CONNECTION") );		
		ForeignKey foreignKey = HibernateUtil.getForeignKey(
				table, 
				JdbcUtil.toIdentifier(this, "CON2MASTER") );	
		Assert.assertNotNull(foreignKey);			
		Assert.assertEquals(
				reverseEngineeringStrategy.tableToClassName(
						TableIdentifier.create(null, null, "MASTER")),
				foreignKey.getReferencedEntityName() );
        Assert.assertEquals(
        		JdbcUtil.toIdentifier(this, "CONNECTION"), 
        		foreignKey.getTable().getName() );	
		Assert.assertEquals(
				HibernateUtil.getTable(
						metadata, 
						JdbcUtil.toIdentifier(this, "MASTER") ), 
				foreignKey.getReferencedTable() );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "CHILDREF1") ) );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "CHILDREF2") ) );
		Assert.assertNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "DUMMY") ) );
		JUnitUtil.assertIteratorContainsExactly(null, table.getForeignKeyIterator(), 3);
	}
	
	@Test
	public void testMasterChild() {		
		Assert.assertNotNull(HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "MASTER")));
		Table child = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "CHILD") );	
		Iterator<?> iterator = child.getForeignKeyIterator();		
		ForeignKey fk = (ForeignKey) iterator.next();		
		Assert.assertFalse("should only be one fk", iterator.hasNext() );	
		Assert.assertEquals(1, fk.getColumnSpan() );
		Assert.assertSame(
				fk.getColumn(0), 
				child.getColumn(
						new Column(JdbcUtil.toIdentifier(this, "MASTERREF"))));		
	}
	
	@Test
	public void testExport() {
		SchemaExport schemaExport = new SchemaExport();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.STDOUT );
		schemaExport.create(targetTypes, metadata);		
	}
	
}
