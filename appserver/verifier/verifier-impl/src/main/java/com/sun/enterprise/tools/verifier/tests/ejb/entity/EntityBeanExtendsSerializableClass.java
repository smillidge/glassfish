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

package com.sun.enterprise.tools.verifier.tests.ejb.entity;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;

import java.util.logging.Level;

/** 
 * The interfaces/classes that the entity bean implements must be serializable 
 * directly or indirectly.
 * @author Sheetal Vartak
 */
public class EntityBeanExtendsSerializableClass extends EjbTest implements EjbCheck { 

    /**
     * The interfaces/classes that the entity bean implements must be 
     * serializable directly or indirectly.
     * Ejb 2.1 says that "The bean class that uses the timer service must 
     * implement the javax.ejb.TimedObject interface."
     * @param descriptor the Enterprise Java Bean deployment descriptor
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {
	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	if (descriptor instanceof EjbEntityDescriptor) {
	    try {
		Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());

		boolean validBean = true;
		Class superClass = c.getSuperclass();
		if (validBean == true) {
		    // walk up the class tree 
		  if(!(superClass.getName()).equals("java.lang.Object")) {
			if (!isValidSerializableType(superClass) &&
					!isTimedObject(superClass)) {
			    validBean = false;
			    result.addWarningDetails(smh.getLocalString
						     ("tests.componentNameConstructor",
						      "For [ {0} ]",
						      new Object[] {compName.toString()}));
			    result.addWarningDetails(smh.getLocalString
						     (getClass().getName() + ".failed1",
						      "[ {0} ] extends class [ {1} ] which is not serializable. ",
						      new Object[] {descriptor.getEjbClassName(),superClass.getName()}));
			    result.setStatus(Result.WARNING);
			    return result;
			} else {
			    result.addGoodDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
			    result.addGoodDetails(smh.getLocalString
						  (getClass().getName() + ".passed1",
						   "Bean [ {0} ] extends class [ {1} ] which is serializable. ",
						   new Object[] {descriptor.getEjbClassName(), superClass.getName()}));
			    do {
				Class[] interfaces = c.getInterfaces();
				
				for (int i = 0; i < interfaces.length; i++) {
				    
                    logger.log(Level.FINE, getClass().getName() + ".debug1",
                            new Object[] {interfaces[i].getName()});

				    if (!isValidSerializableType(interfaces[i])
					 && !isTimedObject(interfaces[i])) {
					validBean = false;
					result.addWarningDetails(smh.getLocalString
								 ("tests.componentNameConstructor",
								  "For [ {0} ]",
								  new Object[] {compName.toString()}));
					result.addWarningDetails(smh.getLocalString
						   (getClass().getName() + ".failed",
						   "[ {0} ] implements interface [ {1} ] which is not serializable. ",
						   new Object[] {descriptor.getEjbClassName(),interfaces[i].getName()}));
					result.setStatus(Result.WARNING);
					break;
				    }
				}
			    } while ((((c=c.getSuperclass()) != null) && (validBean != false)));
			}
		    }
		    if (validBean == true){
			result.addGoodDetails(smh.getLocalString
					      ("tests.componentNameConstructor",
					       "For [ {0} ]",
					       new Object[] {compName.toString()}));
			result.passed(smh.getLocalString
				      (getClass().getName() + ".passed",
				       "Bean [ {0} ] implements interfaces which are all serializable. ",
				       new Object[] {descriptor.getEjbClassName()}));
			result.setStatus(Result.PASSED);
		    }
		}


	    } catch (ClassNotFoundException e) {
		Verifier.debug(e);
		result.addErrorDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
		result.failed(smh.getLocalString
			      (getClass().getName() + ".failedException",
			       "Error: [ {0} ] class not found.",
			       new Object[] {descriptor.getEjbClassName()}));
	    }  

	    return result;
	    
	} else {
	    result.addNaDetails(smh.getLocalString
				("tests.componentNameConstructor",
				 "For [ {0} ]",
				 new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "[ {0} ] expected {1} bean, but called with {2} bean.",
				  new Object[] {getClass(),"Entity","Session"}));
	    return result;
	}
    }

    /** Class checked for implementing java.io.Serializable interface test.
     * Verify the following:
     *
     *   The class must implement the java.io.Serializable interface, either
     *   directly or indirectly.
     *
     * @param serClass the class to be checked for Rmi-IIOP value type
     *        compliance
     *
     * @return <code>boolean</code> true if class implements java.io.Serializable, false otherwise
     */
    public static boolean isValidSerializableType(Class serClass) {

        if (java.io.Serializable.class.isAssignableFrom(serClass))
            return true;
        else
            return false;
    }

    public static boolean isTimedObject(Class serClass) {
        if (javax.ejb.TimedObject.class.isAssignableFrom(serClass))
            return true;
        else
            return false;
    }
}
