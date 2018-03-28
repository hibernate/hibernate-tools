package org.hibernate.tool.hbm2x.hbm2hbmxml.formula;

public class AddressId {
	private String type;
	private String addressId;
	
	public AddressId(String type, String customerId) {
		this.addressId = customerId;
		this.type = type;
	}
	
	public AddressId() {}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String customerId) {
		this.addressId = customerId;
	}
	public boolean equals(Object other) {
		if ( !(other instanceof AddressId) ) return false;
		AddressId add = (AddressId) other;
		return type.equals(add.type) && addressId.equals(add.addressId);
	}
	public int hashCode() {
		return addressId.hashCode() + type.hashCode();
	}
	
	public String toString() {
		return type + '#' + addressId;
	}

}
