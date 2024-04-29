package org.hibernate.tool.orm.jbt.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeRegistry {

	private static Map<String, TypeWrapper> TYPE_REGISTRY = null;	
	private static final BasicTypeRegistry BASIC_TYPE_REGISTRY = new TypeConfiguration().getBasicTypeRegistry();
	
	static {
		TYPE_REGISTRY = createTypeRegistry();
	}
	
	public static Map<String, TypeWrapper> typeRegistry() {
		return TYPE_REGISTRY;
	}
	
	public static TypeWrapper getType(String name) {
		return TYPE_REGISTRY.get(name);
	}
	
	private static Map<String, TypeWrapper> createTypeRegistry() {
		HashMap<String, TypeWrapper> result = new HashMap<String, TypeWrapper>();
		result.put("boolean", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("boolean")));
		result.put("byte", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("byte")));
		result.put("big_integer", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("big_integer")));
		result.put("short", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("short")));
		result.put("calendar", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("calendar")));
		result.put("calendar_date", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("calendar_date")));
		result.put("integer", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("integer")));
		result.put("big_decimal", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("big_decimal")));
		result.put("character", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("character")));
		result.put("class", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("class")));
		result.put("currency", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("currency")));
		result.put("date", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("date")));
		result.put("double", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("double")));
		result.put("float", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("float")));
		result.put("locale", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("locale")));
		result.put("long", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("long")));
		result.put("string", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("string")));
		result.put("text", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("text")));
		result.put("time", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("time")));
		result.put("timestamp", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("timestamp")));
		result.put("timezone", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("timezone")));
		result.put("true_false", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("true_false")));
		result.put("yes_no", TypeWrapperFactory.createTypeWrapper(BASIC_TYPE_REGISTRY.getRegisteredType("yes_no")));
		return result;
	}

}
