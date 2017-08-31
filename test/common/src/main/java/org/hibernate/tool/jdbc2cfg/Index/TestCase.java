/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.Index;

import java.util.Iterator;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tools.test.util.HibernateUtil;
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

	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testUniqueKey() {	
		Table table = HibernateUtil.getTable(
				jmdcfg.getMetadata(), 
				JdbcUtil.toIdentifier(this, "WITH_INDEX") );		
		UniqueKey uniqueKey = table.getUniqueKey(
				JdbcUtil.toIdentifier(this, "OTHER_IDX") );
		Assert.assertNotNull(uniqueKey);
		Assert.assertEquals(1, uniqueKey.getColumnSpan() );	
		Column keyCol = uniqueKey.getColumn(0);
		Assert.assertTrue(keyCol.isUnique() );
		Assert.assertSame(keyCol, table.getColumn(keyCol) );		
	}
	
	@Test
	public void testWithIndex() {		
		Table table = HibernateUtil.getTable(
				jmdcfg.getMetadata(), 
				JdbcUtil.toIdentifier(this, "WITH_INDEX"));
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "WITH_INDEX"), 
				JdbcUtil.toIdentifier(this, table.getName()));	
		Assert.assertNull("there should be no pk", table.getPrimaryKey() );
		Iterator<Index> iterator = table.getIndexIterator();
		int cnt=0;
		while(iterator.hasNext() ) {
			iterator.next();
			cnt++;
		}
		Assert.assertEquals(1, cnt);	
		Index index = table.getIndex(JdbcUtil.toIdentifier(this, "MY_INDEX") );
		Assert.assertNotNull("No index ?", index);
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "MY_INDEX"), 
				JdbcUtil.toIdentifier(this, index.getName()));	
		Assert.assertEquals(2, index.getColumnSpan() );	
		Assert.assertSame(index.getTable(), table);
		Iterator<Column> cols = index.getColumnIterator();
		Column col1 = cols.next();
		Column col2 = cols.next();	
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "FIRST"), 
				JdbcUtil.toIdentifier(this, col1.getName()));
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "THIRD"), 
				JdbcUtil.toIdentifier(this, col2.getName()));		
		Column example = new Column();
		example.setName(col2.getName() );
		Assert.assertSame(
				"column with same name should be same instance!", 
				table.getColumn(example), col2);			
	}
	
}
