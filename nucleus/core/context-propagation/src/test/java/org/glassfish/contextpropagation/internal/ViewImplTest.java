/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.contextpropagation.internal;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

//import mockit.Deencapsulation;

import org.glassfish.contextpropagation.PropagationMode;
//import org.glassfish.contextpropagation.adaptors.BootstrapUtils;
import org.glassfish.contextpropagation.internal.Entry.ContextType;
import org.glassfish.contextpropagation.wireadapters.glassfish.DefaultWireAdapter;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ViewImplTest {
//  static ViewImpl view;
//  static SimpleMap sm;
//  
//  @BeforeClass
//  public static void setupClass() {
//    BootstrapUtils.bootstrap(new DefaultWireAdapter());
//    view = new ViewImpl("prefix");
//    sm = new SimpleMap();
//    Deencapsulation.setField(view, "sMap", sm);
//    sm.put("prefix.removeMe", new Entry("removeMe", PropagationMode.defaultSet(), ContextType.STRING).init(true, true));
//    sm.put("prefix.getMe", new Entry("getMe", PropagationMode.defaultSet(), ContextType.STRING).init(true, true));
//    sm.put("prefix.string", new Entry("string", PropagationMode.defaultSet(), ContextType.STRING).init(true, true));
//    sm.put("prefix.asciiString", new Entry("asciistring", PropagationMode.defaultSet(), ContextType.ASCII_STRING).init(true, true));
//    sm.put("prefix.long", new Entry(1L, PropagationMode.defaultSet(), ContextType.LONG).init(true, true));
//    sm.put("prefix.boolean", new Entry(true, PropagationMode.defaultSet(), ContextType.BOOLEAN).init(true, true));
//    sm.put("prefix.char", new Entry('c', PropagationMode.defaultSet(), ContextType.CHAR).init(true, true));
//    sm.put("prefix.serializable", new Entry("serializable", PropagationMode.defaultSet(), ContextType.SERIALIZABLE).init(true, true));
//  }
//
//  @Test
//  public void testGet() {
//    assertEquals("getMe", view.get("getMe"));
//  }
//
//  @Test
//  public void testPutStringStringEnumSetOfPropagationModeBoolean() {
//    checkPut("string", "string", "new_string", ContextType.STRING);
//  }
//
//  <T> void checkPut(String key, Object origValue, Object newValue, ContextType contextType) {
//    assertEquals(origValue, Deencapsulation.invoke(view, "put", key, newValue, PropagationMode.defaultSet()));
//    assertEquals(newValue, sm.get("prefix." + key));
//    assertEquals(ContextType.STRING, sm.getEntry("prefix.string").getContextType());
//  }
//
//  @Test
//  public void testPutAscii() {
//    checkPut("asciiString", "asciistring", "new_asciistring", ContextType.ASCII_STRING);
//  }
//
//  @Test
//  public void testPutStringUEnumSetOfPropagationModeBoolean() {
//    checkPut("long", 1L, 2L, ContextType.LONG);
//  }
//
//  @Test
//  public void testPutStringBooleanEnumSetOfPropagationModeBoolean() {
//    checkPut("boolean", true, false, ContextType.BOOLEAN);
//  }
//
//  @Test
//  public void testPutStringCharacterEnumSetOfPropagationModeBoolean() {
//    checkPut("char", 'c', 'd', ContextType.CHAR);
//  }
//
//  @Test
//  public void testPutSerializable() {
//    checkPut("serializable", (Serializable) "serializable", (Serializable) "new_serializable", ContextType.SERIALIZABLE);
//  }
//
//  @Test
//  public void testRemove() {
//    assertEquals("removeMe", view.remove("removeMe"));
//  }

}
