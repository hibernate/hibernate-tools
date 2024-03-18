package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.api.HqlCompletionProposalWrapper;

public class HqlCompletionProposalWrapperFactory {

	public static HqlCompletionProposalWrapper createHqlCompletionProposalWrapper(
			final HQLCompletionProposal wrappedCompletionProposal) {
		return new HqlCompletionProposalWrapper() {
			@Override public HQLCompletionProposal getWrappedObject() { return wrappedCompletionProposal; }
		};
	}

}
