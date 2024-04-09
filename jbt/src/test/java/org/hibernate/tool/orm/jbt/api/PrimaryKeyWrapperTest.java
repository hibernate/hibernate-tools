package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.internal.factory.PrimaryKeyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimaryKeyWrapperTest {
	
	private PrimaryKeyWrapper primaryKeyWrapper;
	private PrimaryKey wrappedPrimaryKey;
	
	@BeforeEach
	public void beforeEach() {
		wrappedPrimaryKey = new PrimaryKey(new Table(""));
		primaryKeyWrapper = PrimaryKeyWrapperFactory.createPrimaryKeyWrapper(wrappedPrimaryKey);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedPrimaryKey);
		assertNotNull(primaryKeyWrapper);
	}
	
}
