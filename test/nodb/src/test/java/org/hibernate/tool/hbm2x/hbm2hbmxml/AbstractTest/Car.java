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

package org.hibernate.tool.hbm2x.hbm2hbmxml.AbstractTest;

import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Paco Hern�ndez
 */
public class Car implements java.io.Serializable {

	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(Car.class).getSerialVersionUID();
		
	private long id;
	private String model;
	private Set<CarPart> carParts = new HashSet<CarPart>();
	
	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return Returns the model.
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model The model to set.
	 */
	public void setModel(String model) {
		this.model = model;
	}
	public Set<CarPart> getCarParts() {
		return carParts;
	}
	public void setCarParts(Set<CarPart> carParts) {
		this.carParts = carParts;
	}
}
