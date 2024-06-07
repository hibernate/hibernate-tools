package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Map;

import org.hibernate.tool.orm.jbt.api.wrp.TypeFactoryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.util.TypeRegistry;

public class TypeFactoryWrapperFactory {
	
	private static TypeFactoryWrapper INSTANCE = new TypeFactoryWrapperImpl();

	public static TypeFactoryWrapper createTypeFactoryWrapper() {
		return INSTANCE;
	}
	
	private static class TypeFactoryWrapperImpl implements TypeFactoryWrapper {
	
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
		@Override public TypeWrapper getYesNoType() { return TypeRegistry.getType("true_false"); }
		@Override public TypeWrapper getNamedType(String name) { return TypeRegistry.getType(name); }
		@Override public TypeWrapper getBasicType(String name) { return getNamedType(name); }
		@Override public Map<TypeWrapper, String> getTypeFormats() { return TypeRegistry.getTypeFormats(); }

	}

}
