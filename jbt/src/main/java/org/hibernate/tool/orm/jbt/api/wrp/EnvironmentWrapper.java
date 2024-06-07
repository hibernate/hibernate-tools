package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface EnvironmentWrapper extends Wrapper {
	
	String getTransactionManagerStrategy();

	String getDriver();

	String getHBM2DDLAuto();

	String getDialect();

	String getDataSource();

	String getConnectionProvider();

	String getURL();

	String getUser();

	String getPass();

	String getSessionFactoryName();

	String getDefaultCatalog();

	String getDefaultSchema();

	Class<?> getWrappedClass();

}
