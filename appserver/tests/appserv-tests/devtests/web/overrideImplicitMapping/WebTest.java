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
import java.net.HttpURLConnection;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for Issue 8648: override implicit mapping
 *
 */
public class WebTest {

    private static final String TEST_NAME = "override-implicit-mapping";
    private static final String EXPECTED = "My Jsp Processing";

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private String host;
    private String port;
    private String contextRoot;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
    }
    
    public static void main(String[] args) {
        stat.addDescription("Unit test for issue 8648");
        WebTest webTest = new WebTest(args);
        webTest.doTest();
	    stat.printSummary();
    }

    public void doTest() {
     
        try {

            URL url = new URL("http://" + host  + ":" + port
                              + contextRoot + "/index.jsp");
            System.out.println("Connecting to: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = null;
                BufferedReader reader = null;
                boolean match = false;
                try {
                    is = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        if (EXPECTED.equals(line)) {
                            match = true;
                            break;
                        }
                    }
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch(IOException ex) {
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch(IOException ex) {
                        }
                    }
                }
                stat.addStatus(TEST_NAME, ((match)? stat.PASS : stat.FAIL));
            } else {
                System.err.println("Incorrect response code: Expected: "
                                   + HttpURLConnection.HTTP_OK
                                   + ", received: " + responseCode);
                stat.addStatus(TEST_NAME, stat.FAIL);
            }

        } catch( Exception ex) {
            ex.printStackTrace();
            stat.addStatus(TEST_NAME, stat.FAIL);
        }
    }

}
