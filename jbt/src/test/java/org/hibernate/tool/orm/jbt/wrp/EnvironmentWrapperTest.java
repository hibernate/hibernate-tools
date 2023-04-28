package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentWrapperTest {
	
	private EnvironmentWrapper environmentWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		environmentWrapper = new EnvironmentWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(environmentWrapper);
	}

}
