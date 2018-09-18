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

package com.sun.s1peqe.transaction.txhung.ejb.test;

import javax.ejb.*;
import javax.naming.*;
import javax.ejb.SessionContext;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Enumeration;
import java.rmi.RemoteException;
import java.util.*;


public class TestBean implements SessionBean, SessionSynchronization {
    private String user = null;
    private String dbURL1XA = null;
    private String dbURL1NonXA = null;
    private String resource = null;
    private String password = null;
    private SessionContext ctx = null;



    // SessionBean methods
 
    public void ejbCreate() throws CreateException {
	System.out.println("TestBean ejbCreate");

    }    
 
    public void ejbActivate() {
        System.out.println("TestBean ejbActivate");
    }    

    public void ejbPassivate() {
    }

    public void ejbRemove() {

    }
    
    public void setSessionContext(SessionContext sc) {
        System.out.println("setSessionContext in BeanB");
        try {
            ctx = sc;
            Context ic = new InitialContext();
            user = (String) ic.lookup("java:comp/env/user");
            password = (String) ic.lookup("java:comp/env/password");
            dbURL1XA = (String) ic.lookup("java:comp/env/dbURL1-XA");
	    dbURL1NonXA = (String) ic.lookup("java:comp/env/dbURL1-NonXA");
        } catch (Exception ex) {
            System.out.println("Exception in setSessionContext: " +ex.getMessage());
            ex.printStackTrace();
        }


    }


    public boolean testA1(boolean xa) throws CreateException {
	if(xa)
	resource = dbURL1XA;
        else
	resource = dbURL1NonXA;

        System.out.println("Executing the business method testA1");
	return true;

    }


    public void beforeCompletion() {
        System.out.println("in beforeCompletion");
    	Connection con1 = null;
        System.out.println("insert in BeanB");
        try {
            con1 = getConnection(resource);
            Statement stmt1 = con1.createStatement();
	    String acc = "100";
            float bal = 5000;
            stmt1.executeUpdate("INSERT INTO txAccount VALUES ('" + acc + "', " + bal + ")");
            System.out.println("Account added Successfully in "+resource+"...");
            System.out.println("Rolling back the transaction");
	    ctx.setRollbackOnly();
            //return true;
        } catch (Exception ex) {
            System.out.println("Exception in insert: " + ex.toString());
            ex.printStackTrace();
	    //return false;
        } finally {
            try {
                con1.close();
            } catch (java.sql.SQLException ex) {
            }
        }
    }

    public void afterBegin() {
        System.out.println("in afterBegin");
    }

    public void afterCompletion(boolean committed) {
        System.out.println("in afterCompletion");
    }

    private Connection getConnection(String dbURL) {
        Connection con = null;
        System.out.println("getConnection in BeanB");
        try{
            Context context = new InitialContext();
            DataSource ds = (DataSource) context.lookup(dbURL);
            con = ds.getConnection(user, password);
            System.out.println("Got DB Connection Successfully...");
        } catch (Exception ex) {
            System.out.println("Exception in getConnection: " + ex.toString());
            ex.printStackTrace();
        }
        return con;
    }



} 
