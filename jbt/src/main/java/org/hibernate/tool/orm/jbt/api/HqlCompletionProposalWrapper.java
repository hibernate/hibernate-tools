package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface HqlCompletionProposalWrapper extends Wrapper {

	default String getCompletion() { return ((HQLCompletionProposal)getWrappedObject()).getCompletion(); }

	default int getReplaceStart() { return ((HQLCompletionProposal)getWrappedObject()).getReplaceStart(); }

	default int getReplaceEnd() { return ((HQLCompletionProposal)getWrappedObject()).getReplaceEnd(); }
}
