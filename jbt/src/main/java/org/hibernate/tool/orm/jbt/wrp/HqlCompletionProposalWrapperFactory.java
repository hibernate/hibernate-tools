package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;

public class HqlCompletionProposalWrapperFactory {

	public static HqlCompletionProposalWrapper createHqlCompletionProposalWrapper(
			HQLCompletionProposal hqlCompletionProposalTarget) {
		return new HqlCompletionProposalWrapper() {
			public Object getWrappedObject() { return hqlCompletionProposalTarget; }
		};
	}
	
	public static interface HqlCompletionProposalWrapper extends Wrapper {
		default String getCompletion() { return ((HQLCompletionProposal)getWrappedObject()).getCompletion(); }
	}

}
