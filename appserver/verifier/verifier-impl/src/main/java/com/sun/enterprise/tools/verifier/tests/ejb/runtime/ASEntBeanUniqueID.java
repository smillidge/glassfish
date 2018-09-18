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

/** enterprise-beans
 *    unique-id ? [String]
 *
 * The unique-id is automatically generated and updated at deployment/redeployment
 * The test is crude and only checks if the value promoted is not null.
 * @author Irfan Ahmed
 */
public class ASEntBeanUniqueID extends EjbTest implements EjbCheck { 

    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
        try{
            String s1 = ("/sun-ejb-jar/enterprise-beans/unique-id");
            String entBeanUniqueID = getXPathValue(s1);
            if(entBeanUniqueID == null)
            {
                result.addGoodDetails(smh.getLocalString
                    ("tests.componentNameConstructor",
                    "For [ {0} ]",
                    new Object[] {compName.toString()}));
                result.passed(smh.getLocalString(getClass().getName()+".passed",
                    "PASSED [AS-EJB enterprise-beans] The unique-id key should not be defined. It will be " +
                      "automatically generated at deployment time."));
            }
            else
            {
                result.addWarningDetails(smh.getLocalString
                                       ("tests.componentNameConstructor",
                                        "For [ {0} ]",
                                        new Object[] {compName.toString()}));
                result.warning(smh.getLocalString
                     (getClass().getName() + ".warning",
                      "WARNING [AS-EJB enterprise-beans] : unique-id Element should not be defined. It is " +
                      "automatically generated at deployment time."));
            }

        }catch(Exception ex){
            result.addErrorDetails(smh.getLocalString
                (getClass().getName() + ".notRun",
                "NOT RUN [AS-EJB] : Could not create a descriptor object"));
        }
        return result;
    }
}
