/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.admin.mbeanapi.deployment;

import java.util.Map;

import com.sun.appserv.management.config.JDBCConnectionPoolConfig;

/**
 */
public class CreateJDBCConnectionPoolCmd extends BaseResourceCmd
    implements SourceCmd
{
    public static final String kName      = "Name";
    public static final String kDatasourceClassname = "DatasourceClassname";

    public CreateJDBCConnectionPoolCmd(CmdEnv cmdEnv)
    {
        super(cmdEnv);
    }

    public Object execute() throws Exception
    {
        assert isConnected();

        final JDBCConnectionPoolConfig resource =
        	getDomainConfig().createJDBCConnectionPoolConfig(
            	getName(), getDatasourceClassname(), getOptional());
        return resource;
    }

    private String getName()
    {
        return (String)getCmdEnv().get(kName);
    }

    private String getDatasourceClassname()
    {
        return (String)getCmdEnv().get(kDatasourceClassname);
    }

}
