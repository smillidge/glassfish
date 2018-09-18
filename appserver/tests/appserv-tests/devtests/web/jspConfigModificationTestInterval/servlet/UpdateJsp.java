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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class UpdateJsp extends HttpServlet {

    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        String path = getServletContext().getRealPath("/jsp/test.jsp");
        File f = new File(path);
        PrintStream ps = new PrintStream(new FileOutputStream(f));
        ps.println("updated jsp");
        ps.close();

        /*
         * Manually update timestamp on JSP file to a value far into the
         * future, so that the corresponding servlet class file will be 
         * guaranteed to be out-of-date
         */
        f.setLastModified(System.currentTimeMillis() + 9000000000L);

        res.getWriter().println("Done");
    }
}
