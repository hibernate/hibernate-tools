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

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeRegistry {

	static Map<String, TypeWrapper> TYPE_REGISTRY = null;	
	static Map<TypeWrapper, String> TYPE_FORMATS = null;
	private static final BasicTypeRegistry BASIC_TYPE_REGISTRY = new TypeConfiguration().getBasicTypeRegistry();
	
	static {
		initializeTypeRegistry();
		initializeTypeFormats();
	}
	
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
		return TYPE_FORMATS;
	}

	private static void initializeTypeFormats() {
		TYPE_FORMATS = new HashMap<TypeWrapper, String>();
		addTypeFormat(TYPE_REGISTRY.get("boolean"), Boolean.TRUE);
		addTypeFormat(TYPE_REGISTRY.get("byte"), Byte.valueOf((byte) 42));
		addTypeFormat(TYPE_REGISTRY.get("big_integer"), BigInteger.valueOf(42));
		addTypeFormat(TYPE_REGISTRY.get("short"), Short.valueOf((short) 42));
		addTypeFormat(TYPE_REGISTRY.get("calendar"), new GregorianCalendar());
		addTypeFormat(TYPE_REGISTRY.get("calendar_date"), new GregorianCalendar());
		addTypeFormat(TYPE_REGISTRY.get("integer"), Integer.valueOf(42));
		addTypeFormat(TYPE_REGISTRY.get("big_decimal"), new BigDecimal(42.0));
		addTypeFormat(TYPE_REGISTRY.get("character"), Character.valueOf('h'));
		addTypeFormat(TYPE_REGISTRY.get("class"), Class.class);
		addTypeFormat(TYPE_REGISTRY.get("currency"), Currency.getInstance(Locale.getDefault()));
		addTypeFormat(TYPE_REGISTRY.get("date"), new Date());
		addTypeFormat(TYPE_REGISTRY.get("double"), Double.valueOf(42.42));
		addTypeFormat(TYPE_REGISTRY.get("float"), Float.valueOf((float)42.42));
		addTypeFormat(TYPE_REGISTRY.get("locale"), Locale.getDefault());
		addTypeFormat(TYPE_REGISTRY.get("long"), Long.valueOf(42));
		addTypeFormat(TYPE_REGISTRY.get("string"), "a string"); //$NON-NLS-1$
		addTypeFormat(TYPE_REGISTRY.get("text"), "a text"); //$NON-NLS-1$
		addTypeFormat(TYPE_REGISTRY.get("time"), new Date());
		addTypeFormat(TYPE_REGISTRY.get("timestamp"), new Date());
		addTypeFormat(TYPE_REGISTRY.get("timezone"), TimeZone.getDefault());
		addTypeFormat(TYPE_REGISTRY.get("true_false"), Boolean.TRUE);
		addTypeFormat(TYPE_REGISTRY.get("yes_no"), Boolean.TRUE);
	}
	
	private static void addTypeFormat(TypeWrapper type, Object value) {
		TYPE_FORMATS.put(type, type.toString(value));
	}
	
	private static void initializeTypeRegistry() {
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
