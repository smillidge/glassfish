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

package com.sun.enterprise.tools.verifier.tests.ejb.elements;

import com.sun.enterprise.deployment.EjbReferenceDescriptor;
import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import org.glassfish.ejb.deployment.descriptor.EjbBundleDescriptorImpl;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

/** 
 * The value of the ejb-link element must be the ejb-name of an enterprise
 * bean in the same ejb-jar file.
 */
public class EjbLinkElement extends EjbTest implements EjbCheck { 

    /**
     * The referenced bean must be an enterprise bean in the same ear file.
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	boolean resolved = false;
	boolean oneFailed = false;
	int na = 0;

	// The value of the ejb-link element must be the ejb-name of an enterprise
	// bean in the same J2EE Application archive.
        /*
	if (Verifier.getEarFile() == null) {
	    // this ejb-jar is not part of an .ear file
	    result.addNaDetails(smh.getLocalString
				  ("tests.componentNameConstructor",
				   "For [ {0} ]",
				   new Object[] {compName.toString()}));
	    result.notApplicable
		(smh.getLocalString(getClass().getName() + ".no_ear",
                "This ejb jar [ {0} ] is not part of a J2EE Archive.",
                 new Object[] {descriptor.getName()}));
	    return result;
	}
         */

//	String applicationName = null;
	// The value of the ejb-link element must be the ejb-name of an enterprise
	// bean in the same application .ear file.
	if (!descriptor.getEjbReferenceDescriptors().isEmpty()) {
	    for (Iterator itr = descriptor.getEjbReferenceDescriptors().iterator(); 
		 itr.hasNext();) {                                                     
		EjbReferenceDescriptor nextEjbReference = (EjbReferenceDescriptor) itr.next();
		if (nextEjbReference.isLinked()) {
		    String ejb_link = nextEjbReference.getLinkName();
		    ejb_link = ejb_link.substring(ejb_link.indexOf("#") + 1);

		    try {
                    
//                applicationName = application.getName();
//                File tmpFile = new File(System.getProperty("java.io.tmpdir"));
//                tmpFile = new File(tmpFile, Verifier.TMPFILENAME + ".tmp");
                Set ejbBundles = descriptor.getApplication().getBundleDescriptors(EjbBundleDescriptorImpl.class);
                Iterator ejbBundlesIterator = ejbBundles.iterator();
                EjbBundleDescriptorImpl ejbBundle = null;
 
			while (ejbBundlesIterator.hasNext()) {
			    ejbBundle = (EjbBundleDescriptorImpl)ejbBundlesIterator.next();
//                            if (Verifier.getEarFile() != null){
//                                archivist.extractEntry(ejbBundle.getModuleDescriptor().getArchiveUri(), tmpFile);
//                            }
			    for (Iterator itr2 = ejbBundle.getEjbs().iterator(); itr2.hasNext();) {

				EjbDescriptor ejbDescriptor = (EjbDescriptor) itr2.next();
				if (ejbDescriptor.getName().equals(ejb_link)) {
				    resolved = true;
				    logger.log(Level.FINE, getClass().getName() + ".passed",
                            new Object[] {ejb_link,ejbDescriptor.getName()});
				    addGoodDetails(result, compName);
				    result.addGoodDetails
					(smh.getLocalString
					 (getClass().getName() + ".passed",
					  "Valid referenced bean [ {0} ].",
					  new Object[] {ejb_link}));
				    break;
				}
			    }
    
			} 
		    } catch(Exception e) {
			addErrorDetails(result, compName);
			result.addErrorDetails(smh.getLocalString
					       (getClass().getName() + ".failedException1",
						"Exception occured while opening or saving the J2EE archive.",
						new Object[] {}));
                logger.log(Level.FINE, "com.sun.enterprise.tools.verifier.testsprint",
                        new Object[] {"[" + getClass() + "] Error: " + e.getMessage()});

                        if (!oneFailed) {
                            oneFailed = true;
                        }
		    }
    
		    // before you go onto the next ejb-link, tell me whether you
		    // resolved the last ejb-link okay
		    if (!resolved) {
                        if (!oneFailed) {
                            oneFailed = true;
                        }
			addErrorDetails(result, compName);
			result.addErrorDetails(smh.getLocalString
					       (getClass().getName() + ".failed",
						"Error: No EJB matching [ {0} ] found within [ {1} ] jar file.",
						new Object[] {ejb_link, descriptor.getName()}));
		    } else {
			// clear the resolved flag for the next ejb-link 
			resolved =false;
		    }
    
		} else {
		    // Cannot get the link name of an ejb reference referring 
		    // to an external bean, The value of the ejb-link element 
		    // must be the ejb-name of an enterprise bean in the same 
		    // ejb-ear file.
		    addNaDetails(result, compName);
		    result.addNaDetails
			(smh.getLocalString
			 (getClass().getName() + ".notApplicable1",
			  "Warning:  Cannot verify the existence of an ejb reference [ {0} ] to external bean within different .ear file.",
			  new Object[] {nextEjbReference.getName()}));
		    na++;
		}
	    }

	    if (oneFailed) {
		result.setStatus(Result.FAILED);
	    } else if (na == descriptor.getEjbReferenceDescriptors().size()) {
		result.setStatus(Result.NOT_APPLICABLE);
	    } else {
		result.setStatus(Result.PASSED);
	    }
//            File tmpFile = new File(System.getProperty("java.io.tmpdir"));
//            tmpFile = new File(tmpFile, Verifier.TMPFILENAME + ".tmp");
//	    tmpFile.delete();
	    return result;

	} else {
	    addNaDetails(result, compName);
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "There are no ejb references to other beans within this bean [ {0} ]",  
				  new Object[] {descriptor.getName()}));
	}

	return result;
    }
}
