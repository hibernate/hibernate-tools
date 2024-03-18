package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCompletionProposalWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCompletionProposalWrapperTest {

	private HqlCompletionProposalWrapper hqlCompletionProposalWrapper = null;
	private HQLCompletionProposal wrappedHqlCompletionProposal = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedHqlCompletionProposal = new HQLCompletionProposal(
				HQLCompletionProposal.PROPERTY, 
				Integer.MAX_VALUE);
		hqlCompletionProposalWrapper = HqlCompletionProposalWrapperFactory
				.createHqlCompletionProposalWrapper(
						wrappedHqlCompletionProposal);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedHqlCompletionProposal);
		assertNotNull(hqlCompletionProposalWrapper);
	}
	
	@Test
	public void testGetCompletion() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getCompletion());
		wrappedHqlCompletionProposal.setCompletion("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getCompletion());
	}
	
}
