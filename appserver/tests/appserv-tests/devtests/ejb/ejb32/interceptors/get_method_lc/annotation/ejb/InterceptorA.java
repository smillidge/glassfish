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

package com.acme;

import jakarta.interceptor.InvocationContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.AroundConstruct;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class InterceptorA {

    @AroundConstruct
    private void create(InvocationContext ctx) {
        System.out.println("In InterceptorA.AroundConstruct");

        try {
            java.lang.reflect.Constructor<?> c = ctx.getConstructor();
            System.out.println("Using Constructor: " + c);
            ctx.proceed();
            BaseBean b = (BaseBean)ctx.getTarget();
            System.out.println("Created instance: " + b);
            b.ac = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void afterCreation(InvocationContext ctx) {
        System.out.println("In InterceptorA.PostConstruct");

        try {
            BaseBean b = (BaseBean)ctx.getTarget();
            b.method = ctx.getMethod();
            System.out.println("PostConstruct on : " + b + " method: " + b.method);
            if (b.pc) throw new Exception("PostConstruct already called for " + b);
            ctx.proceed();
            b.pc = true;
            b.method = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    private void preDestroy(InvocationContext ctx) {
        System.out.println("In InterceptorA.PreDestroy");
        try {
            BaseBean b = (BaseBean)ctx.getTarget();
            System.out.println("PreDestroy on : " + b);
            b.method = ctx.getMethod();
            System.out.println("PreDestroy on : " + b + " method: " + b.method);
            ctx.proceed();
            b.method = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AroundInvoke
    public Object interceptCall(InvocationContext ctx) throws Exception {
        System.out.println("In InterceptorA.AroundInvoke");
        BaseBean b = (BaseBean)ctx.getTarget();
        b.ai = true;
        System.out.println("AroundInvoke on : " + b);
        return ctx.proceed();
    }

}
