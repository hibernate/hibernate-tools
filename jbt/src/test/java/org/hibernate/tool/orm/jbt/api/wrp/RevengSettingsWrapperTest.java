package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.internal.factory.RevengSettingsWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevengSettingsWrapperTest {

	private RevengSettings wrappedRevengSettings = null;
	private RevengSettingsWrapper revengSettingsWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		revengSettingsWrapper = RevengSettingsWrapperFactory.createRevengSettingsWrapper(null);
		wrappedRevengSettings = (RevengSettings)revengSettingsWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedRevengSettings);
		assertNotNull(revengSettingsWrapper);
	}
	
	
	@Test
	public void testSetDefaultPackageName() {
		assertEquals("", wrappedRevengSettings.getDefaultPackageName());
		assertSame(revengSettingsWrapper, revengSettingsWrapper.setDefaultPackageName("foo"));
		assertEquals("foo", wrappedRevengSettings.getDefaultPackageName());
	}
	
	@Test
	public void testSetDetectManyToMany() {
		assertTrue(wrappedRevengSettings.getDetectManyToMany());
		assertSame(revengSettingsWrapper, revengSettingsWrapper.setDetectManyToMany(false));
		assertFalse(wrappedRevengSettings.getDetectManyToMany());
	}
	
	@Test
	public void testSetDetectOneToOne() {
		assertTrue(wrappedRevengSettings.getDetectOneToOne());
		assertSame(revengSettingsWrapper, revengSettingsWrapper.setDetectOneToOne(false));
		assertFalse(wrappedRevengSettings.getDetectOneToOne());
	}
	
	@Test
	public void testSetDetectOptimisticLock() {
		assertTrue(wrappedRevengSettings.getDetectOptimsticLock());
		assertSame(revengSettingsWrapper, revengSettingsWrapper.setDetectOptimisticLock(false));
		assertFalse(wrappedRevengSettings.getDetectOptimsticLock());
	}
	
}
