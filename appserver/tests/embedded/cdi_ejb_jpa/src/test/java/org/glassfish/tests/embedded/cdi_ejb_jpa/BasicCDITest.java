/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tests.embedded.cdi_ejb_jpa;

import junit.framework.Assert;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;
import org.glassfish.embeddable.archive.ScatteredEnterpriseArchive;
import org.glassfish.embeddable.web.HttpListener;
import org.glassfish.embeddable.web.WebContainer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author bhavanishankar@dev.java.net
 */

public class BasicCDITest{

    @Test
    public void test() throws Exception {

        GlassFishProperties props = new GlassFishProperties();
        props.setPort("http-listener", 8080);
        GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(props);
        glassfish.start();

        // Test Scattered Web Archive
        ScatteredArchive sa = new ScatteredArchive("cdi_ejb_jpa",
                ScatteredArchive.Type.WAR, new File("src/main/webapp"));
        sa.addClassPath(new File("target/classes"));
        sa.addClassPath(new File("src/main/resources"));
        URI warURI = sa.toURI();
        printContents(warURI);

        // Deploy archive
        Deployer deployer = glassfish.getDeployer();
        String appname = deployer.deploy(warURI);
        System.out.println("Deployed [" + appname + "]");
        Assert.assertEquals(appname, "cdi_ejb_jpa");

        // Now create a http listener and access the app.
        WebContainer webcontainer = glassfish.getService(WebContainer.class);
        HttpListener listener = new HttpListener();
        listener.setId("my-listener");
        listener.setPort(9090);
        webcontainer.addWebListener(listener);

        get("http://localhost:8080/cdi_ejb_jpa/BasicCDITestServlet",
                "All CDI beans have been injected.");

        deployer.undeploy(appname);

        glassfish.dispose();

    }

    private void get(String urlStr, String result) throws Exception {
        URL url = new URL(urlStr);
        URLConnection yc = url.openConnection();
        System.out.println("\nURLConnection [" + yc + "] : ");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String line = null;
        boolean found = false;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            if (line.indexOf(result) != -1) {
                found = true;
            }
        }
        Assert.assertTrue(found);
        System.out.println("\n***** SUCCESS **** Found [" + result + "] in the response.*****\n");
    }

    void printContents(URI jarURI) throws IOException {
        JarFile jarfile = new JarFile(new File(jarURI));
        System.out.println("\n\n[" + jarURI + "] contents : \n");
        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            System.out.println(entry.getSize() + "\t" + new Date(entry.getTime()) +
                    "\t" + entry.getName());
        }
        System.out.println();
    }
}
