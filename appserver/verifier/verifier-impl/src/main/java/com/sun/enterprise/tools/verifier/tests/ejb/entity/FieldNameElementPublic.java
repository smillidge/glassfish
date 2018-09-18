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
import com.sun.enterprise.tools.verifier.VerifierTestContext;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbCMPEntityDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;
import org.glassfish.ejb.deployment.descriptor.FieldDescriptor;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;

/** 
 * The field-name element name must be a public field of the enterprise bean 
 * class or one of its superclasses. 
 */
public class FieldNameElementPublic extends EjbTest implements EjbCheck { 

    /**
     * The field-name element name must be a public field of the enterprise bean
     * class or one of its superclasses.
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	if (descriptor instanceof EjbEntityDescriptor) {
	    String persistentType = 
		((EjbEntityDescriptor)descriptor).getPersistenceType();
	    if (EjbEntityDescriptor.CONTAINER_PERSISTENCE.equals(persistentType)) {
                
                // this test apply only to 1.x cmp beans, in 2.x fields are virtual fields only
                if (EjbCMPEntityDescriptor.CMP_1_1!=((EjbCMPEntityDescriptor) descriptor).getCMPVersion()) {
		    result.addNaDetails(smh.getLocalString
					("tests.componentNameConstructor",
					 "For [ {0} ]",
					 new Object[] {compName.toString()}));
	            result.notApplicable(smh.getLocalString
				 ("com.sun.enterprise.tools.verifier.tests.ejb.entity.cmp2.CMPTest.notApplicable3",
				  "Test do not apply to this cmp-version of container managed persistence EJBs"));
        	    return result;                    
                }   
                
		// RULE: Entity w/Container managed persistence bean provider must 
		//       specify container managed fields in the persistent-fields 
		//       element. The field-name element name must be a public field 
		//       of the enterprise bean class or one of its superclasses. 

		boolean resolved = false;
		boolean oneFailed = false;
		
		if (!((EjbCMPEntityDescriptor)descriptor).getPersistenceDescriptor().getCMPFields().isEmpty()) {
		    // check class to get all fields that actually exist
		    try {
			VerifierTestContext context = getVerifierContext();
		ClassLoader jcl = context.getClassLoader();

			for (Iterator itr = 
				 ((EjbCMPEntityDescriptor)descriptor).getPersistenceDescriptor().getCMPFields().iterator();
			     itr.hasNext();) {

			    FieldDescriptor nextPersistentField = (FieldDescriptor)itr.next();
			    String fieldName = nextPersistentField.getName();
			    Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());

                            // start do while loop here....
			    do {
				// Returns an array containing Field objects
				// reflecting all the accessible public fields of the class 
				// or interface represented by this Class object.
				Field fields[] = c.getFields();
    
				//loop thru all field array elements and ensure fieldName exist
				for (int i=0; i < fields.length; i++) {
				    if (fieldName.equals(fields[i].getName())) {
					resolved = true;
					logger.log(Level.FINE, getClass().getName() + ".debug1",
                            new Object[] {fieldName,fields[i].toString(),c.getName()});
					result.addGoodDetails(smh.getLocalString
							      ("tests.componentNameConstructor",
							       "For [ {0} ]",
							       new Object[] {compName.toString()}));
					result.addGoodDetails
					    (smh.getLocalString
					     (getClass().getName() + ".passed",
					      "[ {0} ] found in public fields of bean [ {1} ]",
					      new Object[] {fieldName,c.getName()}));
					break;
				    } else {					
                        logger.log(Level.FINE, getClass().getName() + ".debug",
                             new Object[] {fieldName,fields[i].toString(),c.getName()});   
				    }
				}
			    } while (((c = c.getSuperclass()) != null) && (!resolved));
 
			    // before you go onto the next field name, tell me whether you
			    // resolved the last field name okay
			    if (!resolved) {
				if (!oneFailed) {
				    oneFailed = true;
				}
				result.addErrorDetails(smh.getLocalString
						       ("tests.componentNameConstructor",
							"For [ {0} ]",
							new Object[] {compName.toString()}));
				result.addErrorDetails
				    (smh.getLocalString
				     (getClass().getName() + ".failed1",
				      "Error: [ {0} ] not found in public fields of bean [ {1} ]",
				      new Object[] {fieldName,descriptor.getEjbClassName()}));
			    }
			    // clear the resolved flag for the next field name
			    if (resolved) {
				resolved = false;
			    }
			}
 
			if (oneFailed) {
			    result.setStatus(Result.FAILED);
			} else {
			    result.setStatus(Result.PASSED);
			}

		    } catch (Exception e) {
			Verifier.debug(e);
			result.addErrorDetails(smh.getLocalString
					       ("tests.componentNameConstructor",
						"For [ {0} ]",
						new Object[] {compName.toString()}));
			result.failed  
			    (smh.getLocalString
			     (getClass().getName() + ".failedException",
			      "Error: [ {0} ] within bean [ {1} ]",
			      new Object[] {e.getMessage(),descriptor.getName()}));
		    }
		} else {
		    // persistent fields are empty
		    result.addNaDetails(smh.getLocalString
					("tests.componentNameConstructor",
					 "For [ {0} ]",
					 new Object[] {compName.toString()}));
		    result.notApplicable(smh.getLocalString
					 (getClass().getName() + ".notApplicable1",
					  "No persistent fields are defined for bean [ {0} ]",
					  new Object[] {descriptor.getName()}));
		}
	    } else { //BEAN_PERSISTENCE.equals(persistentType)
		result.addNaDetails(smh.getLocalString
				    ("tests.componentNameConstructor",
				     "For [ {0} ]",
				     new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable2",
				      "Expected [ {0} ] managed persistence, but [ {1} ] bean has [ {2} ] managed persistence.",
				      new Object[] {EjbEntityDescriptor.CONTAINER_PERSISTENCE,descriptor.getName(),persistentType}));
	    } 
	    return result;
	} else {
	    result.addNaDetails(smh.getLocalString
				("tests.componentNameConstructor",
				 "For [ {0} ]",
				 new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "{0} expected \n {1} bean, but called with {2} bean",
				  new Object[] {getClass(),"Entity","Session"}));
	    return result;
	} 
    }
}
