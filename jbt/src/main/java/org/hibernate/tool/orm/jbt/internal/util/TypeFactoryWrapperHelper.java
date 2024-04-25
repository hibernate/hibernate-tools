package org.hibernate.tool.orm.jbt.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeFactoryWrapperHelper {

	private static Map<String, TypeWrapper> TYPE_REGISTRY = null;	
	private static final BasicTypeRegistry BASIC_TYPE_REGISTRY = new TypeConfiguration().getBasicTypeRegistry();
	
	public static Map<String, TypeWrapper> typeRegistry() {
		if (TYPE_REGISTRY == null) {
			createTypeRegistry();
		}
		return TYPE_REGISTRY;
	}
	
	private static void createTypeRegistry() {
		TYPE_REGISTRY = new HashMap<String, TypeWrapper>();
		TYPE_REGISTRY.put("boolean", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("boolean")));
		TYPE_REGISTRY.put("byte", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("byte")));
		TYPE_REGISTRY.put("big_integer", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("big_integer")));
		TYPE_REGISTRY.put("short", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("short")));
		TYPE_REGISTRY.put("calendar", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("calendar")));
		TYPE_REGISTRY.put("calendar_date", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("calendar_date")));
		TYPE_REGISTRY.put("integer", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("integer")));
		TYPE_REGISTRY.put("big_decimal", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("big_decimal")));
		TYPE_REGISTRY.put("character", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("character")));
		TYPE_REGISTRY.put("class", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("class")));
		TYPE_REGISTRY.put("currency", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("currency")));
		TYPE_REGISTRY.put("date", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("date")));
		TYPE_REGISTRY.put("double", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("double")));
		TYPE_REGISTRY.put("float", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("float")));
		TYPE_REGISTRY.put("locale", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("locale")));
		TYPE_REGISTRY.put("long", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("long")));
		TYPE_REGISTRY.put("string", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("string")));
		TYPE_REGISTRY.put("text", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("text")));
		TYPE_REGISTRY.put("time", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("time")));
		TYPE_REGISTRY.put("timestamp", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("timestamp")));
		TYPE_REGISTRY.put("timezone", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("timezone")));
		TYPE_REGISTRY.put("true_false", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("true_false")));
		TYPE_REGISTRY.put("yes_no", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("yes_no")));
	}

}
