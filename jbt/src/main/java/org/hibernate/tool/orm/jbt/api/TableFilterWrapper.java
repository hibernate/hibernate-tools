package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableFilterWrapper extends Wrapper {

	void setExclude(boolean b);
	void setMatchCatalog(String s);
	void setMatchSchema(String s);
	void setMatchName(String s);
	Boolean getExclude();
	String getMatchCatalog();
	String getMatchSchema();
	String getMatchName();
}
