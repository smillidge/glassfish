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

package com.sun.enterprise.tools.verifier.tests.util;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Stack;

import com.sun.enterprise.tools.verifier.apiscan.classfile.ClosureCompiler;
import com.sun.enterprise.tools.verifier.StringManagerHelper;
import com.sun.enterprise.util.LocalStringManagerImpl;

/**
 * @author Sudipto Ghosh
 */
public class WebArchiveLoadableHelper {

    public static String getFailedResults(ClosureCompiler cc, File jspDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Object o : cc.getFailed().entrySet()) {
            Map.Entry<String, List<String>> referencingPathToFailedList =
                    (Map.Entry<String, List<String>>)o;
            LocalStringManagerImpl smh = StringManagerHelper.getLocalStringsManager();
            String classes = "Failed to find following classes:";
            if (smh != null) {
                classes = smh.getLocalString(
                        WebArchiveLoadableHelper.class.getName() + ".classes",
                        classes);
            }
            sb.append(classes).append("\n[");
            for (Iterator<String> iii = referencingPathToFailedList.getValue().iterator();
                 iii.hasNext();) {
                sb.append("\n\t").append(iii.next());
                if(iii.hasNext()) sb.append(",");
            }
            sb.append("\n]");
            String referencingPath = referencingPathToFailedList.getKey();
            if(referencingPath.length()==0) continue; // skip if a top level class is not found
            String ref = "referenced in the following call stack :";
            String reference = "at";
            if (smh != null) {
                ref = smh.getLocalString(
                        WebArchiveLoadableHelper.class.getName() + ".ref",
                        ref);
                reference = smh.getLocalString(
                        WebArchiveLoadableHelper.class.getName() + ".reference",
                        reference);
            }
            StringTokenizer st = new StringTokenizer(referencingPath, File.separator);
            Stack<String> referencingClassStack = new Stack<String>();
            boolean jspDirExists = (jspDir != null && jspDir.exists());
            final String jspPkgName = "org.apache.jsp.";
            while(st.hasMoreTokens()) {
                String className = st.nextToken();
                //This logic is to map the compiled jsp class to original jsp name.
                //The logic is to find whether the ref class is in jsp out dir. If true
                //then maniputale the ref class to get the jsp name
                String fileName = className.replace(".", File.separator)+".class";
                if (jspDirExists &&
                        className.startsWith(jspPkgName) &&
                        new File(jspDir, fileName).exists()) {
                    StringBuilder jspName = new StringBuilder(
                            className.substring(jspPkgName.length()));
                    int innerClassIndex = jspName.indexOf("$");
                    if (innerClassIndex != -1) {
                        jspName = jspName.replace(innerClassIndex, jspName.length(), "");
                    }
                    if(jspName.toString().endsWith("_jsp")) {
                        jspName = jspName.replace(jspName.lastIndexOf("_jsp"),
                                jspName.length(), ".jsp");
                    } else if(jspName.toString().endsWith("_jspx")) {
                        jspName = jspName.replace(jspName.lastIndexOf("_jspx"),
                                jspName.length(), ".jspx");
                    }
                    className = jspName.toString();
                }
                referencingClassStack.push(className);
            }
            if ((!referencingClassStack.isEmpty()))
                sb.append("\n\n"+ref);
            while(!referencingClassStack.isEmpty()){
                sb.append("\n\t").append(reference).append(" ");
                sb.append(referencingClassStack.pop());
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }
}

