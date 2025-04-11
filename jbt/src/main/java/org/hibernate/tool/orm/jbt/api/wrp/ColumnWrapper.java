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

public interface ColumnWrapper extends Wrapper {
	
	static final int DEFAULT_LENGTH = 255;
	static final int DEFAULT_PRECISION = 19;
	static final int DEFAULT_SCALE = 2;
	
	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	String getSqlType(ConfigurationWrapper configurationWrapper);
	long getLength();
	int getDefaultLength();
	int getPrecision();
	int getDefaultPrecision();
	int getScale();
	int getDefaultScale();
	boolean isNullable();
	ValueWrapper getValue();
	boolean isUnique();
	void setSqlType(String sqlType);
	
}
