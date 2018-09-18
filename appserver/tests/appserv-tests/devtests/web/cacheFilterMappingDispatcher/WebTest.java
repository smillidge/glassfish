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

import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for new <dispatcher> subelement of <cache-mapping>.
 *
 * According to the cache config in sun-web.xml, the response generated by
 * the "/dispatchTo" servlet is cached only if that servlet is the target of
 * a RequestDispatcher "include" or "forward" operation.
 */
public class WebTest {

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private static final String TEST_NAME = "cache-filter-mapping-dispatcher";

    public static void main(String[] args) {

        stat.addDescription("Unit test for new <dispatcher> subelement "
                            + "of <cache-mapping>");

        String host = args[0];
        String port = args[1];
        String contextRoot = args[2];
        String hostPortRoot = host  + ":" + port + contextRoot;

        boolean success = false;

        /*
         * Access /dispatchTo directly, expect "RESPONSE-0" in response
         * body.
         * Counter in /dispatchTo gets incremented.
         */
        success = doTest("http://" + hostPortRoot + "/dispatchTo", 
                         "RESPONSE-0");
        
        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-1" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
             success = doTest("http://" + hostPortRoot + "/dispatchTo",
                              "RESPONSE-1");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.forward() from
             * /dispatchFrom, expect response ("RESPONSE-2") in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=forward",
                             "RESPONSE-2");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.forward() from
             * /dispatchFrom, expect cached response ("RESPONSE-2") in response
             * body.
             * Counter in /dispatchTo does not get incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=forward",
                             "RESPONSE-2");
        }

        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-3" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchTo",
                             "RESPONSE-3");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.include() from
             * /dispatchFrom, expect response ("RESPONSE-4") in response
             * body..
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=include",
                             "RESPONSE-4");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.include() from
             * /dispatchFrom, expect cached response ("RESPONSE-4") in response
             * body.
             * Counter in /dispatchTo does not get incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=include",
                             "RESPONSE-4");
        }        

        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-5" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchTo",
                             "RESPONSE-5");
        }

        if (success) {
            stat.addStatus(TEST_NAME, stat.PASS);
        } else {
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

        stat.printSummary(TEST_NAME);
    }

    /*
     * Returns true in case of success, false otherwise.
     */
    private static boolean doTest(String urlString, String expected) {

        try {
            URL url = new URL(urlString);
            System.out.println("Connecting to: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) { 
                System.out.println("Wrong response code. Expected: 200"
                                   + ", received: " + responseCode);
                return false;
            }

            InputStream is = conn.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(is));
            String line = input.readLine();
            System.out.println("Response: " + line);
            if (!expected.equals(line)) {
                System.out.println("Wrong response. Expected: " + expected
                                   + ", received: " + line);
                return false;
            }

        } catch (Exception ex) {
            System.out.println(TEST_NAME + " test failed.");
            ex.printStackTrace();
            return false;
        }

        return true;

    }

}
