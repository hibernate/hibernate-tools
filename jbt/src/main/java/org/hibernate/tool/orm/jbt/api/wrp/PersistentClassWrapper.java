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

import java.util.Iterator;
import java.util.List;

public interface PersistentClassWrapper extends Wrapper {

	boolean isAssignableToRootClass();
	boolean isRootClass();
	boolean isInstanceOfRootClass();
	boolean isInstanceOfSubclass();
	boolean isInstanceOfJoinedSubclass();
	PropertyWrapper getProperty();
	void setTable(TableWrapper table);
	void setIdentifier(ValueWrapper value);
	void setKey(ValueWrapper value);
	boolean isInstanceOfSpecialRootClass();
	PropertyWrapper getParentProperty();
	void setIdentifierProperty(PropertyWrapper property);
	void setDiscriminator(ValueWrapper value);
	boolean isLazyPropertiesCacheable();
	Iterator<PropertyWrapper> getPropertyIterator();
	Iterator<JoinWrapper> getJoinIterator();
	Iterator<PersistentClassWrapper> getSubclassIterator();
	Iterator<PropertyWrapper> getPropertyClosureIterator();
	String getEntityName();
	String getClassName();
	PropertyWrapper getIdentifierProperty();
	boolean hasIdentifierProperty();
	PersistentClassWrapper getRootClass();
	PersistentClassWrapper getSuperclass();
	PropertyWrapper getProperty(String name);
	TableWrapper getTable();
	Boolean isAbstract();
	ValueWrapper getDiscriminator();
	ValueWrapper getIdentifier();
	PropertyWrapper getVersion();
	void setClassName(String name);
	void setEntityName(String name);
	void setDiscriminatorValue(String str);
	void setAbstract(Boolean b);
	void addProperty(PropertyWrapper p);
	void setProxyInterfaceName(String name);
	void setLazy(boolean b);
	boolean isCustomDeleteCallable();
	boolean isCustomInsertCallable();
	boolean isCustomUpdateCallable();
	boolean isDiscriminatorInsertable();
	boolean isDiscriminatorValueNotNull();
	boolean isDiscriminatorValueNull();
	boolean isExplicitPolymorphism();
	boolean isForceDiscriminator();
	boolean isInherited();
	boolean isJoinedSubclass();
	boolean isLazy();
	boolean isMutable();
	boolean isPolymorphic();
	boolean isVersioned();
	int getBatchSize();
	String getCacheConcurrencyStrategy();
	String getCustomSQLDelete();
	String getCustomSQLInsert();
	String getCustomSQLUpdate();
	String getDiscriminatorValue();
	String getLoaderName();
	int getOptimisticLockMode();
	String getWhere();
	TableWrapper getRootTable();
	List<PropertyWrapper> getProperties();
	List<JoinWrapper> getJoins();
	List<PersistentClassWrapper> getSubclasses();
	List<PropertyWrapper> getPropertyClosure();
	
}
