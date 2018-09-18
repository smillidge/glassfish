/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.tools.verifier.tests.ejb.intf.remoteintf;

import com.sun.enterprise.deployment.MethodDescriptor;
import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.RmiIIOPUtils;
import com.sun.enterprise.tools.verifier.tests.ejb.intf.InterfaceMethodTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;

import java.lang.reflect.Method;

/**
 * Remote interface business method throws clause must include the 
 *   java.rmi.RemoteException test.
 * Verify the following:
 * 
 *   The methods defined in this interface must follow the rules for RMI-IIOP. 
 *   This means that their throws clause must include the 
 *   java.rmi.RemoteException. 
 * 
 */
public class RemoteInterfaceRmiIIOPException extends InterfaceMethodTest { 
    
    /**
     * <p>
     * run an individual verifier test against a declared method of the 
     * remote interface.
     * </p>
     * 
     * @param descriptor the deployment descriptor for the bean
     * @param method the method to run the test on
     * @return true if the test passes
     */
    protected boolean runIndividualMethodTest(EjbDescriptor descriptor, Method method, Result result) {
        
        Class[] methodExceptionTypes = method.getExceptionTypes();
        ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();	  
        // The methods arguments types must be legal types for
        // RMI-IIOP.  This means that their exception values must
        // throw java.rmi.RemoteException
        // methods must also throw java.rmi.RemoteException
        if (RmiIIOPUtils.isValidRmiIIOPException(methodExceptionTypes)) {
            // this is the right exception for method, throws RemoteException
            addGoodDetails(result, compName);
            result.addGoodDetails(smh.getLocalString
                    (getClass().getName() + ".passed",
                    "[ {0} ] method properly throws java.rmi.RemoteException.",
                    new Object[] {method.getName()}));
            return true;
        } else {
            addErrorDetails(result, compName);
            result.addErrorDetails(smh.getLocalString
                    (getClass().getName() + ".failed",
                    "Error: [ {0} ] method was found, but does not properly " +
                    "throw java.rmi.RemoteException.",
                    new Object[] {method.getName()}));
            return false;
        }
    } 
    protected String getInterfaceName(EjbDescriptor descriptor) {
        return descriptor.getRemoteClassName();
    }
    
    protected String getInterfaceType() {
        return MethodDescriptor.EJB_REMOTE;
    }
}
