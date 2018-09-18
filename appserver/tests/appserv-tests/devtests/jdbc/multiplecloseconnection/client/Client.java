/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.jdbc.multiplecloseconnection.client;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;
import com.sun.s1asdev.jdbc.multiplecloseconnection.ejb.SimpleSession;
import com.sun.s1asdev.jdbc.multiplecloseconnection.ejb.SimpleSessionHome;

import javax.naming.InitialContext;

public class Client {

    public static void main(String[] args)
            throws Exception {

        SimpleReporterAdapter stat = new SimpleReporterAdapter();
        String testSuite = "MultipleCloseConnection ";

        InitialContext ic = new InitialContext();
        Object objRef = ic.lookup("java:comp/env/ejb/SimpleSessionHome");
        SimpleSessionHome simpleSessionHome = (SimpleSessionHome)
                javax.rmi.PortableRemoteObject.narrow(objRef, SimpleSessionHome.class);
        stat.addDescription("Multiple Close Connection Tests");
        SimpleSession simpleSession = simpleSessionHome.create();

        boolean passed = true;
        for (int i = 0; i < 50; i++) {
            try {
                if (simpleSession.test1() == false) {
                    passed = false;
                    break;
                }
            } catch (Exception e) {
                passed = false;
                break;
            }
        }
        if (passed) {
            stat.addStatus(testSuite + " test1 : ", SimpleReporterAdapter.PASS);
        } else {
            stat.addStatus(testSuite + " test1 : ", SimpleReporterAdapter.FAIL);
        }

        passed = true;
        for (int i = 0; i < 50; i++) {
            try {
                if (simpleSession.test2() == false) {
                    passed = false;
                    break;
                }
            } catch (Exception e) {
                passed = false;
                break;
            }
        }
        if (passed) {
            stat.addStatus(testSuite + " test2 : ", SimpleReporterAdapter.PASS);
        } else {
            stat.addStatus(testSuite + " test2 : ", SimpleReporterAdapter.FAIL);
        }
        stat.printSummary();
    }
}
