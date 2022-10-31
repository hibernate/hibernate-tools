package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WrapperFactoryTest {
	
	private WrapperFactory wrapperFactory = null;
	
	@BeforeEach
	public void beforeEach() {
		wrapperFactory = new WrapperFactory();
	}
	
	@Test
	public void testCreateArtifactCollectorWrapper() {
		Object artifactCollectorWrapper = wrapperFactory.createArtifactCollectorWrapper();
		assertNotNull(artifactCollectorWrapper);
		assertTrue(artifactCollectorWrapper instanceof DefaultArtifactCollector);
	}
	
	@Test
	public void testCreateCfg2HbmWrapper() {
		Object cfg2HbmWrapper = wrapperFactory.createCfg2HbmWrapper();
		assertNotNull(cfg2HbmWrapper);
		assertTrue(cfg2HbmWrapper instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testCreateNamingStrategyWrapper() {
		Object namingStrategyWrapper = wrapperFactory.createNamingStrategyWrapper();
		assertNotNull(namingStrategyWrapper);
		assertTrue(namingStrategyWrapper instanceof DefaultNamingStrategy);
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		Object reverseEngineeringSettingsWrapper = null;
		try {
			reverseEngineeringSettingsWrapper = wrapperFactory.createReverseEngineeringSettingsWrapper(null);
			fail();
		} catch (Throwable t) {
			assertTrue(t instanceof AssertionError);
		}
		try {
			reverseEngineeringSettingsWrapper = wrapperFactory.createReverseEngineeringSettingsWrapper(new Object());
			fail();
		} catch (Throwable t) {
			assertTrue(t instanceof AssertionError);
		}
		RevengStrategy strategy = new DefaultStrategy();
		reverseEngineeringSettingsWrapper = wrapperFactory.createReverseEngineeringSettingsWrapper(strategy);
		assertNotNull(reverseEngineeringSettingsWrapper);
		assertTrue(reverseEngineeringSettingsWrapper instanceof RevengSettings);
		assertSame(strategy, ((RevengSettings)reverseEngineeringSettingsWrapper).getRootStrategy());
	}
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		Object reverseEngineeringStrategyWrapper = wrapperFactory.createReverseEngineeringStrategyWrapper();
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof DefaultStrategy);
	}

}
