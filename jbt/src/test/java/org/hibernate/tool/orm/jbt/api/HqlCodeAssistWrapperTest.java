package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCodeAssistWrapperFactory;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCodeAssistWrapperTest {
	
	private HqlCodeAssistWrapper hqlCodeAssistWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		Configuration c = new NativeConfiguration();
		c.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		Metadata m = MetadataHelper.getMetadata(c);
		hqlCodeAssistWrapper = HqlCodeAssistWrapperFactory.createHqlCodeAssistWrapper(m);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCodeAssistWrapper);
	}

}
