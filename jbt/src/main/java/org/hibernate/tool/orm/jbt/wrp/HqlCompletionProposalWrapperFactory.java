package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Property;
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
		default int getReplaceStart() { return ((HQLCompletionProposal)getWrappedObject()).getReplaceStart(); }
		default int getReplaceEnd() { return ((HQLCompletionProposal)getWrappedObject()).getReplaceEnd(); }
		default String getSimpleName() { return ((HQLCompletionProposal)getWrappedObject()).getSimpleName(); }
		default int getCompletionKind() { return ((HQLCompletionProposal)getWrappedObject()).getCompletionKind(); }
		default String getEntityName() { return ((HQLCompletionProposal)getWrappedObject()).getEntityName(); }
		default String getShortEntityName() { return ((HQLCompletionProposal)getWrappedObject()).getShortEntityName(); }
		default Property getProperty() { return ((HQLCompletionProposal)getWrappedObject()).getProperty(); }
	}

}
