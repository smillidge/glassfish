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

package com.sun.enterprise.tools.verifier.tests.ejb.runtime;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;

/** message-destination
 *     message-destination-name
 *     jndi-name
 */

public class ASEjbMessageDestination extends EjbTest implements EjbCheck {

    public Result check(EjbDescriptor descriptor) {

        Result result = getInitializedResult();
        ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

        String messageDestinationName=null;
        String jndiName=null;
        int count = 0;
        try{
            count = getCountNodeSet("sun-ejb-jar/enterprise-beans/message-destination");
            if (count>0){
                for(int i=0;i<count;i++){
                    messageDestinationName = getXPathValue("sun-ejb-jar/enterprise-beans/message-destination/message-destination-name");
                    jndiName = getXPathValue("sun-ejb-jar/enterprise-beans/message-destination/jndi-name");
                    
                    if(messageDestinationName==null || messageDestinationName.length()==0){
                        result.addErrorDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
                        result.failed(smh.getLocalString
                            (getClass().getName() + ".failed1",
                            "FAILED [AS-EJB message-destination] : message-destination-name cannot be an empty string",
                            new Object[] {descriptor.getName()}));
                    }else{
                        result.addGoodDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
                        result.passed(smh.getLocalString(
                                            getClass().getName() + ".passed1",
                              "PASSED [AS-EJB message-destination] : message-destination-name is {1}",
                              new Object[] {descriptor.getName(),messageDestinationName}));
                    }
                    
                    if(jndiName==null || jndiName.length()==0){
                        result.addErrorDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
                        result.failed(smh.getLocalString
                            (getClass().getName() + ".failed2",
                            "FAILED [AS-EJB message-destination] : jndi-name cannot be an empty string",
                            new Object[] {descriptor.getName()}));
                    }else{
                        result.addGoodDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
                        result.passed(smh.getLocalString(
                                            getClass().getName() + ".passed2",
                              "PASSED [AS-EJB message-destination] : jndi-name is {1}",
                              new Object[] {descriptor.getName(),jndiName}));
                    }
                }
            }else{
                result.addNaDetails(smh.getLocalString
		    ("tests.componentNameConstructor",
		    "For [ {0} ]",
		    new Object[] {compName.toString()}));
                result.notApplicable(smh.getLocalString(getClass().getName()+".notApplicable",
                    "NOT APPLICABLE [AS-EJB message-destination] : message-destination Element not defined"));
            }

            
        }catch(Exception ex){
            result.failed(smh.getLocalString
                (getClass().getName() + ".failed",
                    "FAILED [AS-EJB message-destination] could not create the servlet object"));
        }
	return result;
    }

}
