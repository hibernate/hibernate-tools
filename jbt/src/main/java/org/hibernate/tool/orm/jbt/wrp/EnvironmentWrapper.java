package org.hibernate.tool.orm.jbt.wrp;

public class EnvironmentWrapper {
	
	public static EnvironmentWrapper INSTANCE = new EnvironmentWrapper();
	
	private EnvironmentWrapper() {}

	public String getTransactionManagerStrategy() { return "hibernate.transaction.coordinator_class"; }

}
