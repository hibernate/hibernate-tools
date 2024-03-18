package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCompletionRequestorTest {
	
	private HqlCompletionRequestor hqlCompletionRequestor = null;
	
	private Object acceptedObject = null;
	private String message = null;
	
	@BeforeEach
	public void beforeEach() {
		hqlCompletionRequestor = new HqlCompletionRequestor(new Object() {
			@SuppressWarnings("unused")
			public boolean accept(Object o) { acceptedObject = o; return true; }
			@SuppressWarnings("unused")
			public void completionFailure(String s) { message = s; }
		});
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCompletionRequestor);
		assertNull(acceptedObject);
		assertNull(message);
	}
	
}
