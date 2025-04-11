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

import java.util.Map;

public interface TypeFactoryWrapper extends Wrapper {

	TypeWrapper getBooleanType();
	TypeWrapper getByteType();
	TypeWrapper getBigIntegerType();
	TypeWrapper getShortType() ;
	TypeWrapper getCalendarType();
	TypeWrapper getCalendarDateType();
	TypeWrapper getIntegerType();
	TypeWrapper getBigDecimalType();
	TypeWrapper getCharacterType();
	TypeWrapper getClassType();
	TypeWrapper getCurrencyType();
	TypeWrapper getDateType();
	TypeWrapper getDoubleType();
	TypeWrapper getFloatType();
	TypeWrapper getLocaleType();
	TypeWrapper getLongType();
	TypeWrapper getStringType();
	TypeWrapper getTextType();
	TypeWrapper getTimeType();
	TypeWrapper getTimestampType();
	TypeWrapper getTimezoneType();
	TypeWrapper getTrueFalseType();
	TypeWrapper getYesNoType();
	TypeWrapper getNamedType(String name);
	TypeWrapper getBasicType(String name);
	Map<TypeWrapper, String> getTypeFormats();

}
