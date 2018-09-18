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

import com.sun.enterprise.deployment.EjbMessageBeanDescriptor;
import com.sun.enterprise.deployment.EjbSessionDescriptor;
import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbEntityDescriptor;
import org.glassfish.ejb.deployment.descriptor.runtime.IASEjbExtraDescriptors;

import java.lang.reflect.Method;


/** ejb [0,n]
 *   is-read-only-bean ? [String]
 *
 * The <B>is-read-only-bean</B> should be defined for entity beans only.
 * Also the implementation should not define ant create and remove methods.
 * This is recommended and not required
 * @author Irfan Ahmed
 */
public class ASEjbIsReadOnlyBean extends EjbTest implements EjbCheck { 

    boolean oneFailed= false;
    boolean oneWarning = false;

    /**
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
        
        try{
            IASEjbExtraDescriptors iasEjbExtraDesc = descriptor.getIASEjbExtraDescriptors();
            String s1 = ("/sun-ejb-jar/enterprise-beans/ejb[ejb-name=\""+descriptor.getName()+"\"]/is-read-only-bean");
            String isReadOnlyBean = getXPathValue(s1);
            if (isReadOnlyBean!=null){
                if (isReadOnlyBean.equals(new String("true"))){
                    testROBSpecific(descriptor,iasEjbExtraDesc,result);
                }else if (isReadOnlyBean.equals(new String("false"))){
                    result.addGoodDetails(smh.getLocalString
                                  ("tests.componentNameConstructor",
                                   "For [ {0} ]",
                                   new Object[] {compName.toString()}));
                    result.passed(smh.getLocalString(getClass().getName()+".passed",
                            "PASSED [AS-EJB ejb] is-read-only-bean is {0}",
                            new Object[]{new String(isReadOnlyBean)}));
                }else{
                    oneFailed = true;
                    result.addErrorDetails(smh.getLocalString
                                ("tests.componentNameConstructor",
                                "For [ {0} ]",
                                new Object[] {compName.toString()}));
                    result.failed(smh.getLocalString(getClass().getName()+".failed",
                            "FAILED [AS-EJB ejb] value [{0}] for is-read-only-bean is incorrect. It can only be true or false",
                            new Object[]{new String(isReadOnlyBean)}));
                    }
            }else
            {
                result.addNaDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.notApplicable(smh.getLocalString(getClass().getName()+".notApplicable",
                            "NOT APPLICABLE [AS-EJB ejb] is-read-only-bean Element not defined"));
            }
            if(oneFailed)
                result.setStatus(Result.FAILED);
            else if(oneWarning)
                result.setStatus(Result.WARNING);
            
        }catch(Exception ex){
            result.addErrorDetails(smh.getLocalString
                           ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
            result.addErrorDetails(smh.getLocalString
                 (getClass().getName() + ".notRun",
                  "NOT RUN [AS-EJB] : Could not create a descriptor object"));
        }
        return result;
        
    }

    public void testROBSpecific(EjbDescriptor descriptor, IASEjbExtraDescriptors iasEjbExtraDesc, Result result){
        ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
        try{
            //Read only Beans can only be Entity Beans
            if(descriptor instanceof EjbEntityDescriptor){
                result.addGoodDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.passed(smh.getLocalString(getClass().getName()+".passed1",
                    "PASSED [AS-EJB ejb] : Read Only Beans can only be Entity Beans"));
            }else if(descriptor instanceof EjbSessionDescriptor){
                oneFailed = true;
                result.addErrorDetails(smh.getLocalString
                                ("tests.componentNameConstructor",
                                "For [ {0} ]",
                                new Object[] {compName.toString()}));
                result.failed(smh.getLocalString(getClass().getName()+".failed1",
                    "FAILED [AS-EJB ejb] : Read Only Beans cannot be Session Beans. They can only be Entity Beans"));
                return;
            }else if(descriptor instanceof EjbMessageBeanDescriptor){
                oneFailed = true;
                result.addErrorDetails(smh.getLocalString
                                ("tests.componentNameConstructor",
                                "For [ {0} ]",
                                new Object[] {compName.toString()}));
                result.failed(smh.getLocalString(getClass().getName()+".failed2",
                    "FAILED [AS-EJB ejb] : Read Only Beans cannot be Message Driven Beans. They can only be Entity Beans"));
                return;
            }

            //Only Container Managed Transactions are Allowed
            String txnType = descriptor.getTransactionType();
            if(txnType.equals(descriptor.CONTAINER_TRANSACTION_TYPE)){
                result.addGoodDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.passed(smh.getLocalString(getClass().getName()+".passed2",
                    "PASSED [AS-EJB ejb] : Read Only Beans can only have Container Managed Transactions"));
            }else{
                oneFailed = true;
                result.addErrorDetails(smh.getLocalString
                                ("tests.componentNameConstructor",
                                "For [ {0} ]",
                                new Object[] {compName.toString()}));
                result.failed(smh.getLocalString(getClass().getName()+".failed3",
                    "FAILED [AS-EJB ejb] : Read Only Beans cannot have Bean Managed Transactions"));
            }

            //Encourage not to have create/remove methods in Home Interface
            String homeName = descriptor.getHomeClassName();
            Class homeClass = null;
            boolean foundCreateMethod = false;
            boolean foundRemoveMethod = false;
            homeClass = getVerifierContext().getClassLoader().loadClass(homeName);
            Method methods[] = homeClass.getDeclaredMethods();
            for(int i=0;i<methods.length;i++){
                if(methods[i].getName().startsWith("create")){
                    foundCreateMethod = true;
                    break;
                }
            }

            for(int i=0;i<methods.length;i++){
                if(methods[i].getName().startsWith("remove")){
                    foundRemoveMethod = true;
                    break;
                }
            }
            if(foundCreateMethod){
                oneWarning = true;
                result.addWarningDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.addWarningDetails(smh.getLocalString(getClass().getName()+".warning",
                    "WARNING [AS-EJB ejb] : Home interface [ {0} ] should have zero create Methods for Read Only Beans.",
                    new Object[] {homeName}));
            }else{
                result.addGoodDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.passed(smh.getLocalString(getClass().getName()+".passed3",
                    "PASSED [AS-EJB ejb] : Read Only Bean has zero create Methods"));
            }

            if(foundRemoveMethod){
                oneWarning = true;
                result.addWarningDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.addWarningDetails(smh.getLocalString(getClass().getName()+".warning1",
                    "WARNING [AS-EJB ejb] : Home interface [ {0} ] should have zero remove Methods for Read Only Beans.",
                    new Object[] {homeName}));
            }else{
                result.addGoodDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.passed(smh.getLocalString(getClass().getName()+".passed4",
                    "PASSED [AS-EJB ejb] : Read Only Bean has zero remove Methods"));
            }

            //Refresh Period Test
            int refreshPeriod = iasEjbExtraDesc.getRefreshPeriodInSeconds();
            if (refreshPeriod != -1){
                long refValue = new Integer(refreshPeriod).longValue();
                if(refValue <0 || refValue > Long.MAX_VALUE){
                    result.addErrorDetails(smh.getLocalString
                                    ("tests.componentNameConstructor",
                                    "For [ {0} ]",
                                    new Object[] {compName.toString()}));
                    result.failed(smh.getLocalString(getClass().getName()+".failed4",
                        "FAILED [AS-EJB ejb] : refresh-period-in-seconds is invalid. It should be between 0 and {0}." ,new Object[]{new Long( Long.MAX_VALUE)}));
                }else{
                    result.addGoodDetails(smh.getLocalString
                                  ("tests.componentNameConstructor",
                                   "For [ {0} ]",
                                   new Object[] {compName.toString()}));
                    result.passed(smh.getLocalString(getClass().getName()+".passed5",
                        "PASSED [AS-EJB ejb] : refresh-period-in-seconds is {0}",new Object[]{new Long(refValue)}));
                }
            }else{
                result.addNaDetails(smh.getLocalString
                              ("tests.componentNameConstructor",
                               "For [ {0} ]",
                               new Object[] {compName.toString()}));
                result.notApplicable(smh.getLocalString(getClass().getName()+".notApplicable1",
                    "NOT APPLICABLE [AS-EJB ejb] refresh-period-in-seconds Element not defined"));
            }

            if(oneFailed)
                result.setStatus(Result.FAILED);

        } catch(ClassNotFoundException cnfe) {
            oneFailed = true;
        } catch(RuntimeException ex){
            oneFailed = true;
        }
        if (oneFailed) {
            result.addErrorDetails(smh.getLocalString
                            ("tests.componentNameConstructor",
                            "For [ {0} ]",
                            new Object[] {compName.toString()}));
            result.addErrorDetails(smh.getLocalString
                 (getClass().getName() + ".notRun",
                  "NOT RUN [AS-EJB] : Could not create a descriptor object"));
        }

    }
}
