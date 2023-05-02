package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.Environment;

public class EnvironmentWrapper {
	
	public static EnvironmentWrapper INSTANCE = new EnvironmentWrapper();
	
	private EnvironmentWrapper() {}

	public String getTransactionManagerStrategy() { return "hibernate.transaction.coordinator_class"; }

	public Object getDriver() { return Environment.DRIVER; }

}
