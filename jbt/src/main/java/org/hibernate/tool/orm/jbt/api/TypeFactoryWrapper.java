package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.util.TypeFactoryWrapperHelper;

public interface TypeFactoryWrapper {

	default TypeWrapper getBooleanType() { return TypeFactoryWrapperHelper.typeRegistry().get("boolean"); }
	default TypeWrapper getByteType() { return TypeFactoryWrapperHelper.typeRegistry().get("byte"); }
	default TypeWrapper getBigIntegerType() { return TypeFactoryWrapperHelper.typeRegistry().get("big_integer"); }
	default TypeWrapper getShortType() { return TypeFactoryWrapperHelper.typeRegistry().get("short"); }

}
