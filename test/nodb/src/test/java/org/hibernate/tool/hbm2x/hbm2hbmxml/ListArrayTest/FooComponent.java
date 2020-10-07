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

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Date;

public class FooComponent implements Serializable {
	
	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(FooComponent.class).getSerialVersionUID();
		
	int count;
	String name;
	Date[] importantDates;
	FooComponent subcomponent;
	Fee fee = new Fee();
	GlarchProxy glarch;
	
	public boolean equals(Object that) {
		FooComponent fc = (FooComponent) that;
		return count==fc.count;
	}
	
	public int hashCode() {
		return count;
	}
	
	public String toString() {
		String result = "FooComponent: " + name + "=" + count;
		result+="; dates=[";
		if ( importantDates!=null) {
			for ( int i=0; i<importantDates.length; i++ ) {
				result+=(i==0 ?"":", ") + importantDates[i];
			}
		}
		result+="]";
		if ( subcomponent!=null ) {
			result+= " (" + subcomponent + ")";
		}
		return result;
	}
	
	public FooComponent() {}
	
	FooComponent(String name, int count, Date[] dates, FooComponent subcomponent) {
		this.name = name;
		this.count = count;
		this.importantDates = dates;
		this.subcomponent = subcomponent;
	}
	
	FooComponent(String name, int count, Date[] dates, FooComponent subcomponent, Fee fee) {
		this.name = name;
		this.count = count;
		this.importantDates = dates;
		this.subcomponent = subcomponent;
		this.fee = fee;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date[] getImportantDates() {
		return importantDates;
	}
	public void setImportantDates(Date[] importantDates) {
		this.importantDates = importantDates;
	}
	
	public FooComponent getSubcomponent() {
		return subcomponent;
	}
	public void setSubcomponent(FooComponent subcomponent) {
		this.subcomponent = subcomponent;
	}
	
	@SuppressWarnings("unused")
	private String getNull() {
		return null;
	}
	@SuppressWarnings("unused")
	private void setNull(String str) throws Exception {
		if (str!=null) throw new Exception("null component property");
	}
	public Fee getFee() {
		return fee;
	}
	
	public void setFee(Fee fee) {
		this.fee = fee;
	}
	
	public GlarchProxy getGlarch() {
		return glarch;
	}
	
	public void setGlarch(GlarchProxy glarch) {
		this.glarch = glarch;
	}	

}







