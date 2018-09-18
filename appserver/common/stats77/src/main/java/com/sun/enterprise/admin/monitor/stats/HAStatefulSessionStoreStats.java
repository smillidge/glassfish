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

package com.sun.enterprise.admin.monitor.stats;
import org.glassfish.j2ee.statistics.CountStatistic;
import org.glassfish.j2ee.statistics.TimeStatistic;
import com.sun.enterprise.admin.monitor.stats.AverageRangeStatistic;

/**
 *
 * @since 8.1
 */
public interface HAStatefulSessionStoreStats extends StatefulSessionStoreStats {
    
    /**
     * Returns the total number of sessions checkpointed using this store
     * @return CountStatistic
     */
    public CountStatistic getCheckpointCount();
    
    /**
     * Returns the total number of sessions successfully checkpointed using
     * this store
     * @return CountStatistic
     */
    public CountStatistic getCheckpointSuccessCount();
    
    /**
     * Returns the total number of sessions that could not be checkpointed
     * using this store
     * @return CountStatistic
     */
    public CountStatistic getCheckpointErrorCount();
    
    /**
     * Returns the total number of bytes checkpointed by the store
     * @return AverageRangeStatistic
     */
    public AverageRangeStatistic getCheckpointedBeanSize();
    
    /**
     * Returns the time spent on checkpointing beans to the store
     * @return AverageRangeStatistic
     */
    public AverageRangeStatistic getCheckpointTime();
    
}
