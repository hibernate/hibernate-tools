package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCompletionProposalWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCompletionProposalWrapperTest {

	private HqlCompletionProposalWrapper hqlCompletionProposalWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		hqlCompletionProposalWrapper = HqlCompletionProposalWrapperFactory
				.createHqlCompletionProposalWrapper(
						new HQLCompletionProposal(
								HQLCompletionProposal.PROPERTY, 
								Integer.MAX_VALUE));
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCompletionProposalWrapper);
	}
	
}
