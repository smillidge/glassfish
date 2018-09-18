/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.javaee7.samples.batch.simple;

import javax.inject.Inject;


@javax.inject.Named//("com.oracle.javaee7.samples.batch.simple.SimpleItemProcessor")
public class SimpleItemProcessor
    implements javax.batch.api.ItemProcessor<String, String> {

//    @Inject
//    IdGenerator idGen;
    
    @Override
    public String processItem(String t) throws Exception {
        String[] record = t.split(", ");
        
    //EMP-ID, MONTH-YEAR, SALARY, TAX%, MEDICARE%, OTHER
        int salary = Integer.valueOf(record[2]);
        double tax = Double.valueOf(record[3]);
        double mediCare = Double.valueOf(record[4]);
        StringBuilder sb = new StringBuilder(t);
        sb.append(", ").append(salary * tax / 100);
        sb.append(", ").append(salary * mediCare / 100);
        sb.append(", ").append(salary - (salary * tax / 100) - (salary * mediCare / 100));
        return  sb.toString();
    }
    
}
