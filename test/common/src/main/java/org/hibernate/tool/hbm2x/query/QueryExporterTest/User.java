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
package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
	
    private static final long serialVersionUID =
			ObjectStreamClass.lookup(User.class).getSerialVersionUID();
	
	private Set<Group> groups = new HashSet<>();
	private UserID userId;

	public User(String name, String org) {
		this.setUserId(new UserID(name, org));
	}

	public User() {
	}
	
	public void setUserId(UserID userId) {
		this.userId = userId;
	}
	
	public UserID getUserId() {
		return userId;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

}
