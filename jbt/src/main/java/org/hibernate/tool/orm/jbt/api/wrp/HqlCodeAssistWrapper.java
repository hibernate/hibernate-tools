package org.hibernate.tool.orm.jbt.api.wrp;

public interface HqlCodeAssistWrapper extends Wrapper {
	
	void codeComplete(String query, int position, Object handler);
	
}
