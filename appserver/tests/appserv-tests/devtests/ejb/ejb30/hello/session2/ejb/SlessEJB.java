/*
 * Copyright (c) 2002, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.ejb.ejb30.hello.session2;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.annotation.Resource;

import javax.ejb.EJB;
import javax.ejb.RemoteHome;
import javax.ejb.CreateException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.transaction.UserTransaction;

@Stateless

@Remote(Sless.class)
@RemoteHome(SlessRemoteHome.class)
public class SlessEJB 
{
    @Resource(name="null") SessionContext sc;

    public String hello() {
        System.out.println("In SlessEJB:hello()");
        try {
            Object lookup = sc.lookup(null);
        } catch(IllegalArgumentException iae) {
            System.out.println("Successfully got IllegalArgException for " +
                               "SessionContext.lookup(null)");
        }
        SessionContext sc2 = (SessionContext) sc.lookup("null");
        System.out.println("SessionContext.lookup(\"null\") succeeded");

        return "hello from SlessEJB";
    }

    public String hello2() throws javax.ejb.CreateException {
        throw new javax.ejb.CreateException();
    }

    public String getId() {
        return "SlessEJB";
    }

    public Sless roundTrip(Sless s) {
        System.out.println("In SlessEJB::roundTrip " + s);
        System.out.println("input Sless.getId() = " + s.getId());
        return s;
    }

    public Collection roundTrip2(Collection collectionOfSless) {
        System.out.println("In SlessEJB::roundTrip2 " + 
                           collectionOfSless);
        if( collectionOfSless.size() > 0 ) {
            Sless sless = (Sless) collectionOfSless.iterator().next();
            System.out.println("input Sless.getId() = " + sless.getId());  
        }
        return collectionOfSless;
    }
}
