package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;

public interface QueryWrapper extends Wrapper {

	List<?> list();
	void setMaxResults(int i);
	void setParameterList(String parameter, List<?> list, Object anything);
	void setParameter(String parameter, Object value, Object anything);
	void setParameter(int position, Object value, Object anything);
	String[] getReturnAliases();
	TypeWrapper[] getReturnTypes();
	
}
