package org.hibernate.tool.orm.jbt.api.wrp;

public interface Cfg2HbmToolWrapper extends Wrapper {

	String getTag(PersistentClassWrapper pcw);
	String getTag(PropertyWrapper pw);

}
