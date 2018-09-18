/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.security.services.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import org.glassfish.api.admin.PasswordAliasStore;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author tjquinn
 */
public class PasswordAliasTest {
    
    private static final char[] TEST_STORE_PW = "dontChangeIt".toCharArray();
    
    private static File storeFile;
    private static PasswordAliasStore store;
    
    @BeforeClass
    public static void createStore() throws Exception {
        storeFile = File.createTempFile("pwAliasStore", "jks");
        final KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null, TEST_STORE_PW);
        final OutputStream os = new BufferedOutputStream(new FileOutputStream(storeFile));
        ks.store(os, TEST_STORE_PW);
        os.close();
        
        store = JCEKSPasswordAliasStore.newInstance(storeFile.getAbsolutePath(), TEST_STORE_PW);
        
        System.out.println("Created temporary store " + storeFile.getAbsolutePath());
    }
    
    @AfterClass
    public static void deleteStore() throws Exception {
        if ( ! storeFile.delete()) {
            throw new IOException("Error cleaning up test alias store file " + storeFile.getAbsolutePath());
        }
    }
    
    @Test
    public void checkShortPW() {
        checkStoreAndGetPWByAlias("aliasFoo", "123456");
    }
    
    @Test
    public void checkLongPW() {
        checkStoreAndGetPWByAlias("aliasBar", "12345678901234567890");
    }
    
    private void checkStoreAndGetPWByAlias(final String alias, final String pw) {
        store.put(alias, pw.toCharArray());
        final String retrievedPW = new String(store.get(alias));
        Assert.assertEquals("Retrieved password failed to match stored password", 
                getBytesInHex(pw.getBytes()), 
                getBytesInHex(retrievedPW.getBytes()));
    }
    
    private static String getBytesInHex(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
             sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
