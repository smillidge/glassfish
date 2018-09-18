/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.osgi.cli.remote;

import com.sun.enterprise.admin.remote.ServerRemoteAdminCommand;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.config.serverbeans.Server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.shell.ShellService;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AccessRequired;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandException;
import org.glassfish.api.admin.CommandLock;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.admin.RestEndpoint;
import org.glassfish.api.admin.RestEndpoints;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.config.support.TargetType;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

/**
 * A simple AdminCommand that bridges to the Felix Shell Service.
 * Since the Felix Shell Service is compatible with all OSGi platforms,
 * this command is named as osgi instead of felix.
 *
 * @author ancoron
 */
@Service(name = "osgi")
@CommandLock(CommandLock.LockType.SHARED)
@I18n("osgi")
@PerLookup
@TargetType({CommandTarget.CLUSTERED_INSTANCE, CommandTarget.STANDALONE_INSTANCE})
@RestEndpoints({
    @RestEndpoint(configBean=Domain.class,
        opType=RestEndpoint.OpType.POST, 
        path="osgi",
        description="Remote OSGi Shell Access")
})
@AccessRequired(resource="domain/osgi/shell", action="execute")
public class OSGiShellCommand implements AdminCommand, PostConstruct {

    private static final Logger log = Logger.getLogger(OSGiShellCommand.class.getPackage().getName());

    private static final Map<String, RemoteCommandSession> sessions =
            new ConcurrentHashMap<String, RemoteCommandSession>();

    @Param(name = "command-line", primary = true, optional = true, multiple = true, defaultValue = "help")
    private Object commandLine;

    @Param(name = "session", optional = true)
    private String sessionOp;

    @Param(name = "session-id", optional = true)
    private String sessionId;

    @Param(name = "instance", optional = true)
    private String instance;
    
    protected BundleContext ctx;

    @Inject
    ServiceLocator locator;

    @Inject
    Domain domain;

