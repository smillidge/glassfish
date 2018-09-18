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

package org.glassfish.nucleus.admin.progress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import static org.glassfish.tests.utils.NucleusTestUtils.*;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 *
 * @author martinmares
 */
@Test(testName="DetachAttachTest")
public class DetachAttachTest {
    private static File nucleusRoot  = getNucleusRoot();

    @AfterTest
    public void cleanUp() throws Exception {
        nadmin("stop-domain");
        JobManagerTest.deleteJobsFile();
        //osgi-cache workaround
        File osgiCacheDir = new File(nucleusRoot, "domains"+File.separator+"domain1"+File.separator+"osgi-cache");
        deleteDirectoryContents(osgiCacheDir);
        nadmin("start-domain");
    }

    // Test disabled till intermittent failures are fixed
    @Test(enabled = false)
    public void uptimePeriodically() throws InterruptedException {
        Set<String> ids = new HashSet<String>();
        for (int i = 0; i < 3; i++) {
            System.out.println("detachAndAttachUptimePeriodically(): round " + i);
            NadminReturn result = nadminWithOutput("--detach", "--terse", "uptime");
            assertTrue(result.returnValue);
            String id = parseJobIdFromEchoTerse(result.out);
            assertTrue(ids.add(id)); //Must be unique
            Thread.sleep(1000L);
            //Now attach
            result = nadminWithOutput("--terse", "attach", id);
            assertTrue(result.returnValue);
            assertTrue(result.out.contains("uptime"));
        }
    }
    
    public void commandWithProgressStatus() throws InterruptedException {
        NadminReturn result = nadminWithOutput("--detach", "--terse", "progress-custom", "6x1");
        assertTrue(result.returnValue);
        String id = parseJobIdFromEchoTerse(result.out);
        Thread.sleep(2000L);
        //Now attach running
        result = nadminWithOutput("attach", id);
        assertTrue(result.returnValue);
        assertTrue(result.out.contains("progress-custom"));
        List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
        assertFalse(prgs.isEmpty());
        assertTrue(prgs.get(0).getValue() > 0);
        assertEquals(100, prgs.get(prgs.size() - 1).getValue());
        //Now attach finished - must NOT exists - seen progress job is removed
        result = nadminWithOutput("attach", id);
        assertFalse(result.returnValue);
    }
    
    public void detachOnesAttachMulti() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
                                            @Override
                                            public Thread newThread(Runnable r) {
                                                Thread result = new Thread(r);
                                                result.setDaemon(true);
                                                return result;
                                            }
                                        });
        //Execute command with progress status suport
        NadminReturn result = nadminWithOutput("--detach", "--terse", "progress-custom", "8x1");
        assertTrue(result.returnValue);
        final String id = parseJobIdFromEchoTerse(result.out);
        Thread.sleep(1500L);
        //Now attach
        final int _attach_count = 3;
        Collection<Callable<NadminReturn>> attaches = new ArrayList<Callable<NadminReturn>>(_attach_count);
        for (int i = 0; i < _attach_count; i++) {
            attaches.add(new Callable<NadminReturn>() {
                    @Override
                    public NadminReturn call() throws Exception {
                        return nadminWithOutput("attach", id);
                    }
                });
        }
        List<Future<NadminReturn>> results = pool.invokeAll(attaches);
        //Test results
        for (Future<NadminReturn> fRes : results) {
            NadminReturn res = fRes.get();
            assertTrue(res.returnValue);
            assertTrue(res.out.contains("progress-custom"));
            List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(res.out);
            assertFalse(prgs.isEmpty());
            assertTrue(prgs.get(0).getValue() > 0);
            assertEquals(100, prgs.get(prgs.size() - 1).getValue());
        }
    }
    
    private String parseJobIdFromEchoTerse(String str) {
        StringTokenizer stok = new StringTokenizer(str, "\n\r");
        assertTrue(stok.hasMoreTokens());
        stok.nextToken();
        //Id is second non empty line
        assertTrue(stok.hasMoreTokens());
        String result = stok.nextToken().trim();
        assertFalse(result.isEmpty());
        assertFalse(result.contains(" ")); //With space does not look like ID but like some error message
        return result;
    }
    
    static class NadminCallable implements Callable<NadminReturn> {
        
        private final String[] args;

        public NadminCallable(String... args) {
            this.args = args;
        }

        @Override
        public NadminReturn call() throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        
    
    }
    
}
