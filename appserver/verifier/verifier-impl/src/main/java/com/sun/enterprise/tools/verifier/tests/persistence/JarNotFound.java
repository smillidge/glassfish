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

package com.sun.enterprise.tools.verifier.tests.persistence;

import org.glassfish.deployment.common.Descriptor;
import com.sun.enterprise.deployment.PersistenceUnitDescriptor;
import com.sun.enterprise.deployment.BundleDescriptor;
import org.glassfish.deployment.common.ModuleDescriptor;
import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.tests.VerifierCheck;
import com.sun.enterprise.tools.verifier.tests.VerifierTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.deployment.common.DeploymentUtils;
import org.glassfish.deployment.common.RootDeploymentDescriptor;

/**
 * jar files specified using <jar-file> element in persistence.xml should be
 * present in the archive.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class JarNotFound extends VerifierTest implements VerifierCheck {
    public Result check(Descriptor descriptor) {
        Result result = getInitializedResult();
        result.setStatus(Result.PASSED);
        addErrorDetails(result,
                getVerifierContext().getComponentNameConstructor());
        PersistenceUnitDescriptor pu = PersistenceUnitDescriptor.class.cast(
                descriptor);
        File absolutePURootFile = getAbsolutePuRootFile(pu);
        logger.fine("Absolute PU Root: " + absolutePURootFile);
        String absolutePuRoot = absolutePURootFile.getAbsolutePath();
        List<String> jarFileNames = new ArrayList<String>(pu.getJarFiles());
        for (String jarFileName : jarFileNames) {
            // ASSUMPTION:
            // Because of the way deployment changes names of directories etc.
            // it is very difficult to back calculate path names. So,
            // the following code assumes user is specifying valid URIs.

            // in the xml, names always use '/'
            String nativeJarFileName = jarFileName.replace('/',
                    File.separatorChar);
            final File parentFile = new File(absolutePuRoot).getParentFile();
            // only components are exploded, hence first look for original archives.
            File jarFile = new File(parentFile, nativeJarFileName);
            if (!jarFile.exists()) {
                // if the referenced jar is itself a component, then
                // it might have been exploded, hence let's see
                // if that is the case.

                // let's calculate the name component and path component from this URI
                // e.g. if URI is ../../foo_bar/my-ejb.jar,
                // name component is foo_bar/my-ejb.jar and
                // path component is ../../
                // These are my own notions used here.
                String pathComponent = "";
                String nameComponent = jarFileName;
                if(jarFileName.lastIndexOf("../") != -1) {
                    final int separatorIndex = jarFileName.lastIndexOf("../")+3;
                    pathComponent = jarFileName.substring(0,separatorIndex);
                    nameComponent = jarFileName.substring(separatorIndex);
                }
                logger.fine("For jar-file="+ jarFileName+ ", " +
                        "pathComponent=" +pathComponent +
                        ", nameComponent=" + nameComponent);
                File parentPath = new File(parentFile, pathComponent);
                jarFile = new File(parentPath, DeploymentUtils.
                        getRelativeEmbeddedModulePath(parentPath.
                        getAbsolutePath(), nameComponent));

                if (!jarFile.exists()) {
                    result.failed(smh.getLocalString(
                            getClass().getName() + "failed",
                            "[ {0} ] specified in persistence.xml does not exist in the application.",
                            new Object[]{jarFileName}));
                }
            }
        }
        return result;
    }

    private File getAbsolutePuRootFile(
            PersistenceUnitDescriptor persistenceUnitDescriptor) {
        final String applicationLocation =
                getVerifierContext().getAbstractArchive().getURI().getPath();
        File absolutePuRootFile = new File(applicationLocation,
                getAbsolutePuRoot(applicationLocation, 
                persistenceUnitDescriptor).replace('/', File.separatorChar));
        if (!absolutePuRootFile.exists()) {
            throw new RuntimeException(
                    absolutePuRootFile.getAbsolutePath() + " does not exist!");
        }
        return absolutePuRootFile;
    }

    /**
     * This method calculates the absolute path of the root of a PU.
     * Absolute path is not the path with regards to root of file system.
     * It is the path from the root of the Java EE application this
     * persistence unit belongs to.
     * Returned path always uses '/' as path separator.
     * @param applicationLocation absolute path of application root
     * @param persistenceUnitDescriptor
     * @return the absolute path of the root of this persistence unit
     */
    private String getAbsolutePuRoot(String applicationLocation,
            PersistenceUnitDescriptor persistenceUnitDescriptor) {
        RootDeploymentDescriptor rootDD = persistenceUnitDescriptor.getParent().                getParent();
        String puRoot = persistenceUnitDescriptor.getPuRoot();
        if(rootDD.isApplication()){
            return puRoot;
        } else {
            ModuleDescriptor module = BundleDescriptor.class.cast(rootDD).
                    getModuleDescriptor();
            if(module.isStandalone()) {
                return puRoot;
            } else {
                final String moduleLocation =
                        DeploymentUtils.getRelativeEmbeddedModulePath(
                        applicationLocation, module.getArchiveUri());
                return moduleLocation + '/' + puRoot; // see we always '/'
            }
        }
    }

}
