package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.orm.jbt.internal.factory.ForeignKeyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForeignKeyWrapperTest {

	private ForeignKeyWrapper foreignKeyWrapper = null; 
	private ForeignKey wrappedForeignKey = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedForeignKey = new ForeignKey();
		foreignKeyWrapper = ForeignKeyWrapperFactory.createForeignKeyWrapper(wrappedForeignKey);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedForeignKey);
		assertNotNull(foreignKeyWrapper);
	}
	
}
