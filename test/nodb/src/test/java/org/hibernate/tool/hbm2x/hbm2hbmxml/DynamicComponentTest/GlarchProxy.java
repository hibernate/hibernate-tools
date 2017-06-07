package org.hibernate.tool.hbm2x.hbm2hbmxml.DynamicComponentTest;

import java.util.List;
import java.util.Map;

public interface GlarchProxy {
	
	public int getVersion();
	public int getDerivedVersion();
	public void setVersion(int version);
	
	public String getName();
	public void setName(String name);
	
	public GlarchProxy getNext();
	public void setNext(GlarchProxy next);
	
	public short getOrder();
	public void setOrder(short order);
	
	public List<Object> getStrings();
	public void setStrings(List<Object> strings);
	
	public Map<Object, Object> getDynaBean();
	public void setDynaBean(Map<Object, Object> bean);
	
	public Map<Object, Object> getStringSets();
	public void setStringSets(Map<Object, Object> stringSets);
	
	public List<Object> getFooComponents();
	public void setFooComponents(List<Object> fooComponents);
	
	public GlarchProxy[] getProxyArray();
	public void setProxyArray(GlarchProxy[] proxyArray);
	
	public Object getAny();
	public void setAny(Object any);
}







