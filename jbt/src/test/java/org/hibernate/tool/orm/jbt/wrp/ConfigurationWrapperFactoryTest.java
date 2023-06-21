package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.wrp.ConfigurationWrapperFactory.ConfigurationWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationWrapperFactoryTest {
	
	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		revengConfigurationWrapper = ConfigurationWrapperFactory.createRevengConfigurationWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(nativeConfigurationWrapper);
		assertNotNull(revengConfigurationWrapper);
	}

}
