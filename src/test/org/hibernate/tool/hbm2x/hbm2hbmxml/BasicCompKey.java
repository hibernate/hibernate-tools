
/*
 * Created on 16/01/2005
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

/**
 * Testing class for cfg2hbm generating hbms.
 * 
 * @author David Channon (with the help of hbm2java)
 */
public class BasicCompKey implements java.io.Serializable {

    // Fields
    private java.lang.String customerId;
    private java.lang.Integer orderNumber;
    private java.lang.String productId;

    // Constructors

    /** default constructor */
    public BasicCompKey() {
    }
    
    // Property accessors
    /**
     */
   public java.lang.String getCustomerId () {
        return this.customerId;
    }
    
   public void setCustomerId (java.lang.String customerId) {
        this.customerId = customerId;
    }
    /**
     */
   public java.lang.Integer getOrderNumber () {
        return this.orderNumber;
    }
    
   public void setOrderNumber (java.lang.Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
    /**
     */
   public java.lang.String getProductId () {
        return this.productId;
    }
    
   public void setProductId (java.lang.String productId) {
        this.productId = productId;
    }
}
