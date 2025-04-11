/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
