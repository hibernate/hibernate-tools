package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.Property;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.wrp.HqlCompletionProposalWrapperFactory.HqlCompletionProposalWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCompletionProposalWrapperFactoryTest {
	
	private HqlCompletionProposalWrapper hqlCompletionProposalWrapper = null;
	private HQLCompletionProposal hqlCompletionProposalTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		hqlCompletionProposalTarget = 
				new HQLCompletionProposal(
						HQLCompletionProposal.PROPERTY, 
						Integer.MAX_VALUE);
		hqlCompletionProposalWrapper = HqlCompletionProposalWrapperFactory
				.createHqlCompletionProposalWrapper(hqlCompletionProposalTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalWrapper);
	}
	
	@Test
	public void testGetCompletion() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getCompletion());
		hqlCompletionProposalTarget.setCompletion("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getCompletion());
	}
	
	@Test
	public void testGetReplaceStart() {
		assertNotEquals(Integer.MAX_VALUE, hqlCompletionProposalWrapper.getReplaceStart());
		hqlCompletionProposalTarget.setReplaceStart(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, hqlCompletionProposalWrapper.getReplaceStart());
	}
	
	@Test
	public void testGetReplaceEnd() {
		assertNotEquals(Integer.MIN_VALUE, hqlCompletionProposalWrapper.getReplaceEnd());
		hqlCompletionProposalTarget.setReplaceEnd(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, hqlCompletionProposalWrapper.getReplaceEnd());
	}
	
	@Test
	public void testGetSimpleName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getSimpleName());
		hqlCompletionProposalTarget.setSimpleName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getSimpleName());
	}
	
	@Test
	public void testGetCompletionKind() {
		assertEquals(HQLCompletionProposal.PROPERTY, hqlCompletionProposalWrapper.getCompletionKind());
		hqlCompletionProposalTarget.setCompletionKind(HQLCompletionProposal.KEYWORD);
		assertEquals(HQLCompletionProposal.KEYWORD, hqlCompletionProposalWrapper.getCompletionKind());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getEntityName());
		hqlCompletionProposalTarget.setEntityName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getEntityName());
	}
	
	@Test
	public void testGetShortEntityName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getShortEntityName());
		hqlCompletionProposalTarget.setShortEntityName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getShortEntityName());
	}
	
	@Test
	public void testGetProperty() {
		Property propertyTarget = new Property();
		assertNull(hqlCompletionProposalWrapper.getProperty());
		hqlCompletionProposalTarget.setProperty(propertyTarget);
		assertSame(propertyTarget, hqlCompletionProposalWrapper.getProperty());
	}
	
}
