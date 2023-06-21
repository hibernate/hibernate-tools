package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.wrp.ConfigurationWrapperFactory.ConfigurationWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationWrapperFactoryTest {
	
	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private Configuration wrappedNativeConfiguration = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	private Configuration wrappedRevengConfiguration = null;
	private ConfigurationWrapper jpaConfigurationWrapper = null;
	private Configuration wrappedJpaConfiguration = null;
	
	@BeforeEach
	public void beforeEach() {
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		wrappedNativeConfiguration = nativeConfigurationWrapper.getWrappedObject();
		revengConfigurationWrapper = ConfigurationWrapperFactory.createRevengConfigurationWrapper();
		wrappedRevengConfiguration = revengConfigurationWrapper.getWrappedObject();
		jpaConfigurationWrapper = ConfigurationWrapperFactory.createJpaConfigurationWrapper(null, null);
		wrappedJpaConfiguration = jpaConfigurationWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(nativeConfigurationWrapper);
		assertNotNull(wrappedNativeConfiguration);
		assertNotNull(revengConfigurationWrapper);
		assertNotNull(wrappedRevengConfiguration);
		assertNotNull(jpaConfigurationWrapper);
		assertNotNull(wrappedJpaConfiguration);
	}
	
	@Test
	public void testGetProperty() {
		assertNull(nativeConfigurationWrapper.getProperty("foo"));
		wrappedNativeConfiguration.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationWrapper.getProperty("foo"));
		assertNull(revengConfigurationWrapper.getProperty("foo"));
		wrappedRevengConfiguration.setProperty("foo", "bar");
		assertEquals("bar", revengConfigurationWrapper.getProperty("foo"));
		assertNull(jpaConfigurationWrapper.getProperty("foo"));
		wrappedJpaConfiguration.setProperty("foo", "bar");
		assertEquals("bar", jpaConfigurationWrapper.getProperty("foo"));
	}

}
