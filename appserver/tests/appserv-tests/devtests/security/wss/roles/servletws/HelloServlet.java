/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.security.wss.roles.servletws;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(targetNamespace="http://servletws.roles.wss.security.s1asdev.sun.com", serviceName="WssRolesServletService")
public class HelloServlet {
    @Resource WebServiceContext wsContext;

    public String hello(String who) {
        if (!wsContext.isUserInRole("javaee") ||
                wsContext.isUserInRole("ejbuser")) {
            throw new RuntimeException("not of role javaee or of role ejbuser");
        }

        return wsContext.getUserPrincipal() + "Hello, " + who;
    }
}
