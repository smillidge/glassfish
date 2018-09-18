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

package com.sun.enterprise.security.jauth.callback;

import javax.security.auth.callback.Callback;
import java.util.Arrays;

/**
 * Callback for PasswordValidation.
 *
 * @version %I%, %G%
 */
public class PasswordValidationCallback extends
        javax.security.auth.message.callback.PasswordValidationCallback {

    /**
     * Create a PasswordValidationCallback.
     *
     * @param username the username to authenticate
     *
     * @param password the user's password, which may be null.
     */
    public PasswordValidationCallback(String username, char[] password) {
        super(null, username, password);
    }
}
