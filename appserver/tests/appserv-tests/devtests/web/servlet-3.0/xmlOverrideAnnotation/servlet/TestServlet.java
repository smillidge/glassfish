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

package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="testServlet", urlPatterns={"/mytest"}, initParams={ @WebInitParam(name="n1", value="v1"), @WebInitParam(name="n2", value="v2") })
public class TestServlet extends HttpServlet {
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        PrintWriter writer = res.getWriter();
        writer.write("filterMessage=" + req.getAttribute("filterMessage"));
        String msg = "";
        Enumeration en = getInitParameterNames();
        while (en.hasMoreElements()) {
            String name = (String)en.nextElement();
            String value = getInitParameter(name);
            msg += name + "=" + value + ", ";
        } 
        writer.write(", initParams: " + msg + "\n");
    }
}
