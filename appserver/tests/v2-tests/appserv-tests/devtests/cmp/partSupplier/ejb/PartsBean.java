/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package Data;

import javax.ejb.*;
import java.util.*; 

/**
 * Created Dec 16, 2002 1:22:14 PM
 * Code generated by the Forte For Java EJB Builder
 * @author mvatkina
 */


public abstract class PartsBean implements javax.ejb.EntityBean {
    
    private javax.ejb.EntityContext context;
    
    
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context=aContext;
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
        
    }
    
    
    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context=null;
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
        
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        
    }
    
    public abstract java.lang.Integer getPartid();
    public abstract void setPartid(java.lang.Integer partid);
    
    public abstract java.lang.String getName();
    public abstract void setName(java.lang.String name);
    
    public abstract java.lang.String getColor();
    public abstract void setColor(java.lang.String color);
    
    public abstract java.math.BigDecimal getWeight();
    public abstract void setWeight(java.math.BigDecimal weight);
    
    public abstract java.lang.Double getPrice();
    public abstract void setPrice(java.lang.Double price);
    
    public abstract java.util.Collection getSuppliers();
    
    public abstract void setSuppliers(java.util.Collection suppliers);
    
    public java.lang.Integer ejbCreate(java.lang.Integer partid, java.lang.String name, java.lang.String color, java.math.BigDecimal weight, java.lang.Double price) throws javax.ejb.CreateException {
        if (partid == null) {
            throw new javax.ejb.CreateException("The partid is required.");
        }
        setPartid(partid);
        setName(name);
        setColor(color);
        setWeight(weight);
        setPrice(price);

        return null;
    }
    
    public void ejbPostCreate(java.lang.Integer partid, java.lang.String name, java.lang.String color, java.math.BigDecimal weight, java.lang.Double price) throws javax.ejb.CreateException {
        
    }
    
    public void addSupplier(Data.LocalSuppliers supplier) {
        System.out.println("Debug : PartsBean addSupplier");
        if (supplier != null)
            System.out.println("Debug : supplier is null");
        try {
            java.util.Collection sups = getSuppliers();
            sups.add(supplier);

        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
    
    public void dropSupplier(Data.LocalSuppliers supplier) {
        System.out.println("Debug : PartsBean dropSupplier");
        try {
            java.util.Collection suppliers = getSuppliers();
            suppliers.remove(supplier);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
    
    public void testInCascadeDelete() {
        throw new RuntimeException("Called testInCascadeDelete!!!");
    }
    
}
