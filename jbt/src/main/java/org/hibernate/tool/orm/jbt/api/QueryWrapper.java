package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.Type;

public interface QueryWrapper extends Wrapper {

	List<?> list();
	void setMaxResults(int i);
	void setParameterList(String parameter, List<?> list, Object anything);
	void setParameter(String parameter, Object value, Object anything);
	void setParameter(int position, Object value, Object anything);
	String[] getReturnAliases();
	Type[] getReturnTypes();
	
}