    @Override
    public void execute(AdminCommandContext context) {
        ActionReport report = context.getActionReport();

        if(instance != null) {
            Server svr = domain.getServerNamed(instance);
            if(svr == null) {
                report.setMessage("No server target found for "
                        + instance);
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                return;
            }
            String host = svr.getAdminHost();
            int port = svr.getAdminPort();

            try {
                ServerRemoteAdminCommand remote =
                        new ServerRemoteAdminCommand(
                                locator,
                                "osgi",
                                host,
                                port,
                                false,
                                "admin",
                                "",
                                log);

                ParameterMap params = new ParameterMap();

                if(commandLine == null) {
                    params.set("DEFAULT".toLowerCase(Locale.US), "asadmin-osgi-shell");
                } else if(commandLine instanceof String) {
                    params.set("DEFAULT".toLowerCase(Locale.US), (String) commandLine);
                } else if(commandLine instanceof List) {
                    params.set("DEFAULT".toLowerCase(Locale.US), (List<String>) commandLine);
                }

                if(sessionOp != null) {
                    params.set("session", sessionOp);
                }

                if(sessionId != null) {
                    params.set("session-id", sessionId);
                }

                report.setMessage(remote.executeCommand(params));
                report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
                return;
            } catch(CommandException x) {
                report.setMessage("Remote execution failed: "
                        + x.getMessage());
                report.setFailureCause(x);
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                return;
            }
        }

        String cmdName = "";
        String cmd = "";
        if(commandLine == null) {
            cmd = "asadmin-osgi-shell";
            cmdName = cmd;
        } else if(commandLine instanceof String) {
            cmd = (String) commandLine;
            cmdName = cmd;
        } else if(commandLine instanceof List) {
            for(Object arg : (List) commandLine) {
                if(cmd.length() == 0) {
                    // first arg
                    cmd = (String) arg;
                    cmdName = cmd;
                } else {
                    cmd += " " + (String) arg;
                }
            }
        } else if(commandLine instanceof String[]) {
            for(Object arg : (String[]) commandLine) {
                if(cmd.length() == 0) {
                    // first arg
                    cmd = (String) arg;
                    cmdName = cmd;
                } else {
                    cmd += " " + (String) arg;
                }
            }
        } else {
            // shouldn't happen...
            report.setMessage("Unable to deal with argument list of type "
                    + commandLine.getClass().getName());
            report.setActionExitCode(ActionReport.ExitCode.WARNING);
            return;
        }

        // standard output...
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(512);
        PrintStream out = new PrintStream(bOut);

        // error output...
        ByteArrayOutputStream bErr = new ByteArrayOutputStream(512);
        PrintStream err = new PrintStream(bErr);

        try {
            Object shell = null;

            ServiceReference sref = ctx.getServiceReference(
                    "org.apache.felix.service.command.CommandProcessor");
            if(sref != null) {
                shell = ctx.getService(sref);
            }

            if(shell == null) {
                // try with felix...
                sref = ctx.getServiceReference("org.apache.felix.shell.ShellService");
                if(sref != null) {
                    shell = ctx.getService(sref);
                }

                if(shell == null) {
                    report.setMessage("No Shell Service available");
                    report.setActionExitCode(ActionReport.ExitCode.WARNING);
                    return;
                } else if("asadmin-osgi-shell".equals(cmdName)) {
                    out.println("felix");
                } else {
                    ShellService s = (ShellService) shell;
                    s.executeCommand(cmd, out, err);
                }
            } else {
                // try with gogo...

                // GLASSFISH-19126 - prepare fake input stream...
                InputStream in = new InputStream() {

                    @Override
                    public int read() throws IOException {
                        return -1;
                    }

                    @Override
                    public int available() throws IOException {
                        return 0;
                    }

                    @Override
                    public int read(byte[] b) throws IOException {
                        return -1;
                    }

                    @Override
                    public int read(byte[] b, int off, int len) throws IOException {
                        return -1;
                    }
                };

                CommandProcessor cp = (CommandProcessor) shell;
                if(sessionOp == null) {
                    if("asadmin-osgi-shell".equals(cmdName)) {
                        out.println("gogo");
                    } else {
                        CommandSession session = cp.createSession(in, out, err);
                        session.execute(cmd);
                        session.close();
                    }
                } else if("new".equals(sessionOp)) {
                    CommandSession session = cp.createSession(in, out, err);
                    RemoteCommandSession remote = new RemoteCommandSession(session);

                    log.log(Level.FINE, "Remote session established: {0}",
                            remote.getId());

                    sessions.put(remote.getId(), remote);
                    out.println(remote.getId());
                } else if("list".equals(sessionOp)) {
                    for(String id : sessions.keySet()) {
                        out.println(id);
                    }
                } else if("execute".equals(sessionOp)) {
                    RemoteCommandSession remote = sessions.get(sessionId);
                    CommandSession session = remote.attach(in, out, err);
                    session.execute(cmd);
                    remote.detach();
                } else if("stop".equals(sessionOp)) {
                    RemoteCommandSession remote = sessions.remove(sessionId);
                    CommandSession session = remote.attach(in, out, err);
                    session.close();

                    log.log(Level.FINE, "Remote session closed: {0}",
                            remote.getId());
                }
            }

            out.flush();
            err.flush();

            String output = bOut.toString("UTF-8");
            String errors = bErr.toString("UTF-8");
            report.setMessage(output);

            if(errors.length() > 0) {
                report.setMessage(errors);
                report.setActionExitCode(ActionReport.ExitCode.WARNING);
            } else {
                report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
            }
        } catch (Exception ex) {
            report.setMessage(ex.getMessage());
            report.setActionExitCode(ActionReport.ExitCode.WARNING);
        } finally {
            out.close();
            err.close();
        }
    }

    @Override
    public void postConstruct() {
        if(ctx == null) {
            Bundle me = BundleReference.class.cast(getClass().getClassLoader()).getBundle();
            ctx = me.getBundleContext();
        }
    }
}
