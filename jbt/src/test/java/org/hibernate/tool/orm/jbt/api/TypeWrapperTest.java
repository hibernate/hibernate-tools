package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeWrapperTest {

	private TypeConfiguration typeConfiguration = null;
	
	@BeforeEach
	public void beforeEach() {
		typeConfiguration = new TypeConfiguration();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(typeConfiguration);
		assertNotNull(TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(int.class)));
	}

}
