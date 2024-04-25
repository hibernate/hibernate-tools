package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.util.TypeFactoryWrapperHelper;

public interface TypeFactoryWrapper {

	default TypeWrapper getBooleanType() { return TypeFactoryWrapperHelper.typeRegistry().get("boolean"); }

}
