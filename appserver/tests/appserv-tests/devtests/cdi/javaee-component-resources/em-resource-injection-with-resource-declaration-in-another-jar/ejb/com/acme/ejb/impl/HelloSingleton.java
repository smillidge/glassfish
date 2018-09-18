/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.acme.ejb.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.acme.ejb.api.Hello;
import com.acme.util.TestDatabase;

@Singleton
@Startup
@DependsOn("Singleton4")
public class HelloSingleton implements Hello {

    @Resource
    SessionContext sessionCtx;

    @PersistenceUnit(unitName = "pu1")
    @TestDatabase
    private EntityManagerFactory emf;
    

    @PostConstruct
    private void init() {
        System.out.println("HelloSingleton::init()");

        String appName;
        String moduleName;
        appName = (String) sessionCtx.lookup("java:app/AppName");
        moduleName = (String) sessionCtx.lookup("java:module/ModuleName");
        System.out.println("AppName = " + appName);
        System.out.println("ModuleName = " + moduleName);
    }

    public String hello() {
        System.out.println("HelloSingleton::hello()");
        return testEMF(emf);
    }

    private String testEMF(EntityManagerFactory emf2) {
        if (emf == null) return "EMF injection failed, is null in Singleton EJB";
        if (emf.createEntityManager() == null) return "Usage of EMF failed in Singleton EJB";
        return Hello.HELLO_TEST_STRING;
    }

    @PreDestroy
    private void destroy() {
        System.out.println("HelloSingleton::destroy()");
    }

}
