package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface EnvironmentWrapper extends Wrapper {
	
	default String getTransactionManagerStrategy() { return "hibernate.transaction.coordinator_class"; }

	default String getDriver() { return Environment.DRIVER; }

	default String getHBM2DDLAuto() { return Environment.HBM2DDL_AUTO; }

	default String getDialect() { return Environment.DIALECT; }

	default String getDataSource() { return Environment.DATASOURCE; }

	default String getConnectionProvider() { return Environment.CONNECTION_PROVIDER; }

	default String getURL() { return Environment.URL; }

	default String getUser() { return Environment.USER; }

	default String getPass() { return Environment.PASS; }

	default String getSessionFactoryName() { return Environment.SESSION_FACTORY_NAME; }

	default String getDefaultCatalog() { return Environment.DEFAULT_CATALOG; }

	default String getDefaultSchema() { return Environment.DEFAULT_SCHEMA; }

	default Class<Environment> getWrappedClass() { return Environment.class; }

}
