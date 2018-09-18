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

/*
 * PersistenceLauncher.java
 *
 * Created on July 3, 2000, 8:22 AM
 */

package com.sun.jdo.api.persistence.enhancer;

import java.util.Properties;

import java.io.PrintWriter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.sun.jdo.api.persistence.enhancer.util.Support;


/**
 * Application launcher for persistence-capable classes.
 * @author Martin Zaun
 * @version 1.1
 */
public class PersistenceLauncher {

    // chose whether to separate or join out and err channels
    //static private final PrintWriter err = new PrintWriter(System.err, true);
    static private final PrintWriter err = new PrintWriter(System.out, true);
    static private final PrintWriter out = new PrintWriter(System.out, true);
    static private final String prefix = "PersistenceLauncher.main() : ";//NOI18N

    /**
     * Creates new PersistenceLauncher.
     */
    private PersistenceLauncher() {
    }

    /**
     * Prints usage message.
     */
    static void usage()
    {
        out.flush();
        err.println("PersistenceLauncher:");
        err.println("    usage: <options> ... <target class name> <args> ...");
        err.println("    options:");
        err.println("           -h | --help");
        err.println("           -n | --noEnhancement");
        err.println("           -q | --quiet");
        err.println("           -w | --warn");
        err.println("           -d | --debug");
        err.println("           -t | --timing");
        err.println("    class names have to be fully qualified");
        err.println("done.");
        err.println();
        err.flush();
    }

    /**
     * Creates a class loader and launches a target class.
     * @param args the command line arguments
     */
    public static void main(String[] args)
        throws ClassNotFoundException,
        NoSuchMethodException,
        SecurityException,
        IllegalAccessException,
        IllegalArgumentException,
        InvocationTargetException {
/*
        message("property PersistenceExecutor.TAG_REPOSITORY = "
                + System.getProperty("PersistenceExecutor.TAG_REPOSITORY"));
        message("property PersistenceExecutor.TAG_CLASSPATH = "
                + System.getProperty("PersistenceExecutor.TAG_CLASSPATH"));
        message("property PersistenceExecutor.TAG_LIBRARY = "
                + System.getProperty("PersistenceExecutor.TAG_LIBRARY"));
        message("property PersistenceExecutor.TAG_CLASSNAME = "
                + System.getProperty("PersistenceExecutor.TAG_CLASSNAME"));
*/

        // get launcher options
        final String classpath = System.getProperty("java.class.path");
        boolean noEnhancement = false;
        boolean debug = false;
        boolean timing = false;
        Properties enhancerSettings = new Properties();
        String targetClassname = null;
        String[] targetClassArgs = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-h")//NOI18N
                || arg.equals("--help")) {//NOI18N
                usage();

                // exit gently
                return;
            }
            if (arg.equals("-n")//NOI18N
                || arg.equals("--noEnhancement")) {//NOI18N
                noEnhancement = false;
                continue;
            }
            if (arg.equals("-t")//NOI18N
                || arg.equals("--timing")) {//NOI18N
                timing = true;
                enhancerSettings.setProperty(EnhancerClassLoader.DO_SIMPLE_TIMING,
                                             "true");//NOI18N
                continue;
            }
            if (arg.equals("-d")//NOI18N
                || arg.equals("--debug")) {//NOI18N
                debug = true;
                enhancerSettings.setProperty(EnhancerClassLoader.VERBOSE_LEVEL,
                                             EnhancerClassLoader.VERBOSE_LEVEL_DEBUG);
                continue;
            }
            if (arg.equals("-w")//NOI18N
                || arg.equals("--warn")) {//NOI18N
                debug = false;
                enhancerSettings.setProperty(EnhancerClassLoader.VERBOSE_LEVEL,
                                             EnhancerClassLoader.VERBOSE_LEVEL_WARN);
                continue;
            }
            if (arg.equals("-q")//NOI18N
                || arg.equals("--quiet")) {//NOI18N
                debug = false;
                enhancerSettings.setProperty(EnhancerClassLoader.VERBOSE_LEVEL,
                                             EnhancerClassLoader.VERBOSE_LEVEL_QUIET);
                continue;
            }

            // get target class name
            targetClassname = arg;

            // copy remaining arguments and leave loop
            i++;
            final int length = args.length - i;
            targetClassArgs = new String[length];
            System.arraycopy(args, i, targetClassArgs, 0, length);
            break;
        }

        // debugging oputput
        if (debug) {
            out.println(prefix + "...");//NOI18N
            out.println("settings and arguments:");//NOI18N
            out.println("    classpath = " + classpath);//NOI18N
            out.println("    noEnhancement = " + noEnhancement);//NOI18N
            out.println("    debug = " + debug);//NOI18N
            out.println("    enhancerSettings = {");//NOI18N
            enhancerSettings.list(out);
            out.println("    }");//NOI18N
            out.println("    targetClassname = " + targetClassname);//NOI18N
            out.print("    targetClassArgs = { ");//NOI18N
            for (int i = 0; i < targetClassArgs.length; i++) {
                out.print(targetClassArgs[i] + " ");//NOI18N
            }
            out.println("}");//NOI18N
        }

        // check options
        if (targetClassname == null) {
            usage();
            throw new IllegalArgumentException("targetClassname == null");//NOI18N
        }

        // get class loader
        final ClassLoader loader;
        if (noEnhancement) {
            if (debug) {
                out.println(prefix + "using system class loader");//NOI18N
            }
            //out.println("using system class loader");
            loader = PersistenceLauncher.class.getClassLoader();
        } else {
            if (debug) {
                out.println(prefix + "creating enhancer class loader");//NOI18N
            }
            final Properties settings = enhancerSettings;
            final PrintWriter out = PersistenceLauncher.out;
            loader = new EnhancerClassLoader(classpath, settings, out);
        }

        // get target class' main method
        Class clazz;
        Method main;
        try {
            final String mname = "main";//NOI18N
            final Class[] mparams = new Class[]{ String[].class };
            final boolean init = true;
            if (debug) {
                out.println(prefix + "getting method "//NOI18N
                            + targetClassname + "." + mname + "(String[])");//NOI18N
            }
            clazz = Class.forName(targetClassname, init, loader);
            main = clazz.getDeclaredMethod(mname, mparams);
        } catch (ClassNotFoundException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        } catch (NoSuchMethodException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        } catch (SecurityException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        }

        // invoke target class' main method
        try {
            final Object[] margs = new Object[]{ targetClassArgs };
            if (debug) {
                out.println("invoking method " + clazz.getName()//NOI18N
                            + "." + main.getName() + "(String[])");//NOI18N
            }
            main.invoke(null, margs);
        } catch (IllegalAccessException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        } catch (IllegalArgumentException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        } catch (InvocationTargetException e) {
            // log exception only
            if (debug) {
                out.flush();
                err.println("PersistenceLauncher: EXCEPTION SEEN: " + e);
                e.printStackTrace(err);
                err.flush();
            }
            throw e;
        } finally {
            if (timing) {
                Support.timer.print();
            }
        }

        if (debug) {
            out.println(prefix + "done.");//NOI18N
            out.flush();
            err.flush();
        }
    }
}
