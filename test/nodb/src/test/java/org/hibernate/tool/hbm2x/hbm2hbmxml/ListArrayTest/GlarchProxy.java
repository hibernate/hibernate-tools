/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest;

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







