/*
 * Copyright (c) 2001, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.ejb.cmp.readonly.ejb;

import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface StudentHome extends EJBHome {

 /**
     * Gets a reference to the remote interface to the StudentBean bean.
     * @exception throws CreateException and RemoteException.
     *
     */
    public Student create(String studentId, String name)
        throws RemoteException, CreateException;

    /**
     * Gets a reference to the remote interface to the StudentBean object by Primary Key.
     * @exception throws FinderException and RemoteException.
     *
     */
 
    public Student findByPrimaryKey(String studentId) 
        throws FinderException, RemoteException;

    public java.util.Collection findFoo() throws FinderException, RemoteException;
    public Student findBar(String s) throws FinderException, RemoteException;

    public Student findByRemoteStudent(Student student) throws FinderException, RemoteException;

    // only to be called on read-only StudentHome
    public void testLocalCreate(String pk) throws RemoteException;
    public void testLocalRemove(String pk) throws RemoteException;
    public void testLocalFind(String pk) throws RemoteException;
    public void testFind(String pk) throws RemoteException;
}
