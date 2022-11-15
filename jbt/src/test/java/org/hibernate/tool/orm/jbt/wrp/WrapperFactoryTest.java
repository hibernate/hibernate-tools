package org.hibernate.tool.orm.jbt.wrp;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
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
		Object namingStrategyWrapper = wrapperFactory.createNamingStrategyWrapper(DefaultNamingStrategy.class.getName());
		assertNotNull(namingStrategyWrapper);
		assertTrue(namingStrategyWrapper instanceof NamingStrategyWrapper.StrategyClassNameProvider);
		assertEquals(
				((NamingStrategyWrapper.StrategyClassNameProvider)namingStrategyWrapper).getStrategyClassName(),
				DefaultNamingStrategy.class.getName());
		assertTrue(namingStrategyWrapper instanceof NamingStrategy);
		namingStrategyWrapper = null;
		assertNull(namingStrategyWrapper);
		try {
			namingStrategyWrapper = wrapperFactory.createNamingStrategyWrapper("foo");
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Exception while looking up class 'foo'");
		}
		assertNull(namingStrategyWrapper);
	}
	
	@Test
	public void testCreateOverrideRepositoryWrapper() {
		Object overrideRepositoryWrapper = wrapperFactory.createOverrideRepositoryWrapper();
		assertNotNull(overrideRepositoryWrapper);
		assertTrue(overrideRepositoryWrapper instanceof OverrideRepository);
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
	public void testCreateReverseEngineeringStrategy() throws Exception {
		Object reverseEngineeringStrategyWrapper = wrapperFactory.createReverseEngineeringStrategyWrapper(null);
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof DefaultStrategy);
		reverseEngineeringStrategyWrapper = 
				wrapperFactory.createReverseEngineeringStrategyWrapper(TestRevengStrategy.class.getName());
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof DelegatingStrategy);
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		assertTrue(delegateField.get(reverseEngineeringStrategyWrapper) instanceof TestRevengStrategy);
	}
	
	@SuppressWarnings("serial")
	public static class TestNamingStrategy extends DefaultNamingStrategy {}
	
	public static class TestRevengStrategy extends DefaultStrategy {}

}
