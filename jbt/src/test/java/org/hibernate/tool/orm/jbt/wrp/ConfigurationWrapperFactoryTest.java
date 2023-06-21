package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.wrp.ConfigurationWrapperFactory.ConfigurationWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationWrapperFactoryTest {
	
	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	private ConfigurationWrapper jpaConfigurationWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		revengConfigurationWrapper = ConfigurationWrapperFactory.createRevengConfigurationWrapper();
		jpaConfigurationWrapper = ConfigurationWrapperFactory.createJpaConfigurationWrapper(null, null);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(nativeConfigurationWrapper);
		assertTrue(nativeConfigurationWrapper.getWrappedObject() instanceof NativeConfiguration);
		assertNotNull(revengConfigurationWrapper);
		assertTrue(revengConfigurationWrapper.getWrappedObject() instanceof RevengConfiguration);
		assertNotNull(jpaConfigurationWrapper);
		assertTrue(jpaConfigurationWrapper.getWrappedObject() instanceof JpaConfiguration);
	}

}
