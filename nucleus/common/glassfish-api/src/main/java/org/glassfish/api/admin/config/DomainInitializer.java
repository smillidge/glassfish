/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.api.admin.config;

import org.jvnet.hk2.annotations.Contract;
import org.glassfish.api.admin.config.Container;

/**
 * Marker interface to mark inhabitants that require some minimal initial 
 * configuration to be inserted into a newly create domain's domain.xml
 *
 * @author Nandini Ektare
 */
@Contract
public interface DomainInitializer {
    /**
     * The actual initial config that needs to be inserted into
     * the fresh domain.xml
     *
     * See {@link Attribute#value()} for how the default value is inferred.
     *
     */
    public <T extends Container> T getInitialConfig(DomainContext initialCtx);
}
