/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.ejb.deployment.descriptor;

import java.lang.Exception;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import com.sun.enterprise.deployment.WritableJndiNameEnvironment;

public class EjbDescriptorInheritedMethodImplementationTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */

    public EjbDescriptorInheritedMethodImplementationTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested.
     */
    public static Test suite() throws Exception {
        return new TestSuite(EjbDescriptorInheritedMethodImplementationTest.class);
    }


    /**
     * This method tests if methods inherited from WritableJndiNameEnvironment are
     * directly implemented in EjbDescriptor or not and if implemented, methods
     * are marked final or not.
     */
    public void testEjbDescriptorInheritedMethodImplementation() {

        Map<Error, List<Method>> unimplementedMethods = new HashMap<Error, List<Method>>();

        validateWritableJndiNameEnvInterfaceImplementation();

        List<Method> methodsDefinedByWritableJndiNameEnvInterface = Arrays
                .asList(WritableJndiNameEnvironment.class.getMethods());

        for (Method writableJndiNameEnvMethod : methodsDefinedByWritableJndiNameEnvInterface) {
            try {
                Method ejbDescriptorMethod = EjbDescriptor.class.getDeclaredMethod(
                        writableJndiNameEnvMethod.getName(),
                        writableJndiNameEnvMethod.getParameterTypes());
                if (!Modifier.isFinal(ejbDescriptorMethod.getModifiers())) {
                    updateUnimplementedMethodsMap(Error.NON_FINAL_METHOD, ejbDescriptorMethod, unimplementedMethods);
                }
            } catch (NoSuchMethodException e) {
                updateUnimplementedMethodsMap(Error.UNIMPLEMENTED_METHOD, writableJndiNameEnvMethod, unimplementedMethods);
            }
        }

        assertTrue(getErrorMessage(unimplementedMethods),
                unimplementedMethods.size() == 0);

    }

    /**
     * This method validates if EjbDescriptor abstract class implements
     * WritableJndiNameEnviroment interface or not. EjbDescriptor abstract
     * class implements WritableJndiNameEnvironment by way of implementing
     * EjbDescriptor interface, which in-turn is implementing
     * WritableJndiNameEnvironment interface. This test ensures that right
     * interfaces have been implemented by EjbDescriptor abstract class.
     */
    private void validateWritableJndiNameEnvInterfaceImplementation() {

        boolean doesEjbDescriptorClassImplementsEjbDescriptorInterface = validateInterfaceImplementation(
                EjbDescriptor.class,
                com.sun.enterprise.deployment.EjbDescriptor.class);

        assertTrue(
                "Abstract class org.glassfish.ejb.deployment.descriptor.EjbDescriptor "
                        + "doesn't implement com.sun.enterprise.deployment.EjbDescriptor interface.",
                doesEjbDescriptorClassImplementsEjbDescriptorInterface);

        boolean doesEjbDescriptorInterfaceImplementsJndiNameEnvInterface = validateInterfaceImplementation(
                com.sun.enterprise.deployment.EjbDescriptor.class,
                WritableJndiNameEnvironment.class);

        assertTrue(
                "Abstract class org.glassfish.ejb.deployment.descriptor.EjbDescriptor "
                        + "doesn't implement com.sun.enterprise.deployment.EjbDescriptor interface.",
                doesEjbDescriptorInterfaceImplementsJndiNameEnvInterface);
    }

    private boolean validateInterfaceImplementation(Class<?> interface1,
                                                    Class<?> interface2) {
        Class<?>[] interfaces = interface1.getInterfaces();
        for (Class<?> interface1interface : interfaces) {
            if (interface1interface == interface2)
                return true;
        }
        return false;
    }

    private String getErrorMessage(Map<Error, List<Method>> unimplementedMethods) {
        StringBuilder sb = new StringBuilder();
        for (Error error : unimplementedMethods.keySet()) {
            sb.append("\n" + error.getErrorMsg() + "\n");
            sb.append("\t");
            for (Method method : unimplementedMethods.get(error)) {
                sb.append(method);
                sb.append("\n\t");
            }
        }
        return sb.toString();
    }

    private void updateUnimplementedMethodsMap(Error error, Method method, Map<Error,
            List<Method>> unimplementedMethods) {
        if (unimplementedMethods.containsKey(error)) {
            List<Method> methods = unimplementedMethods.get(error);
            methods.add(method);
        } else {
            List<Method> methods = new ArrayList<Method>();
            methods.add(method);
            unimplementedMethods.put(error, methods);
        }
    }

    private enum Error {

        NON_FINAL_METHOD("Following com.sun.enterprise.deployment.WritableJndiNameEnvironment" +
                " methods are not marked final when implemented in " +
                "org.glassfish.ejb.deployment.descriptor.EjbDescriptor." +
                "None of the sub-classes of EjbDescriptor are expected to " +
                "override these methods as it might lead to change in intended behavior " +
                "and hence these methods must be marked final in EjbDescriptor."),

        UNIMPLEMENTED_METHOD("Following com.sun.enterprise.deployment.WritableJndiNameEnvironment " +
                "methods are not implemented directly in " +
                "org.glassfish.ejb.deployment.descriptor.EjbDescriptor. " +
                "Implementation of these methods is mandatory within " +
                "EjbDescriptor to ensure expected behavior when any of these" +
                " methods are invoked in EjbDescriptor's context.");

        String error;

        Error(String error) {
            this.error = error;
        }

        public String getErrorMsg() {
            return error;
        }
    }
}
