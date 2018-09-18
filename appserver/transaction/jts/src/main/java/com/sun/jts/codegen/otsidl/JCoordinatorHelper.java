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

package com.sun.jts.codegen.otsidl;


/**
* com/sun/jts/codegen/otsidl/JCoordinatorHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from com/sun/jts/ots.idl
* Tuesday, February 5, 2002 12:57:23 PM PST
*/


//#-----------------------------------------------------------------------------
abstract public class JCoordinatorHelper
{
  private static String  _id = "IDL:otsidl/JCoordinator:1.0";

  public static void insert (org.omg.CORBA.Any a, com.sun.jts.codegen.otsidl.JCoordinator that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static com.sun.jts.codegen.otsidl.JCoordinator extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (com.sun.jts.codegen.otsidl.JCoordinatorHelper.id (), "JCoordinator");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static com.sun.jts.codegen.otsidl.JCoordinator read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_JCoordinatorStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.jts.codegen.otsidl.JCoordinator value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static com.sun.jts.codegen.otsidl.JCoordinator narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof com.sun.jts.codegen.otsidl.JCoordinator)
      return (com.sun.jts.codegen.otsidl.JCoordinator)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      com.sun.jts.codegen.otsidl._JCoordinatorStub stub = new com.sun.jts.codegen.otsidl._JCoordinatorStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
