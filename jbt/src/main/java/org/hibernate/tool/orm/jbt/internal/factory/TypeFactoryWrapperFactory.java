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

import java.util.Map;

import org.hibernate.tool.orm.jbt.api.wrp.TypeFactoryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.util.TypeRegistry;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class TypeFactoryWrapperFactory {
	
	private static TypeFactoryWrapper INSTANCE = new TypeFactoryWrapperImpl();

	public static TypeFactoryWrapper createTypeFactoryWrapper() {
		return INSTANCE;
	}
	
	private static class TypeFactoryWrapperImpl 
			extends AbstractWrapper
			implements TypeFactoryWrapper {
	
		@Override public TypeWrapper getBooleanType() { return TypeRegistry.getType("boolean"); }
		@Override public TypeWrapper getByteType() { return TypeRegistry.getType("byte"); }
		@Override public TypeWrapper getBigIntegerType() { return TypeRegistry.getType("big_integer"); }
		@Override public TypeWrapper getShortType() { return TypeRegistry.getType("short"); }
		@Override public TypeWrapper getCalendarType() { return TypeRegistry.getType("calendar"); }
		@Override public TypeWrapper getCalendarDateType() { return TypeRegistry.getType("calendar_date"); }
		@Override public TypeWrapper getIntegerType() { return TypeRegistry.getType("integer"); }
		@Override public TypeWrapper getBigDecimalType() { return TypeRegistry.getType("big_decimal"); }
		@Override public TypeWrapper getCharacterType() { return TypeRegistry.getType("character"); }
		@Override public TypeWrapper getClassType() { return TypeRegistry.getType("class"); }
		@Override public TypeWrapper getCurrencyType() { return TypeRegistry.getType("currency"); }
		@Override public TypeWrapper getDateType() { return TypeRegistry.getType("date"); }
		@Override public TypeWrapper getDoubleType() { return TypeRegistry.getType("double"); }
		@Override public TypeWrapper getFloatType() { return TypeRegistry.getType("float"); }
		@Override public TypeWrapper getLocaleType() { return TypeRegistry.getType("locale"); }
		@Override public TypeWrapper getLongType() { return TypeRegistry.getType("long"); }
		@Override public TypeWrapper getStringType() { return TypeRegistry.getType("string"); }
		@Override public TypeWrapper getTextType() { return TypeRegistry.getType("text"); }
		@Override public TypeWrapper getTimeType() { return TypeRegistry.getType("time"); }
		@Override public TypeWrapper getTimestampType() { return TypeRegistry.getType("timestamp"); }
		@Override public TypeWrapper getTimezoneType() { return TypeRegistry.getType("timezone"); }
		@Override public TypeWrapper getTrueFalseType() { return TypeRegistry.getType("true_false"); }
		@Override public TypeWrapper getYesNoType() { return TypeRegistry.getType("yes_no"); }
		@Override public TypeWrapper getNamedType(String name) { return TypeRegistry.getType(name); }
		@Override public TypeWrapper getBasicType(String name) { return getNamedType(name); }
		@Override public Map<TypeWrapper, String> getTypeFormats() { return TypeRegistry.getTypeFormats(); }

	}

}
