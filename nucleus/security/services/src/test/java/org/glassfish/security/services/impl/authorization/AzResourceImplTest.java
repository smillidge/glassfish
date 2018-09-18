/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.security.services.impl.authorization;

import org.glassfish.security.services.api.common.Attribute;
import org.glassfish.security.services.api.common.Attributes;
import org.glassfish.security.services.impl.common.AttributesImpl;
import org.junit.Test;

import java.net.URI;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;

import static org.glassfish.security.services.impl.authorization.AzResourceImpl.addAttributesFromUriQuery;
import static org.glassfish.security.services.impl.authorization.AzResourceImpl.decodeURI;
import static junit.framework.Assert.*;

/**
 * @see AzResourceImpl
 */
public class AzResourceImplTest {
    @Test
    public void testGetters() throws Exception {
        URI uri;
        AzResourceImpl impl;

        // Null
        try {
            new AzResourceImpl(null);
            fail( "Expected exception" );
        } catch (IllegalArgumentException e) {
        }

        // Doesn't care about scheme
        uri = new URI( "http://foo" );
        impl = new AzResourceImpl(uri);
        assertSame( "non-admin OK", uri, impl.getUri() );

        // Empty domain (i.e. default)
        uri = new URI( "admin:///tenants/tenant/zirka?locked=true%3D" );
        impl = new AzResourceImpl(uri);

        // Test getters
        assertEquals("URI", uri, impl.getUri());
        assertEquals("toString", "admin:///tenants/tenant/zirka?locked=true%3D", impl.toString());

        // Non-empty domain, empty path
        uri = new URI( "admin://myDomain?locked=true%3D" );
        impl = new AzResourceImpl(uri);

        // Test getters
        assertEquals("URI", uri, impl.getUri());
        assertEquals("toString", "admin://myDomain?locked=true%3D", impl.toString());
    }

    @Test
    public void testAddAttributesFromUriQuery() throws Exception {
        URI uri = new URI( "admin:///tenants/tenant/zirka?locked=true" );
        Attributes attributes = new AttributesImpl();
        Attribute attribute;
        Set<String> values;
        Iterator<String> iter;
        BitSet bitset;

        final boolean REPLACE = true;

        // Null
        try {
            addAttributesFromUriQuery( null, attributes, REPLACE );
            fail( "Expected IllegalArgumentException from null URI." );
        } catch (IllegalArgumentException e) {
        }
        try {
            addAttributesFromUriQuery( uri, null, REPLACE );
            fail( "Expected IllegalArgumentException from null Attributes." );
        } catch (IllegalArgumentException e) {
        }

        assertEquals( "Empty attributes", 0, attributes.getAttributeCount() );

        // No params
        uri = new URI( "admin:///tenants/tenant/zirka" );
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals( "Empty attributes", 0, attributes.getAttributeCount() );

        // 1 param
        uri = new URI( "admin:///tenants/tenant/zirka?name1=value1" );
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals("Attributes count", 1, attributes.getAttributeCount());
        assertNotNull("attribute", attribute = attributes.getAttribute("name1"));
        values = attribute.getValues();
        assertEquals("Values count", 1, values.size());
        iter = values.iterator();
        assertTrue( iter.hasNext() );
        assertEquals("Values value", "value1", iter.next());
        assertFalse( iter.hasNext() );

        // Repeat, no dup value
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals("Attributes count", 1, attributes.getAttributeCount());
        assertNotNull("attribute", attribute = attributes.getAttribute("name1"));
        values = attribute.getValues();
        assertEquals("Values count", 1, values.size());
        iter = values.iterator();
        assertTrue( "iterator", iter.hasNext() );
        assertEquals("Values value", "value1", iter.next());
        assertFalse("iterator", iter.hasNext());

        // New value
        uri = new URI( "admin:///tenants/tenant/boris?name1=value2" );
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals("Attributes count", 1, attributes.getAttributeCount());
        assertNotNull("attribute", attribute = attributes.getAttribute("name1"));
        values = attribute.getValues();
        assertEquals("Values count", 2, values.size());
        bitset = new BitSet(2);
        for ( String v : values ) {
            if ( "value1".equals(v) && !bitset.get(0) ) {
                bitset.set(0);
            } else if ( "value2".equals(v) && !bitset.get(1) ) {
                bitset.set(1);
            } else {
                fail( "Unexpected attribute value " + v );
            }
        }

        // Replace attribute
        uri = new URI( "admin:///tenants/tenant/lucky?name1=value3" );
        addAttributesFromUriQuery( uri, attributes, REPLACE );
        assertEquals("Attributes count", 1, attributes.getAttributeCount());
        assertNotNull("attribute", attribute = attributes.getAttribute("name1"));
        values = attribute.getValues();
        assertEquals("Values count", 1, values.size());
        iter = values.iterator();
        assertTrue( "iterator", iter.hasNext() );
        assertEquals("Values value", "value3", iter.next());
        assertFalse("iterator", iter.hasNext());

        // New attribute
        uri = new URI( "admin:///tenants/tenant/lucky?name2=value21&name2=value22" );
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals("Attributes count", 2, attributes.getAttributeCount());
        assertNotNull("attribute", attributes.getAttribute("name1"));
        assertNotNull("attribute", attribute = attributes.getAttribute("name2"));
        values = attribute.getValues();
        assertEquals("Values count", 2, values.size());
        bitset = new BitSet(2);
        for ( String v : values ) {
            if ( "value21".equals(v) && !bitset.get(0) ) {
                bitset.set(0);
            } else if ( "value22".equals(v) && !bitset.get(1) ) {
                bitset.set(1);
            } else {
                fail( "Unexpected attribute value " + v );
            }
        }

        // Encoded attribute
        attributes = new AttributesImpl();
        uri = new URI( "admin:///tenants/tenant/lucky?na%3Dme2=val%26ue1&na%3Dme2=val%3Due2" );
        addAttributesFromUriQuery( uri, attributes, !REPLACE );
        assertEquals("Attributes count", 1, attributes.getAttributeCount());
        assertNotNull("attribute", attribute = attributes.getAttribute("na=me2"));
        values = attribute.getValues();
        assertEquals("Values count", 2, values.size());
        bitset = new BitSet(2);
        for ( String v : values ) {
            if ( "val&ue1".equals(v) && !bitset.get(0) ) {
                bitset.set(0);
            } else if ( "val=ue2".equals(v) && !bitset.get(1) ) {
                bitset.set(1);
            } else {
                fail( "Unexpected attribute value " + v );
            }
        }
    }

    @Test
    public void testdecodeURI() throws Exception {
        assertNull( "Expected null", decodeURI(null) );

        assertEquals( "decoded",
            "#($^&#&(*$C@#$*&^@#*&(|}|}{|}dfaj;",
            decodeURI( "%23(%24%5E%26%23%26(*%24C%40%23%24*%26%5E%40%23*%26(%7C%7D%7C%7D%7B%7C%7Ddfaj%3B" ) );
    }
}
