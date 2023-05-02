package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.Environment;

public class EnvironmentWrapper {
	
	public static EnvironmentWrapper INSTANCE = new EnvironmentWrapper();
	
	private EnvironmentWrapper() {}

	public String getTransactionManagerStrategy() { return "hibernate.transaction.coordinator_class"; }

	public String getDriver() { return Environment.DRIVER; }

	public String getHBM2DDLAuto() { return Environment.HBM2DDL_AUTO; }

	public String getDialect() { return Environment.DIALECT; }

	public String getDataSource() { return Environment.DATASOURCE; }

	public String getConnectionProvider() { return Environment.CONNECTION_PROVIDER; }

	public String getURL() { return Environment.URL; }

	public String getUser() { return Environment.USER; }

	public String getPass() { return Environment.PASS; }

	public String getSessionFactoryName() { return Environment.SESSION_FACTORY_NAME; }

}
