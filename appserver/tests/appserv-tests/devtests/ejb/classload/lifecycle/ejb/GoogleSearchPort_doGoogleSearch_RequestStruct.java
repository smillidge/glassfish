/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1, build EA-R26)

package com.sun.s1asdev.ejb.classload.lifecycle;


public class GoogleSearchPort_doGoogleSearch_RequestStruct {
    protected java.lang.String key;
    protected java.lang.String q;
    protected int start;
    protected int maxResults;
    protected boolean filter;
    protected java.lang.String restrict;
    protected boolean safeSearch;
    protected java.lang.String lr;
    protected java.lang.String ie;
    protected java.lang.String oe;
    
    public GoogleSearchPort_doGoogleSearch_RequestStruct() {
    }
    
    public GoogleSearchPort_doGoogleSearch_RequestStruct(java.lang.String key, java.lang.String q, int start, int maxResults, boolean filter, java.lang.String restrict, boolean safeSearch, java.lang.String lr, java.lang.String ie, java.lang.String oe) {
        this.key = key;
        this.q = q;
        this.start = start;
        this.maxResults = maxResults;
        this.filter = filter;
        this.restrict = restrict;
        this.safeSearch = safeSearch;
        this.lr = lr;
        this.ie = ie;
        this.oe = oe;
    }
    
    public java.lang.String getKey() {
        return key;
    }
    
    public void setKey(java.lang.String key) {
        this.key = key;
    }
    
    public java.lang.String getQ() {
        return q;
    }
    
    public void setQ(java.lang.String q) {
        this.q = q;
    }
    
    public int getStart() {
        return start;
    }
    
    public void setStart(int start) {
        this.start = start;
    }
    
    public int getMaxResults() {
        return maxResults;
    }
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public boolean isFilter() {
        return filter;
    }
    
    public void setFilter(boolean filter) {
        this.filter = filter;
    }
    
    public java.lang.String getRestrict() {
        return restrict;
    }
    
    public void setRestrict(java.lang.String restrict) {
        this.restrict = restrict;
    }
    
    public boolean isSafeSearch() {
        return safeSearch;
    }
    
    public void setSafeSearch(boolean safeSearch) {
        this.safeSearch = safeSearch;
    }
    
    public java.lang.String getLr() {
        return lr;
    }
    
    public void setLr(java.lang.String lr) {
        this.lr = lr;
    }
    
    public java.lang.String getIe() {
        return ie;
    }
    
    public void setIe(java.lang.String ie) {
        this.ie = ie;
    }
    
    public java.lang.String getOe() {
        return oe;
    }
    
    public void setOe(java.lang.String oe) {
        this.oe = oe;
    }
}
