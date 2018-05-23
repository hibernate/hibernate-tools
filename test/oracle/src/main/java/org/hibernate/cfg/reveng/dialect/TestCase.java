package org.hibernate.cfg.reveng.dialect;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.dialect.MetaDataDialect;
import org.hibernate.tool.api.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.tool.internal.dialect.OracleMetaDataDialect;
import org.hibernate.tool.internal.metadata.DefaultDatabaseCollector;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestCase {

    private Properties properties = null;
    private ServiceRegistry serviceRegistry;

    @Before
    public void setUp() {
        JdbcUtil.createDatabase(this);
        properties = Environment.getProperties();
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        serviceRegistry = ssrb.build();
    }

    @After
    public void tearDown() {
        JdbcUtil.dropDatabase(this);
    }

    @Test
    public void testColumnTypeSizes() {
        MetaDataDialect dialect = configureOracleMetaDataDialect();

        assertSqlTypeLengths(dialect, "a_varchar2_char", "VARCHAR2", 10, 0);
        assertSqlTypeLengths(dialect, "a_varchar2_byte", "VARCHAR2", 10, 0);
        assertSqlTypeLengths(dialect, "a_varchar_char", "VARCHAR2", 10, 0);
        assertSqlTypeLengths(dialect, "a_varchar_byte", "VARCHAR2", 10, 0);
        assertSqlTypeLengths(dialect, "a_nvarchar", "NVARCHAR2", 10, 0);
        assertSqlTypeLengths(dialect, "a_char_char", "CHAR", 10, 0);
        assertSqlTypeLengths(dialect, "a_char_byte", "CHAR", 10, 0);
        assertSqlTypeLengths(dialect, "a_nchar_char", "NCHAR", 10, 0);
        assertSqlTypeLengths(dialect, "a_nchar_byte", "NCHAR", 10, 0);
        assertSqlTypeLengths(dialect, "a_number_int", "NUMBER", 10, 0);
        assertSqlTypeLengths(dialect, "a_number_dec", "NUMBER", 10, 2);
        assertSqlTypeLengths(dialect, "a_float", "FLOAT", 10, 0);
    }

    private MetaDataDialect configureOracleMetaDataDialect() {
        MetaDataDialect dialect = new OracleMetaDataDialect();
        JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        ConnectionProvider connectionProvider = serviceRegistry.getService(ConnectionProvider.class);
        dialect.configure(
                ReverseEngineeringRuntimeInfo.createInstance(
                        connectionProvider,
                        jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(),
                        new DefaultDatabaseCollector(dialect)));
        return dialect;
    }

    private void assertSqlTypeLengths(MetaDataDialect dialect, String columnName, String typeName, int columnSize, int decimalDigits) {
        columnName = columnName.toUpperCase();
        String catalog = properties.getProperty(AvailableSettings.DEFAULT_CATALOG);
        String schema = properties.getProperty(AvailableSettings.DEFAULT_SCHEMA);
        Iterator<Map<String, Object>> columns = dialect.getColumns(catalog, schema, "PERSON", columnName);
        boolean found = false;
        while (columns.hasNext()) {
            Map<String, Object> column = columns.next();
            assertThat(column.get("COLUMN_NAME"), equalTo(columnName.toUpperCase()));
            assertThat(column.get("TYPE_NAME"), equalTo(typeName));
            assertThat(column.get("COLUMN_SIZE"), equalTo(columnSize));
            assertThat(column.get("DECIMAL_DIGITS"), equalTo(decimalDigits));
            found = true;
        }
        assertThat("Expected column '" + columnName + "'to exist.", found, equalTo(true));
    }
}
