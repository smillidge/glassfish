/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.ant.tasks;

import org.apache.tools.ant.BuildException;


public class StartServerTask extends AdminTask {

    private String domain;

    public StartServerTask() {
        setCommand("start-domain");
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setDomaindir(String domaindir) {
        addCommandParameter("domaindir", domaindir);
    }

    public void setDebug(Boolean debug) {
        addCommandParameter("debug", debug.toString());
    }

    public void execute() throws BuildException {
        if (domain != null)
            addCommandOperand(domain);
        super.execute();
    }
}
