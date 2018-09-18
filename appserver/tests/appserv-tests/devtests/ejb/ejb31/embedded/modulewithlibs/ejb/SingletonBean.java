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

package org.glassfish.tests.ejb.sample;

import javax.ejb.Singleton;
import javax.ejb.Startup; 
import javax.annotation.PreDestroy;
import javax.annotation.PostConstruct;

/**
 * @author Marina Vatkina
 */
@Singleton 
@Startup
public class SingletonBean {

    @javax.annotation.Resource(name="jdbc/__default") javax.sql.DataSource ds;

    @PostConstruct
    private void init() {
	System.out.println("ds = " + ds);
    }

    public String foo() {

        return "called";
    }

    @PreDestroy
    public void destroy() {
        System.out.println("SingletonBean :: In PreDestroy()");
    }
}
