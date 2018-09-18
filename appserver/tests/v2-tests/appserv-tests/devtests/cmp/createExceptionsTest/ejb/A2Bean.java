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

package create;

import javax.ejb.*;
import javax.naming.*;

/**
 * 2.0 bean. 
 * @author mvatkina
 */


public abstract class A2Bean implements javax.ejb.EntityBean {
    
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
        System.out.println("Debug: A2Bean ejbRemove");
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

    public abstract java.lang.String getName() ;
    public abstract void setName(java.lang.String s) ;

    /** This ejbCreate/ejbPostCreate combination tests CreateException 
     * thrown from ejbPostCreate. 
     */
    public java.lang.String ejbCreate(java.lang.String name) throws javax.ejb.CreateException {

        setName(name);
        return null;
    }
    
    public void ejbPostCreate(java.lang.String name) throws javax.ejb.CreateException { 
        throw new javax.ejb.CreateException("A2Bean.ejbPostCreate");
    }

    /** This ejbCreate/ejbPostCreate combination tests CreateException 
     * thrown from ejbCreate. 
     */
    public java.lang.String ejbCreate() throws javax.ejb.CreateException {
       throw new javax.ejb.CreateException("A2Bean.ejbCreate");
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
    }
    
    /** This ejbCreate/ejbPostCreate combination tests that bean state is
     * reset prior to call to ejbCreate.
     */
    public java.lang.String ejbCreate(int i)  throws javax.ejb.CreateException {
        if (getName() != null) {
             throw new java.lang.IllegalStateException("A2Bean.ejbCreate not reset");
        }

        setName("A2Bean_" + i);
        return null;
    }

    public void ejbPostCreate(int i)   throws javax.ejb.CreateException {
    }

}
