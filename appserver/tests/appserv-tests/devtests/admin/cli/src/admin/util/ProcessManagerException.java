/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * ProcessManagerException.java
 * Any errors in ProcessManager will cause this to be thrown
 * @since JDK 1.4
 * @author bnevins
 * Created on October 28, 2005, 10:08 PM
 */
package admin.util;

public class ProcessManagerException extends Exception {

    public ProcessManagerException(Throwable cause) {
        super(cause);
    }
    public ProcessManagerException(String message) {
        super(message);
    }
}
