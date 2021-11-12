/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
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

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

public class Glarch implements GlarchProxy, Lifecycle, Serializable {

	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(Glarch.class).getSerialVersionUID();
	
	private int version;
	private GlarchProxy next;
	private short order;
	private List<Object> strings;
	private Map<Object,Object> stringSets;
	private List<Object> fooComponents;
	private GlarchProxy[] proxyArray;
	private transient Map<Object, Object> dynaBean;
	private String immutable;
	private int derivedVersion;
	private Object any;
	private int x;
	private String name;

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public GlarchProxy getNext() {
		return next;
	}
	public void setNext(GlarchProxy next) {
		this.next = next;
	}

	public short getOrder() {
		return order;
	}
	public void setOrder(short order) {
		this.order = order;
	}

	public List<Object> getStrings() {
		return strings;
	}

	public void setStrings(List<Object> strings) {
		this.strings = strings;
	}

	public Map<Object, Object> getStringSets() {
		return stringSets;
	}

	public void setStringSets(Map<Object, Object> stringSets) {
		this.stringSets = stringSets;
	}

	public List<Object> getFooComponents() {
		return fooComponents;
	}

	public void setFooComponents(List<Object> fooComponents) {
		this.fooComponents = fooComponents;
	}

	public GlarchProxy[] getProxyArray() {
		return proxyArray;
	}
	public void setProxyArray(GlarchProxy[] proxyArray) {
		this.proxyArray = proxyArray;
	}

	public boolean onDelete(Session s) throws CallbackException {
		return NO_VETO;
	}

	public void onLoad(Session s, Serializable id) {
		if ( ! ( ( (String) id ).length()==32 ) ) throw new RuntimeException("id problem");
	}

	public boolean onSave(Session s) throws CallbackException {
		dynaBean = new HashMap<Object, Object>();
		dynaBean.put("foo", "foo");
		dynaBean.put("bar", new Integer(66));
		immutable="never changes!";
		return NO_VETO;
	}

	public boolean onUpdate(Session s) throws CallbackException {
		return NO_VETO;
	}

	/*public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}*/

	/**
	 * Returns the dynaBean.
	 * @return DynaBean
	 */
	public Map<Object, Object> getDynaBean() {
		return dynaBean;
	}

	/**
	 * Sets the dynaBean.
	 * @param dynaBean The dynaBean to set
	 */
	public void setDynaBean(Map<Object, Object> dynaBean) {
		this.dynaBean = dynaBean;
	}

	/**
	 * Returns the immutable.
	 * @return String
	 */
	public String getImmutable() {
		return immutable;
	}

	/**
	 * Sets the immutable.
	 * @param immutable The immutable to set
	 */
	public void setImmutable(String immutable) {
		this.immutable = immutable;
	}

	/**
	 * Returns the derivedVersion.
	 * @return int
	 */
	public int getDerivedVersion() {
		return derivedVersion;
	}

	/**
	 * Sets the derivedVersion.
	 * @param derivedVersion The derivedVersion to set
	 */
	public void setDerivedVersion(int derivedVersion) {
		this.derivedVersion = derivedVersion;
	}

	/**
	 * Returns the any.
	 * @return Object
	 */
	public Object getAny() {
		return any;
	}

	/**
	 * Sets the any.
	 * @param any The any to set
	 */
	public void setAny(Object any) {
		this.any = any;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}







