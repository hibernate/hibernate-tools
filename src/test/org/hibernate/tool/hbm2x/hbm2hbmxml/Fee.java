//$Id$
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.Serializable;
import java.util.Set;

public class Fee implements Serializable {
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






