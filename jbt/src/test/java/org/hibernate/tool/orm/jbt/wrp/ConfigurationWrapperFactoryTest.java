package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.wrp.ConfigurationWrapperFactory.ConfigurationWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationWrapperFactoryTest {
	
	private ConfigurationWrapper nativeConfigurationWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(nativeConfigurationWrapper);
	}

}
