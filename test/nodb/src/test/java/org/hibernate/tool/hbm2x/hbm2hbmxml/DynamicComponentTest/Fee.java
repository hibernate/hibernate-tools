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

package org.hibernate.tool.hbm2x.hbm2hbmxml.DynamicComponentTest;

import java.io.ObjectStreamClass;
import java.io.Serializable;

public class Fee implements Serializable {

	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(Fee.class).getSerialVersionUID();
		
	public Fee anotherFee;
	public String fi;
	public String key;
	private FooComponent compon;
	private int count;
	
	public Fee() {
	}
	
	public String getFi() {
		return fi;
	}
	
	public void setFi(String fi) {
		this.fi = fi;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Fee getAnotherFee() {
		return anotherFee;
	}
	
	public void setAnotherFee(Fee anotherFee) {
		this.anotherFee = anotherFee;
	}
	
	
	public FooComponent getCompon() {
		return compon;
	}
	
	public void setCompon(FooComponent compon) {
		this.compon = compon;
	}
	
	/**
	 * Returns the count.
	 * @return int
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 * @param count The count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}






