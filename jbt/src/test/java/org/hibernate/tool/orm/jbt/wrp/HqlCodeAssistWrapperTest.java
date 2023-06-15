package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCodeAssistWrapperTest {
	
	private HqlCodeAssistWrapper hqlCodeAssistWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		Configuration configuration = new NativeConfiguration();
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		hqlCodeAssistWrapper = new HqlCodeAssistWrapper(metadata);
	}
	
	@Test
	public void testCodeComplete() {
		// First test the handler's 'accept' method
		TestCodeCompletionHandler completionHandler = new TestCodeCompletionHandler();
		assertEquals(0, completionHandler.acceptCount);
		hqlCodeAssistWrapper.codeComplete("", 0, completionHandler);
		assertNotEquals(0, completionHandler.acceptCount);
		// Now try to invoke the handler's 'completionFailure' method
		hqlCodeAssistWrapper = new HqlCodeAssistWrapper(null);
		assertNull(completionHandler.errorMessage);
		hqlCodeAssistWrapper.codeComplete("FROM ", 5, completionHandler);
		assertNotNull(completionHandler.errorMessage);
	}
	
	static class TestCodeCompletionHandler {
		int acceptCount = 0;
		String errorMessage = null;
		public boolean accept(Object o) {
			acceptCount++;
			return false;
		}
		public void completionFailure(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}

}
