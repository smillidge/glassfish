/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package create;

import javax.ejb.*;
import java.util.*;

/**
 * @author mvatkina
 */

public interface A1UnPKHome extends javax.ejb.EJBHome {
    
    public  java.util.Collection findAll()  throws java.rmi.RemoteException, javax.ejb.FinderException;
    
    public  A1 create(java.lang.String name) throws java.rmi.RemoteException, javax.ejb.CreateException;
    
    public  A1 create(int i) throws java.rmi.RemoteException, javax.ejb.CreateException;
    
}
