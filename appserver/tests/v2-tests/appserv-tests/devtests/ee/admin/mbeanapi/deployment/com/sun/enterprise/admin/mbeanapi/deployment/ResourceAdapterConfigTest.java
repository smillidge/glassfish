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
import java.util.HashMap;

import com.sun.appserv.management.config.ResourceAdapterConfig;
import com.sun.appserv.management.config.ResourceAdapterConfigKeys;


/**
 */
public class ResourceAdapterConfigTest extends BaseTest
{
    private final Cmd target;

    static final String kThreadPoolIDs  = "thread-pool-1";
    static final String kObjectType     = "user";
    static final String kName           = "someName";
    static final String kRACName        = "myResourceAdapter";

    public ResourceAdapterConfigTest(final String user, 
        final String password, final String host, final int port, 
        final String racName)
    {
        final CmdFactory cmdFactory = getCmdFactory();

        final ConnectCmd connectCmd = cmdFactory.createConnectCmd(
                user, password, host, port);

        final CreateResourceAdapterConfigCmd createRACCmd = 
                cmdFactory.createCreateResourceAdapterConfigCmd(racName,
                        getOptional());

        final DeleteResourceAdapterConfigCmd deleteRACCmd = 
                cmdFactory.createDeleteResourceAdapterConfigCmd(racName);

        final PipeCmd p1 = new PipeCmd(connectCmd, createRACCmd);
        final PipeCmd p2 = new PipeCmd(p1, new VerifyCreateCmd());
        final PipeCmd p3 = new PipeCmd(connectCmd, deleteRACCmd);

        final CmdChainCmd chainCmd = new CmdChainCmd();
        chainCmd.addCmd(p2);
        chainCmd.addCmd(p3);

        target = chainCmd;
    }

    protected void runInternal() throws Exception
    {
        target.execute();
    }


    public static void main(String[] args) throws Exception
    {
        new ResourceAdapterConfigTest(
                "admin", "password", "localhost", 8686, kRACName).run();
    }

    private Map getOptional()
    {
        final Map optional = new HashMap(3);

        optional.put(ResourceAdapterConfigKeys.THREAD_POOL_IDS_KEY, 
                kThreadPoolIDs);
        //optional.put(CreateResourceKeys.RESOURCE_OBJECT_TYPE_KEY, 
                //kObjectType);
        System.out.println("Remove hard coded Name");
        optional.put("Name", kName);

        return optional;
    }

    private final class VerifyCreateCmd implements Cmd, SinkCmd
    {
        private ResourceAdapterConfig rac;

        private VerifyCreateCmd()
        {
        }

        public void setPipedData(Object o)
        {
            rac = (ResourceAdapterConfig)o;
        }

        public Object execute() throws Exception
        {
            System.out.println(
                "ResourceAdapterName="+rac.getResourceAdapterName());
            System.out.println("Name="+rac.getName());
            System.out.println("ObjectType="+rac.getObjectType());
            System.out.println("ThreadPoolIDs="+rac.getThreadPoolIDs());

            return new Integer(0);
        }

    }
}
