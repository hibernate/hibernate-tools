/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hibernate.tool.ide.completion;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author leon
 */
public class Product {

    private Long id;

    private int version;

    private Double weight;

    private BigDecimal price;

    private Set stores;

    private ProductOwner owner;

	private Set otherOwners;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Set getStores() {
        return stores;
    }

    public void setStores(Set stores) {
        this.stores = stores;
    }

    public void setOwner(ProductOwner owner) {
        this.owner = owner;
    }

    public ProductOwner getOwner() {
        return owner;
    }
    
    public Set getOtherOwners() {
    	return otherOwners;
    }

    public void setOtherOwners(Set otherOwners) {
		this.otherOwners = otherOwners;
	}
 }
