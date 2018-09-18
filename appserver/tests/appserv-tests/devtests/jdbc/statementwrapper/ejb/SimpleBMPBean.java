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

package com.sun.s1asdev.jdbc.statementwrapper.ejb;

import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;
import java.sql.*;


public class SimpleBMPBean
    implements EntityBean{

    protected DataSource ds;

    public void setEntityContext(EntityContext entityContext) {
	Context context = null;
	try {
	    context    = new InitialContext();
	    ds = (DataSource) context.lookup("java:comp/env/DataSource");
	} catch (NamingException e) {
	    throw new EJBException("cant find datasource");
	}
        System.out.println("[**SimpleBMPBean**] Done with setEntityContext....");
    }

    public Integer ejbCreate() throws CreateException {
	    return new Integer(1);
    }

    public boolean statementTest() {
        boolean result = false;
        Connection conFromDS = null;
        Connection conFromStatement = null;
        Statement stmt = null;
        try{
            conFromDS = ds.getConnection();
            stmt = conFromDS.createStatement();
            conFromStatement = stmt.getConnection();

		System.out.println("statement Test : conFromDS : " + conFromDS);
		System.out.println("statement Test : conFromStatement : " + conFromStatement);

            if( conFromDS==conFromStatement || conFromDS.equals(conFromStatement) ){
                result = true;
            }

        }catch(SQLException sqe){}finally{
            try{
                if(stmt != null){
                    stmt.close();
                }
            }catch(SQLException sqe){}

            try{
                if(conFromDS != null){
                    conFromDS.close();
                }
            }catch(SQLException sqe){}
        }
        return result;
   }
    public boolean preparedStatementTest(){
        boolean result = false;
        Connection conFromDS = null;
        Connection conFromStatement = null;
        PreparedStatement stmt = null;
        try{
            conFromDS = ds.getConnection();
            stmt = conFromDS.prepareStatement("select * from customer_stmt_wrapper");
            conFromStatement = stmt.getConnection();

		System.out.println("Prepared statement Test : conFromDS : " + conFromDS);
		System.out.println("Prepared statement Test : conFromStatement : " + conFromStatement);
            if( conFromDS==conFromStatement || conFromDS.equals(conFromStatement) ){
                result = true;
            }

        }catch(SQLException sqe){}finally{
            try{
                if(stmt != null){
                    stmt.close();
                }
            }catch(SQLException sqe){}

            try{
                if(conFromDS != null){
                    conFromDS.close();
                }
            }catch(SQLException sqe){}
        }
        return result;
    }
    public boolean callableStatementTest(){
        boolean result = false;
        Connection conFromDS = null;
        Connection conFromStatement = null;
        CallableStatement stmt = null;
        try{
            conFromDS = ds.getConnection();
            stmt = conFromDS.prepareCall("select * from customer_stmt_wrapper");
            conFromStatement = stmt.getConnection();

		System.out.println("Callable statement Test : conFromDS : " + conFromDS);
		System.out.println("Callable statement Test : conFromStatement : " + conFromStatement);
            if( conFromDS==conFromStatement || conFromDS.equals(conFromStatement) ){
                result = true;
            }

        }catch(SQLException sqe){}finally{
            try{
                if(stmt != null){
                    stmt.close();
                }
            }catch(SQLException sqe){}

            try{
                if(conFromDS != null){
                    conFromDS.close();
                }
            }catch(SQLException sqe){}
        }
        return result;
    }
    public boolean metaDataTest(){
     boolean result = false;
        Connection conFromDS = null;
        Connection conFromMetaData = null;
        DatabaseMetaData dbmd = null;
        try{
            conFromDS = ds.getConnection();
            dbmd = conFromDS.getMetaData();
            conFromMetaData = dbmd.getConnection();

		System.out.println("statementTest : conFromDS : " + conFromDS);
		System.out.println("statementTest : conFromDbMetadata : " + conFromMetaData);
            if( conFromDS==conFromMetaData || conFromDS.equals(conFromMetaData) ){
                result = true;
            }

        }catch(SQLException sqe){}finally{
            try{
                if(conFromDS != null){
                    conFromDS.close();
                }
            }catch(SQLException sqe){}
        }
        return result;
    }
    public boolean resultSetTest(){
        boolean result = false;
        Connection conFromDS = null;
        Connection conFromResultSet = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            conFromDS = ds.getConnection();
            stmt = conFromDS.createStatement();
            rs = stmt.executeQuery("select * from customer_stmt_wrapper");
            conFromResultSet = rs.getStatement().getConnection();

		System.out.println("ResultSet test : conFromDS : " + conFromDS);
		System.out.println("ResultSet test : conFromResultSet: " + conFromResultSet);
            if( conFromDS==conFromResultSet || conFromDS.equals(conFromResultSet) ){
                result = true;
            }
        }catch(SQLException sqe){}finally{

            try{
                if(rs != null){
                    rs.close();
                }
            }catch(SQLException sqe){}

            try{
                if(stmt != null){
                    stmt.close();
                }
            }catch(SQLException sqe){}

            try{
                if(conFromDS != null){
                    conFromDS.close();
                }
            }catch(SQLException sqe){}
        }
        return result;
    }
    public void ejbLoad() {}
    public void ejbStore() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void unsetEntityContext() {}
    public void ejbPostCreate() {}
}
