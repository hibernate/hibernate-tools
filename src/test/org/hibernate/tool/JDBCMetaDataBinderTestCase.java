/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author max
 *
 */
public abstract class JDBCMetaDataBinderTestCase extends BaseTestCase {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public JDBCMetaDataBinderTestCase() {
		super(null);
	}

	public JDBCMetaDataBinderTestCase(String output) {
		super(null, output);
	}

	/* static */ protected JDBCMetaDataConfiguration cfg;

	private static boolean storesLowerCaseIdentifiers;

	private static boolean storesUpperCaseIdentifiers;

	/** should this maybe be on dialect ? **/
	protected String identifier(String actual) {
		if (storesLowerCaseIdentifiers) {
			return actual.toLowerCase();
		} else if (storesUpperCaseIdentifiers) {
			return actual.toUpperCase();
		} else {
			return actual;
		}
	}

	/**
	 * Tries to adjust for different behaviors on databases regarding cases on
	 * identifiers. Used if you don't care about cases in comparisons.
	 * 
	 * @param expected
	 * @param actual
	 */
	protected void assertEqualIdentifiers(String expected, String actual) {
		assertEquals(identifier(expected), identifier(actual));
	}

	/**
	 * @param sqls
	 * @throws SQLException
	 */
	protected void executeDDL(String[] sqls, boolean ignoreErrors) throws SQLException {
		Configuration configuration = new Configuration();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = builder.build();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);

		if (!appliesTo(jdbcServices.getDialect())) {
			fail("test case does not apply to " + jdbcServices.getDialect());
			return; // don't do anything to avoid crippled db
		}

		ConnectionProvider connectionProvider = 
				serviceRegistry.getService(ConnectionProvider.class);
		Statement statement = null;
		Connection con = null;
		try {

			con = connectionProvider.getConnection();

			DatabaseMetaData metaData = con.getMetaData();
			storesLowerCaseIdentifiers = metaData.storesLowerCaseIdentifiers();
			storesUpperCaseIdentifiers = metaData.storesUpperCaseIdentifiers();

			statement = con.createStatement();

			for (int i = 0; i < sqls.length; i++) {
				String ddlsql = sqls[i];
				log.info("Execute: " + ddlsql);

				try {
					statement.execute(ddlsql);
				} catch (SQLException se) {
					if (ignoreErrors) {
						log.info(se.toString() + " for " + ddlsql);
					} else {
						log.error(ddlsql, se);
						throw se;
					}
				}
			}
			con.commit();
		} finally {
			if (statement != null)
				statement.close();
			connectionProvider.closeConnection(con);

		}
	}

	protected abstract String[] getCreateSQL();

	protected abstract String[] getDropSQL();

	protected void setUp() throws Exception {
		super.setUp();
		if (cfg == null) { // only do if we haven't done it before - to save
							// time!

			try {
				executeDDL(getDropSQL(), true);
			} catch (SQLException se) {
				System.err.println("Error while dropping - normally ok.");
				se.printStackTrace();
			}

			cfg = new JDBCMetaDataConfiguration();
			configure(cfg);

			String[] sqls = getCreateSQL();

			executeDDL(sqls, false);

			cfg.readFromJDBC();
		}
	}

	protected void tearDown() throws Exception {
		executeDDL(getDropSQL(), true);

		super.tearDown();
	}

	/**
	 * @param cfg2
	 */
	protected void configure(JDBCMetaDataConfiguration configuration) {

	}

	/**
	 * @param column
	 * @return
	 */
	protected String toPropertyName(String column) {
		return cfg.getReverseEngineeringStrategy().columnToPropertyName(null, column);
	}

	/**
	 * @param table
	 * @return
	 */
	protected String toClassName(String table) {
		return cfg.getReverseEngineeringStrategy().tableToClassName(new TableIdentifier(null, null, table));
	}

	/**
	 * Return the first foreignkey with the matching name ... there actually
	 * might be multiple foreignkeys with same name, but then they point to
	 * different entitities.
	 * 
	 * @param table
	 * @param fkName
	 * @return
	 */
	protected ForeignKey getForeignKey(Table table, String fkName) {
		Iterator<?> iter = table.getForeignKeyIterator();
		while (iter.hasNext()) {
			ForeignKey fk = (ForeignKey) iter.next();
			if (fk.getName().equals(fkName)) {
				return fk;
			}
		}
		return null;
	}

	/**
	 * Find the first table matching the name (without looking at
	 * schema/catalog)
	 * 
	 * @param tabName
	 * @return
	 */
	protected Table getTable(String tabName) {
		return getTable(cfg, tabName);
	}

	protected Table getTable(JDBCMetaDataConfiguration configuration, String tabName) {
		Iterator<Table> iter = configuration.getMetadata().collectTableMappings().iterator();
		while (iter.hasNext()) {
			Table table = (Table) iter.next();
			if (table.getName().equals(tabName)) {
				return table;
			}
		}
		return null;
	}

	public JDBCMetaDataConfiguration getConfiguration() {
		return cfg;
	}

}
