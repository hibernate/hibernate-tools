package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.mapping.Property;

public interface HqlCompletionProposalWrapper extends Wrapper {

	String getCompletion();

	int getReplaceStart();

	int getReplaceEnd();

	String getSimpleName();

	int getCompletionKind();

	String getEntityName();

	String getShortEntityName();

	Property getProperty();

	int aliasRefKind();

	int entityNameKind();

	int propertyKind();

	int keywordKind();

	int functionKind();

}
