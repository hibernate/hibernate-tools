package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public class HqlCodeAssistWrapper extends HQLCodeAssist {

	public HqlCodeAssistWrapper(Metadata metadata) {
		super(metadata);
	}

}
