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

package com.sun.enterprise.tools.verifier.tests.ejb.entity.primarykeyclass;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;

import java.util.logging.Level;

/** 
 * Entity bean's Primary Key Class test. 
 * If the enterprise bean is a Entity Bean, the Bean provider specifies
 * the fully qualified name of the Entity bean's primary key class in the 
 * "primary-class" element. The Bean provider 'must' specify the primary key
 * class for an Entity with bean managed persistence, and 'may' (but is not
 * required to) specify the primary key class for an Entity with 
 * Container-managed persistence. 
 */
public class PrimaryKeyClassElement extends EjbTest implements EjbCheck { 

    /**
     * Entity bean's Primary Key Class test.
     * If the enterprise bean is a Entity Bean, the Bean provider specifies
     * the fully qualified name of the Entity bean's primary key class in the
     * "primary-class" element. The Bean provider 'must' specify the primary key
     * class for an Entity with bean managed persistence, and 'may' (but is not
     * required to) specify the primary key class for an Entity with
     * Container-managed persistence.
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	if (descriptor instanceof EjbEntityDescriptor) {
	    String persistence = 
		((EjbEntityDescriptor)descriptor).getPersistenceType();
	    if (EjbEntityDescriptor.BEAN_PERSISTENCE.equals(persistence)) {		
        logger.log(Level.FINE, getClass().getName() + ".debug1",
                new Object[] {descriptor.getName(), "bean"});
		String primkey =
		    ((EjbEntityDescriptor)descriptor).getPrimaryKeyClassName();

		if (!primkey.equals("java.lang.String")) {
		    try {
			Class c = Class.forName(primkey, false, getVerifierContext().getClassLoader());

			result.addGoodDetails(smh.getLocalString
					      ("tests.componentNameConstructor",
					       "For [ {0} ]",
					       new Object[] {compName.toString()}));
			result.passed(smh.getLocalString
				      (getClass().getName() + ".passed",
				       "Primary Key Class [ {0} ] exist and is loadable",
				       new Object[] {primkey}));
		    } catch (Exception e) {
			Verifier.debug(e);
			result.addErrorDetails(smh.getLocalString
					       ("tests.componentNameConstructor",
						"For [ {0} ]",
						new Object[] {compName.toString()}));
			result.failed(smh.getLocalString
				      (getClass().getName() + ".failedException",
				       "Error: Loading Primary Key Class [ {0} ]",
				       new Object[] {primkey}));
			return result;
		    }
		} else {
		    result.addGoodDetails(smh.getLocalString
					  ("tests.componentNameConstructor",
					   "For [ {0} ]",
					   new Object[] {compName.toString()}));

		    result.passed(smh.getLocalString
				  (getClass().getName() + ".passed1",
				   "Primary Key Class is [ {0} ]",
				   new Object[] {primkey}));
		}

		return result;

	    } else if (EjbEntityDescriptor.CONTAINER_PERSISTENCE.equals(persistence)) {
		result.addNaDetails(smh.getLocalString
				    ("tests.componentNameConstructor",
				     "For [ {0} ]",
				     new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable1",
				      "Entity Bean [ {0} ] with [ {1} ] managed persistence, primkey optional.",
				      new Object[] {descriptor.getName(),persistence}));
		return result;
	    } 
	    else {
		result.addNaDetails(smh.getLocalString
				    ("tests.componentNameConstructor",
				     "For [ {0} ]",
				     new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable2",
				      "Expected [ {0} ] managed persistence, but [ {1} ] bean has [ {2} ] managed persistence.",
				      new Object[] {EjbEntityDescriptor.BEAN_PERSISTENCE,descriptor.getName(),persistence}));
		return result;
	    }
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
}
