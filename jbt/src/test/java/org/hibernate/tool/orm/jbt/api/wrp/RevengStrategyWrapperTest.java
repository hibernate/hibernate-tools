package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.RevengSettingsWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.RevengStrategyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevengStrategyWrapperTest {

	private RevengStrategy wrappedRevengStrategy = null;
	private RevengStrategyWrapper revengStrategyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		revengStrategyWrapper = RevengStrategyWrapperFactory.createRevengStrategyWrapper();
		wrappedRevengStrategy = (RevengStrategy)revengStrategyWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedRevengStrategy);
		assertNotNull(revengStrategyWrapper);
	}
	
	
	@Test
	public void testSetSettings() throws Exception {
		RevengSettingsWrapper revengSettingsWrapper = RevengSettingsWrapperFactory.createRevengSettingsWrapper(null);
		RevengSettings revengSettings = (RevengSettings)revengSettingsWrapper.getWrappedObject();
		Field field = AbstractStrategy.class.getDeclaredField("settings");
		field.setAccessible(true);
		assertNotSame(field.get(wrappedRevengStrategy), revengSettings);
		revengStrategyWrapper.setSettings(revengSettingsWrapper);
		assertSame(field.get(wrappedRevengStrategy), revengSettings);
	}
	
}
