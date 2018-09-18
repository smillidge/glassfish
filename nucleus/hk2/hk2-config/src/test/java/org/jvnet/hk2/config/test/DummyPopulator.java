/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.config.test;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.config.ConfigParser;
import org.jvnet.hk2.config.Populator;

/**
* Created by IntelliJ IDEA.
* User: makannan
* Date: 5/2/12
* Time: 11:11 AM
* To change this template use File | Settings | File Templates.
*/
@Service
public class DummyPopulator
    implements Populator {

    private boolean populateCalled;

    public void run(ConfigParser p) {
        populateCalled = true;
    }

    public boolean isPopulateCalled() {
        return populateCalled;
    }
}
