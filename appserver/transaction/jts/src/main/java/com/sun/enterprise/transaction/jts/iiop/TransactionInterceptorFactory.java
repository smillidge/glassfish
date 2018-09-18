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

package com.sun.enterprise.transaction.jts.iiop;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.jts.pi.InterceptorImpl;
import com.sun.jts.jta.TransactionManagerImpl;
import com.sun.jts.CosTransactions.Configuration;

import org.glassfish.enterprise.iiop.api.IIOPInterceptorFactory;
import org.glassfish.enterprise.iiop.api.GlassFishORBHelper;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

import org.omg.IOP.Codec;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;

/**
 *
 * @author mvatkina
 */
@Service(name="TransactionInterceptorFactory")
public class TransactionInterceptorFactory implements IIOPInterceptorFactory{

    private TransactionServerInterceptor tsi = null;
    private TransactionClientInterceptor tci = null;

    @Inject private ServiceLocator habitat;

    public ClientRequestInterceptor createClientRequestInterceptor(ORBInitInfo info, Codec codec) {
        if (tci == null) {
            tci = new TransactionClientInterceptor("TransactionClientInterceptor", 1, habitat);
        }

        return tci;
    }

    public ServerRequestInterceptor createServerRequestInterceptor(ORBInitInfo info, Codec codec) {
        if (tsi == null) {
            tsi = new TransactionServerInterceptor(2, habitat);
        }

        return tsi;
    }

}
