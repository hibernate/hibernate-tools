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
package org.hibernate.tool.orm.jbt.api.wrp;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface ConfigurationWrapper extends Wrapper {

	String getProperty(String property);
	ConfigurationWrapper addFile(File file);
	void setProperty(String name, String value);
	ConfigurationWrapper setProperties(Properties properties);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(NamingStrategyWrapper namingStrategy);
	Properties getProperties();
	void addProperties(Properties properties);
	ConfigurationWrapper configure(Document document);
	ConfigurationWrapper configure(File file);
	ConfigurationWrapper configure();
	void addClass(Class<?> clazz);
	void buildMappings();
	SessionFactoryWrapper buildSessionFactory();
	Iterator<PersistentClassWrapper> getClassMappings();
	void setPreferBasicCompositeIds(boolean b);
	void setReverseEngineeringStrategy(RevengStrategyWrapper strategy);
	void readFromJDBC();
	PersistentClassWrapper getClassMapping(String string);
	NamingStrategyWrapper getNamingStrategy();
	EntityResolver getEntityResolver();
	Iterator<TableWrapper> getTableMappings();
	
}
