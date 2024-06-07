package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface HqlCodeAssistWrapper extends Wrapper {
	
	void codeComplete(String query, int position, Object handler);
	
}
