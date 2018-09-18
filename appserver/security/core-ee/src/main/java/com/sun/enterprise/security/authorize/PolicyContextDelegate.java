/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.security.authorize;

import org.glassfish.api.invocation.ComponentInvocation;
import org.jvnet.hk2.annotations.Contract;

import java.lang.reflect.Method;

@Contract
public interface PolicyContextDelegate {

    Object getEnterpriseBean(ComponentInvocation inv);

    public Object getEJbArguments(ComponentInvocation inv);

    public Object getSOAPMessage(ComponentInvocation inv);

    public void setSOAPMessage(Object message, ComponentInvocation inv);

    public boolean authorize(ComponentInvocation inv, Method m) throws Exception;
}
