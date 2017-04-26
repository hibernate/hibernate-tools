/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.util.EnumSet;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;
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
 *
 */
public class ForeignKeysTest {

	private static final String[] CREATE_SQL = new String[] {
				"create table master (id char not null, name varchar(20), primary key (id) )",					
				"create table child  ( childid character not null, masterref character, primary key (childid), foreign key (masterref) references master(id) )",				
				"create table connection  ( conid int, name varchar(50), masterref character, childref1 character, childref2 character, primary key(conid), " +
				"    constraint con2master foreign key (masterref) references master(id)," +
				"    constraint childref1 foreign key  (childref1) references child(childid), " +
				"    constraint childref2 foreign key  (childref2) references child(childid))"
				// todo - link where pk is fk to something						
		};

	private static final String[] DROP_SQL = new String[] {
				"drop table connection",				
				"drop table child",
				"drop table master",					
		};
	
	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeDDL(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeDDL(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
	}	
	
	@Test
	public void testMultiRefs() {		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "connection") );		
		ForeignKey foreignKey = HibernateUtil.getForeignKey(
				table, 
				JdbcUtil.toIdentifier(this, "con2master") );	
		Assert.assertNotNull(foreignKey);			
		Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().tableToClassName(
						new TableIdentifier(null, null, "master")),
				foreignKey.getReferencedEntityName() );
        Assert.assertEquals(
        		JdbcUtil.toIdentifier(this, "connection"), 
        		foreignKey.getTable().getName() );	
		Assert.assertEquals(
				jmdcfg.getTable(JdbcUtil.toIdentifier(this, "master") ), 
				foreignKey.getReferencedTable() );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "childref1") ) );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "childref2") ) );
		Assert.assertNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "dummy") ) );
		JUnitUtil.assertIteratorContainsExactly(null, table.getForeignKeyIterator(), 3);
	}
	
	@Test
	public void testMasterChild() {		
		Assert.assertNotNull(jmdcfg.getTable(JdbcUtil.toIdentifier(this, "master")));
		Table child = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "child") );	
		Iterator<?> iterator = child.getForeignKeyIterator();		
		ForeignKey fk = (ForeignKey) iterator.next();		
		Assert.assertFalse("should only be one fk", iterator.hasNext() );	
		Assert.assertEquals(1, fk.getColumnSpan() );
		Assert.assertSame(
				fk.getColumn(0), 
				child.getColumn(
						new Column(JdbcUtil.toIdentifier(this, "masterref"))));		
	}
	
	@Test
	public void testExport() {
		SchemaExport schemaExport = new SchemaExport();
		Metadata metadata = jmdcfg.getMetadata();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.STDOUT );
		schemaExport.create(targetTypes, metadata);		
	}
	
}
