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

package org.hibernate.tool.jdbc2cfg.PersistentClasses;
import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 * 
 */
public class Orders {

    Integer id;

    String name;

    Set<Item> items = new HashSet<Item>();
    Set<Item> items_1 = new HashSet<Item>();
    

    /**
     * @return Returns the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the setOfItem.
     */
    public Set<Item> getItemsForOrderId() {
        return items;
    }

    /**
     * @param items
     *            The setOfItem to set.
     */
    public void setItemsForOrderId(Set<Item> items) {
        this.items = items;
    }
    /**
     * @return Returns the setOfItem_1.
     */
    public Set<Item> getItemsForRelatedOrderId() {
        return items_1;
    }
    /**
     * @param items_1 The setOfItem_1 to set.
     */
    public void setItemsForRelatedOrderId(Set<Item> items_1) {
        this.items_1 = items_1;
    }
}
