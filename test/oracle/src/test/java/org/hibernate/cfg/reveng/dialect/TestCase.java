package org.hibernate.cfg.reveng.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCase {

    private Properties properties = null;
    private ServiceRegistry serviceRegistry;

    @BeforeEach
    public void setUp() {
        JdbcUtil.createDatabase(this);
        properties = Environment.getProperties();
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        serviceRegistry = ssrb.build();
    }

    @AfterEach
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
            assertEquals(column.get("COLUMN_NAME"),columnName.toUpperCase());
            assertEquals(column.get("TYPE_NAME"), typeName);
            assertEquals(column.get("COLUMN_SIZE"), columnSize);
            assertEquals(column.get("DECIMAL_DIGITS"), decimalDigits);
            found = true;
        }
        assertTrue(found, "Expected column '" + columnName + "'to exist.");
    }
}
