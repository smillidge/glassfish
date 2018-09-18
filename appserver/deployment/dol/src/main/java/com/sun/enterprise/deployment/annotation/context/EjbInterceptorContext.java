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

package com.sun.enterprise.deployment.annotation.context;

import com.sun.enterprise.deployment.EjbInterceptor;

import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

/**
 * This provides a context for a of interceptor.
 *
 * @author Shing Wai Chan
 */
public class EjbInterceptorContext extends ResourceContainerContextImpl {
    public EjbInterceptorContext(EjbInterceptor ejbInterceptor) {
        super(ejbInterceptor);
    }

    public EjbInterceptor getDescriptor() {
        return (EjbInterceptor)descriptor;
    }

    public void setDescriptor(EjbInterceptor ejbInterceptor) {
        descriptor = ejbInterceptor;
    }

    public void endElement(ElementType type, AnnotatedElement element) {
        
        if (ElementType.TYPE.equals(type)) {
            // done with processing this class, let's pop this context
            getProcessingContext().popHandler();
        }
    }
}
