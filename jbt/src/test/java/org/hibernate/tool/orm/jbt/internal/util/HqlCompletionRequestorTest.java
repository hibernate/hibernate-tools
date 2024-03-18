package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
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
	
	@Test
	public void testAccept() {
		HQLCompletionProposal objectToAccept = new HQLCompletionProposal(0, 0);
		assertNull(acceptedObject);
		hqlCompletionRequestor.accept(objectToAccept);
		assertNotNull(acceptedObject);
		assertSame(objectToAccept, acceptedObject);
	}
	
	@Test
	public void testCompletionFailure() {
		assertNull(message);
		hqlCompletionRequestor.completionFailure("foobar");
		assertNotNull(message);
		assertEquals("foobar", message);
	}
	
}
