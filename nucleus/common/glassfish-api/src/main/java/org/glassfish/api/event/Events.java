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

package org.glassfish.api.event;

import org.jvnet.hk2.annotations.Contract;

/**
 * Contract to register/unregister events listener.
 * This implementation is not meant to be used for performance sensitive 
 * message delivery.
 * 
 * @author Jerome Dochez
 */
@Contract
public interface Events {

    /**
     * Registers a new listener for global events
     * 
     * @param listener the new listener
     */
    public void register(EventListener listener);

    /**
     * Unregisteres a listener
     *
     * @param listener the register to remove
     * @return true if the removal was successful
     */
    public boolean unregister(EventListener listener);

    /**
     * Sends a event asynchronously
     *
     * @param event event to send
     */
    public void send(EventListener.Event event);

    /**
     * Sends a event to all listener synchronously or asynchronously.
     *
     * @param event event to send
     * @param asynchronously true if the event should be sent asynchronously
     */
    public void send(EventListener.Event event, boolean asynchronously);
}
