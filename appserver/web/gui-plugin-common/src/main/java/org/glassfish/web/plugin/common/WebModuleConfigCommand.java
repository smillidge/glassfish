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

package org.glassfish.web.plugin.common;

import com.sun.enterprise.config.serverbeans.Application;
import com.sun.enterprise.config.serverbeans.Applications;
import com.sun.enterprise.config.serverbeans.Engine;
import com.sun.enterprise.config.serverbeans.Module;
import com.sun.enterprise.util.LocalStringManagerImpl;
import org.glassfish.api.ActionReport;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.web.config.serverbeans.WebModuleConfig;
import javax.inject.Inject;

/**
 * Superclass of all web module config-related commands.
 * 
 * All of these commands have the app-name (with perhaps /module-name appended)
 * as a required argument.
 * 
 * @author tjquinn
 */
public abstract class WebModuleConfigCommand implements AdminCommand {

    private final static String WEB_SNIFFER_TYPE = "web";

    private final static String LINE_SEP = System.getProperty("line.separator");

    @Param(primary=true)
    private String appNameAndOptionalModuleName;

    @Inject
    private Applications apps;

    static final protected LocalStringManagerImpl localStrings =
            new LocalStringManagerImpl(WebModuleConfigCommand.class);

    protected WebModuleConfig webModuleConfig(final ActionReport report) {
        Module m = module(report);
        if (m == null) {
            return null;
        }

        WebModuleConfig config = (WebModuleConfig) engine(report).getApplicationConfig();
        return config;
    }

    /**
     * Returns the Application corresponding to the app specified in the
     * command arguments.
     *
     * @return Application object for the app
     */
    private Application application() {
        final Application result = apps.getModule(Application.class,
                appName());

        return result;
    }

    private Module module(final ActionReport report) {
        final Application app = application();
        if (app == null) {
            fail(report, "appNotReg","Application {0} not registered",
                    appName());
            return null;
        }

        /*
         * Be helpful by announcing if the user specified a submodule but this
         * is not an EAR or if the user did NOT specify a submodule but this IS
         * an EAR.
         */
        if (app.isStandaloneModule() && appNameAndOptionalModuleName.contains("/")) {
            fail(report, "standaloneAppNoSubMods",
                    "Application {0} is a stand-alone application and contains no submodules but submodule {1} was specified",
                    appName(),
                    moduleName());
            return null;
        }

        if ( ! app.isStandaloneModule() && ! appNameAndOptionalModuleName.contains("/")) {
            fail(report, "earNoModuleSelection",
                    "Application {0} is an enterprise application; please also specify one of the web module names ({1}) as part of the command argument (for example, {0}/{2})",
                    appName(),
                    webModuleList(app),
                    app.getModule().get(0).getName());
            return null;
        }

        final Module module = app.getModule(moduleName());
        if (module == null) {
            if (app.getModule().isEmpty()) {
                fail(report, "noWebModules", "Application {0} contains no web modules",
                        appName());
            } else {
                fail(report, "noSuchModule","Application {0} contains web modules {1} but {2} is not one of them",
                        appName(),
                        webModuleList(app),
                        moduleName());
            }
        }
        return module;
    }

    private String webModuleList(final Application app) {
        /*
         * Build a list of web module names to include in the error message.
         */
        final StringBuilder moduleNames = new StringBuilder();
        for (Module m : app.getModule()) {
            if (m.getEngine("web") != null) {
                moduleNames.append((moduleNames.length() > 0 ? ", " : "")).
                        append(m.getName());
            }
        }
        return moduleNames.toString();
    }

    protected Engine engine(final ActionReport report) {
        Module module = module(report);
        if (module == null) {
            return null;
        }

        Engine e = module.getEngine(WEB_SNIFFER_TYPE);
        if (e == null) {
            fail(report, "noSuchEngine","Application {0}/module {1} does not contain engine {2}",
                    appName(),
                    moduleName(),
                    WEB_SNIFFER_TYPE);
        }
        return e;
    }

    /**
     * Returns either the explicit module name (if the command argument
     * specified one) or the app name.  An app can contain a module with the
     * same name.
     *
     * @return module name inferred from the command arguments
     */
    protected String moduleName() {
        final int endOfAppName = endOfAppName();
        return (endOfAppName == appNameAndOptionalModuleName.length()) ?
            appNameAndOptionalModuleName :
            appNameAndOptionalModuleName.substring(endOfAppName + 1);
    }

    protected String appName() {
        return appNameAndOptionalModuleName.substring(0, endOfAppName());
    }
    
    private int endOfAppName() {
        final int slash = appNameAndOptionalModuleName.indexOf('/');
        return (slash == -1 ? appNameAndOptionalModuleName.length() : slash);
    }

    protected String appNameAndOptionalModuleName() {
        return appNameAndOptionalModuleName;
    }

    protected ActionReport fail(final ActionReport report,
            final Exception e,
            final String msgKey,
            final String defaultFormat, Object... args) {
        report.setFailureCause(e);
        final StringBuilder causeMessages = new StringBuilder();
        Throwable t = e;
        while (t != null) {
            causeMessages.append(causeMessages.length() > 1 ? LINE_SEP : "").
                    append(t.getLocalizedMessage());
            t = t.getCause();
        }

        return fail(report, msgKey, defaultFormat + causeMessages.toString(), args);

    }
    protected ActionReport fail(final ActionReport report, final String msgKey,
            final String defaultFormat, Object... args) {
        return finish(report, ActionReport.ExitCode.FAILURE,
                msgKey, defaultFormat, args);
    }

    protected ActionReport succeed(final ActionReport report,
            final String msgKey, final String defaultFormat, Object... args) {
        return finish(report, ActionReport.ExitCode.SUCCESS,
                msgKey, defaultFormat, args);
    }

    protected String descriptionValueOrNotSpecified(final String value) {
        if (value != null) {
            return value;
        }
        return localStrings.getLocalString("notSpecified", "(description not specified)");

    }
    private ActionReport finish(final ActionReport report,
            final ActionReport.ExitCode exitCode,
            final String msgKey, final String defaultFormat, Object... args) {
        String msg = localStrings.getLocalString(msgKey, defaultFormat, args);
        report.setMessage(msg);
        report.setActionExitCode(exitCode);
        return report;
    }
}
