package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;

public interface CriteriaWrapper extends Wrapper {
	
	void setMaxResults(int intValue);
	List<?> list();

}
