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
import java.util.Properties;

public interface ValueWrapper extends Wrapper {

	boolean isSimpleValue();
	boolean isCollection();
	ValueWrapper getCollectionElement();
	boolean isOneToMany();
	boolean isManyToOne();
	boolean isOneToOne();
	boolean isMap();
	boolean isComponent();
	boolean isEmbedded();
	boolean isToOne();
	TableWrapper getTable();
	TypeWrapper getType();
	void setElement(ValueWrapper v);
	void setCollectionTable(TableWrapper table);
	void setTable(TableWrapper table);
	boolean isList();
	void setIndex(ValueWrapper v);
	void setTypeName(String s);
	String getComponentClassName();
	Iterator<ColumnWrapper> getColumnIterator();
	boolean isTypeSpecified();
	TableWrapper getCollectionTable();
	ValueWrapper getKey();
	ValueWrapper getIndex();
	String getElementClassName();
	String getTypeName();
	boolean isDependantValue();
	boolean isAny();
	boolean isSet();
	boolean isPrimitiveArray();
	boolean isArray();
	boolean isIdentifierBag();
	boolean isBag();
	String getReferencedEntityName();
	String getEntityName();
	Iterator<PropertyWrapper> getPropertyIterator();
	void addColumn(ColumnWrapper column);
	void setTypeParameters(Properties properties);
	String getForeignKeyName();
	PersistentClassWrapper getOwner();
	ValueWrapper getElement();
	String getParentProperty();
	void setElementClassName(String name);
	void setKey(ValueWrapper value);
	void setFetchModeJoin();
	boolean isInverse();
	PersistentClassWrapper getAssociatedClass() ;
	void setLazy(boolean b);
	void setRole(String role);
	void setReferencedEntityName(String name);
	void setAssociatedClass(PersistentClassWrapper pc);

}
