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

/**
 * @author leon
 */
public class ProductOwner {

    private String firstName;

    private String lastName;

    private ProductOwnerAddress address;
    
    private StoreCity account;
    
    public ProductOwner() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(ProductOwnerAddress address) {
        this.address = address;
    }

    public ProductOwnerAddress getAddress() {
        return address;
    }

    public StoreCity getAccount() {
        return account;
    }

    public void setAccount(StoreCity account) {
        this.account = account;
    }


}
