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

public interface PropertyWrapper extends Wrapper {

	ValueWrapper getValue();
	void setName(String name);
	void setPersistentClass(PersistentClassWrapper pc);
	PersistentClassWrapper getPersistentClass();
	boolean isComposite();
	String getPropertyAccessorName();
	String getName();
	TypeWrapper getType();
	void setValue(ValueWrapper value);
	void setPropertyAccessorName(String s);
	void setCascade(String s);
	boolean isBackRef();
	boolean isSelectable();
	boolean isInsertable();
	boolean isUpdateable();
	String getCascade();
	boolean isLazy();
	boolean isOptional();
	boolean isNaturalIdentifier();
	boolean isOptimisticLocked();

}
