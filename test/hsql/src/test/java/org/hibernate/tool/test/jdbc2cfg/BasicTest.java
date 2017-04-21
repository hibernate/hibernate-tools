/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;
import java.util.Iterator;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import junit.framework.ComparisonFailure;

/**
 * @author max
 * @author koen
 * 
 */
public class BasicTest {

	private static final String[] CREATE_SQL = new String[] {
			"create table basic ( a int not null, name varchar(20), primary key (a)  )",
			"create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean boolean )",
			"create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )" 
		};

	private static final String[] DROP_SQL = new String[] { 
			"drop table basic", 
			"drop table somecolumnsnopk",
			"drop table multikeyed" 
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
	public void testBasic() throws SQLException {
		assertHasNext(
				"There should be three tables!", 
				3, 
				jmdcfg.getMetadata().getEntityBindings().iterator());
		Table table = getTable(jmdcfg, JdbcUtil.toIdentifier(this, "basic"));
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "basic"), 
				JdbcUtil.toIdentifier(this, table.getName()));
		Assert.assertEquals(2, table.getColumnSpan());
		Column basicColumn = table.getColumn(0);
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "a"), 
				JdbcUtil.toIdentifier(this, basicColumn.getName()));
		PrimaryKey key = table.getPrimaryKey();
		Assert.assertNotNull("There should be a primary key!", key);
		Assert.assertEquals(key.getColumnSpan(), 1);
		Column column = key.getColumn(0);
		Assert.assertTrue(column.isUnique());
		Assert.assertSame(basicColumn, column);
	}

	@Test
	public void testScalePrecisionLength() {
		Table table = getTable(jmdcfg, JdbcUtil.toIdentifier(this, "basic"));
		Column nameCol = table.getColumn(new Column(JdbcUtil.toIdentifier(this, "name")));
		Assert.assertEquals(nameCol.getLength(), 20);
		Assert.assertEquals(nameCol.getPrecision(), Column.DEFAULT_PRECISION);
		Assert.assertEquals(nameCol.getScale(), Column.DEFAULT_SCALE);
	}

	@Test
	public void testCompositeKeys() {
		Table table = getTable(jmdcfg, JdbcUtil.toIdentifier(this, "multikeyed"));
		PrimaryKey primaryKey = table.getPrimaryKey();
		Assert.assertEquals(2, primaryKey.getColumnSpan());
	}

	private Table getTable(JDBCMetaDataConfiguration configuration, String tabName) {
		Iterator<Table> iter = configuration.getMetadata().collectTableMappings().iterator();
		while (iter.hasNext()) {
			Table table = (Table) iter.next();
			if (table.getName().equals(tabName)) {
				return table;
			}
		}
		return null;
	}

	protected void assertHasNext(String reason, int expected, Iterator<?> iterator) {
		int actual = 0;
		Object last = null;
		while (iterator.hasNext() && actual <= expected) {
			last = iterator.next();
			actual++;
		}
		if (actual < expected) {
			throw new ComparisonFailure(reason == null ? "Expected were less" : reason, "" + expected, "" + actual);
		}
		if (actual > expected) {
			throw new ComparisonFailure((reason == null ? "Expected were higher" : reason) + ", Last: " + last,
					"" + expected, "" + actual);
		}
	}

}
