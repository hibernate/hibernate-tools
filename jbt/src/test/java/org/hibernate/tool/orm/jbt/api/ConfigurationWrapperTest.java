package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationWrapperTest {

	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private NativeConfiguration wrappedNativeConfiguration = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	private RevengConfiguration wrappedRevengConfiguration = null;
	private ConfigurationWrapper jpaConfigurationWrapper = null;
	private JpaConfiguration wrappedJpaConfiguration = null;

	@BeforeEach
	public void beforeEach() throws Exception {
		initializeFacadesAndTargets();
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

	private void initializeFacadesAndTargets() {
		wrappedNativeConfiguration = new NativeConfiguration();
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedJpaConfiguration);
		wrappedRevengConfiguration = new RevengConfiguration();
		revengConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedRevengConfiguration);
		wrappedJpaConfiguration = new JpaConfiguration(null, null);
		jpaConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedJpaConfiguration);
	}
	
}
