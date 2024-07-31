package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.api.wrp.EnvironmentWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class EnvironmentWrapperFactory {
	
	static EnvironmentWrapper ENVIRONMENT_WRAPPER_INSTANCE = new EnvironmentWrapperImpl();

	public static EnvironmentWrapper createEnvironmentWrapper() {
		return ENVIRONMENT_WRAPPER_INSTANCE;
	}
	
	private static class EnvironmentWrapperImpl 
			extends AbstractWrapper
			implements EnvironmentWrapper {
		
		@Override public String getTransactionManagerStrategy() { return "hibernate.transaction.coordinator_class"; }

		@Override public  String getDriver() { return Environment.JAKARTA_JDBC_DRIVER; }

		@Override public  String getHBM2DDLAuto() { return Environment.HBM2DDL_AUTO; }

		@Override public  String getDialect() { return Environment.DIALECT; }

		@Override public  String getDataSource() { return Environment.JAKARTA_JTA_DATASOURCE; }

		@Override public  String getConnectionProvider() { return Environment.CONNECTION_PROVIDER; }

		@Override public  String getURL() { return Environment.JAKARTA_JDBC_URL; }

		@Override public  String getUser() { return Environment.JAKARTA_JDBC_USER; }

		@Override public  String getPass() { return Environment.JAKARTA_JDBC_PASSWORD; }

		@Override public  String getSessionFactoryName() { return Environment.SESSION_FACTORY_NAME; }

		@Override public  String getDefaultCatalog() { return Environment.DEFAULT_CATALOG; }

		@Override public  String getDefaultSchema() { return Environment.DEFAULT_SCHEMA; }

		@Override public  Class<Environment> getWrappedClass() { return Environment.class; }

	}
	
}
