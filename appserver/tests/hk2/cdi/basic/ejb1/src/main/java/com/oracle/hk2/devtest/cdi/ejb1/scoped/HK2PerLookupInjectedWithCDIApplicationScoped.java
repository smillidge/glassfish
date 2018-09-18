/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.oracle.hk2.devtest.cdi.ejb1.scoped;

import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;

/**
 * This is a per-lookup hk2 cdi service injected with an application scoped CDI
 * service, in order to test whether or not a normal scoped CDI service can
 * properly be injected into an hk2 service
 * 
 * @author jwells
 *
 */
@Service @PerLookup
public class HK2PerLookupInjectedWithCDIApplicationScoped {
    @Inject
    private CountingApplicationScopedCDIService cdiApplicationScopedService;
    
    public CountingApplicationScopedCDIService getCountingCDIService() {
        return cdiApplicationScopedService;
    }

}
