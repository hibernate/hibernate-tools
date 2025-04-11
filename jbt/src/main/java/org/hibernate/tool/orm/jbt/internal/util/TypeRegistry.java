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
package org.hibernate.tool.orm.jbt.internal.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeRegistry {

	static Map<String, TypeWrapper> TYPE_REGISTRY = new HashMap<String, TypeWrapper>();	
	static Map<TypeWrapper, String> TYPE_FORMATS = null;
	private static final BasicTypeRegistry BASIC_TYPE_REGISTRY = new TypeConfiguration().getBasicTypeRegistry();
	
	public static TypeWrapper getType(String name) {
		if (!TYPE_REGISTRY.containsKey(name)) {
			Type basicType = BASIC_TYPE_REGISTRY.getRegisteredType(name);
			if (basicType != null) {
				TYPE_REGISTRY.put(name, TypeWrapperFactory.createTypeWrapper(basicType));
			} else {
				TYPE_REGISTRY.put(name, null);
			}
		}
		return TYPE_REGISTRY.get(name);
	}
	
	public static Map<TypeWrapper, String> getTypeFormats() {
		if (TYPE_FORMATS == null) {
			initializeTypeFormats();
		}
		return TYPE_FORMATS;
	}

	private static void initializeTypeFormats() {
		TYPE_FORMATS = new HashMap<TypeWrapper, String>();
		addTypeFormat(getType("boolean"), Boolean.TRUE);
		addTypeFormat(getType("byte"), Byte.valueOf((byte) 42));
		addTypeFormat(getType("big_integer"), BigInteger.valueOf(42));
		addTypeFormat(getType("short"), Short.valueOf((short) 42));
		addTypeFormat(getType("calendar"), new GregorianCalendar());
		addTypeFormat(getType("calendar_date"), new GregorianCalendar());
		addTypeFormat(getType("integer"), Integer.valueOf(42));
		addTypeFormat(getType("big_decimal"), new BigDecimal(42.0));
		addTypeFormat(getType("character"), Character.valueOf('h'));
		addTypeFormat(getType("class"), Class.class);
		addTypeFormat(getType("currency"), Currency.getInstance(Locale.getDefault()));
		addTypeFormat(getType("date"), new Date());
		addTypeFormat(getType("double"), Double.valueOf(42.42));
		addTypeFormat(getType("float"), Float.valueOf((float)42.42));
		addTypeFormat(getType("locale"), Locale.getDefault());
		addTypeFormat(getType("long"), Long.valueOf(42));
		addTypeFormat(getType("string"), "a string"); //$NON-NLS-1$
		addTypeFormat(getType("text"), "a text"); //$NON-NLS-1$
		addTypeFormat(getType("time"), new Date());
		addTypeFormat(getType("timestamp"), new Date());
		addTypeFormat(getType("timezone"), TimeZone.getDefault());
		addTypeFormat(getType("true_false"), Boolean.TRUE);
		addTypeFormat(getType("yes_no"), Boolean.TRUE);
	}
	
	private static void addTypeFormat(TypeWrapper type, Object value) {
		TYPE_FORMATS.put(type, type.toString(value));
	}
	
}
