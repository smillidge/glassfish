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

package com.sun.enterprise.deployment;

import com.sun.enterprise.deployment.interfaces.MailResourceIntf;
import com.sun.enterprise.deployment.util.DOLUtils;
import com.sun.enterprise.repository.ResourceProperty;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/** 
 * This class represents the configuration information of the JavaMail
 * Session object within Java EE.
 */
public class MailConfiguration implements Serializable {
    private static final String PROP_NAME_PREFIX_LEGACY = "mail-";
    private static final char PROP_NAME_DELIM_LEGACY = '-';
 
    private static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";
    private static final String MAIL_TRANSPORT_PROTOCOL =
                                                    "mail.transport.protocol";
    private static final String MAIL_HOST = "mail.host";
    private static final String MAIL_USER = "mail.user";
    private static final String MAIL_FROM = "mail.from";
    private static final String MAIL_DEBUG = "mail.debug";

    private static final String MAIL_PREFIX = "mail.";
    private static final String MAIL_SUFFIX_CLASS = ".class";
    private static final String MAIL_SUFFIX_HOST = ".host";
    private static final String MAIL_SUFFIX_USER = ".user";
    private static final char MAIL_DELIM = '.';

    /**
     * Mail resource attributes
     */
    private String description = "";
    private String jndiName = "";
    private boolean enabled = false;
    private String storeProtocol = null;
    private String storeProtocolClass = null;
    private String transportProtocol = null;
    private String transportProtocolClass = null;
    private String mailHost = null;
    private String username = null;
    private String mailFrom = null;
    private boolean debug = false;

    private Properties mailProperties = new Properties();

    static Logger _logger = DOLUtils.getDefaultLogger();
 
    /** 
     * Construct a specification of mail configuration with the given username,
     * Mail From Address and mail hostname. 
     * @param the username.
     * @param the from address.
     * @param the mail hostname.
     */
    public MailConfiguration(String username, 
			     String mailFrom, 
			     String mailHost) {
        // called from MailConfigurationNode, which is never used
	this.username = username;
	this.mailFrom = mailFrom;
	this.mailHost = mailHost;

        put(MAIL_FROM, mailFrom);
        put(MAIL_USER, username);
        put(MAIL_HOST, mailHost);
    }

    /** 
     * Construct a specification of mail configuration.
     */
    public MailConfiguration(MailResourceIntf mailRes) {
        // called from MailResourceDeployer
        try {
            loadMailResources(mailRes);
        } catch (Exception ce) {
            _logger.log(Level.INFO,"enterprise.deployment_mail_cfgexcp",ce);
        }
    }

    /** 
     * Load all configuration information from the mail resource node in
     * domain.xml for the JavaMail Session object within Java EE.
     */
    private void loadMailResources(MailResourceIntf mailResource)
                                throws Exception {

        if (mailResource == null) {
            _logger.log(Level.FINE,
                "MailConfiguration: no MailResource object. mailResource=null");
            return;
        }
 
        jndiName = mailResource.getName();
        description = mailResource.getDescription();
        enabled = mailResource.isEnabled();

        storeProtocol = mailResource.getStoreProtocol();
        storeProtocolClass = mailResource.getStoreProtocolClass();
        transportProtocol = mailResource.getTransportProtocol();
        transportProtocolClass = mailResource.getTransportProtocolClass();
        mailHost = mailResource.getMailHost();
        username = mailResource.getUsername();
        mailFrom = mailResource.getMailFrom();
        debug = mailResource.isDebug();
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("storeProtocol " + storeProtocol);
            _logger.fine("storeProtocolClass " + storeProtocolClass);
            _logger.fine("transportProtocol " + transportProtocol);
            _logger.fine("transportProtocolClass " + transportProtocolClass);
            _logger.fine("mailHost " + mailHost);
            _logger.fine("username " + username);
            _logger.fine("mailFrom " + mailFrom);
            _logger.fine("debug " + debug);
        }

        // JavaMail doesn't default this one properly
        if (transportProtocol == null)
            transportProtocol = "smtp";

        // Save to Property list
        put(MAIL_HOST, mailHost);
        put(MAIL_USER, username);
        put(MAIL_STORE_PROTOCOL, storeProtocol);
        put(MAIL_TRANSPORT_PROTOCOL, transportProtocol);
        if (storeProtocol != null)
            put(MAIL_PREFIX + storeProtocol + MAIL_SUFFIX_CLASS,
                                                        storeProtocolClass);
        if (transportProtocol != null)
            put(MAIL_PREFIX + transportProtocol + MAIL_SUFFIX_CLASS,
                                                        transportProtocolClass);
        put(MAIL_FROM, mailFrom);
        put(MAIL_DEBUG, (debug ? "true" : "false"));

        // Get the properties and save to Property list
        Set properties = mailResource.getProperties();

        for (Iterator it = properties.iterator(); it.hasNext();) {
            ResourceProperty property = (ResourceProperty)it.next();
            String name = property.getName();
            String value = (String)property.getValue();

            if (name.startsWith(PROP_NAME_PREFIX_LEGACY))
                name = name.replace(PROP_NAME_DELIM_LEGACY, MAIL_DELIM);

            put(name, value);
            if (_logger.isLoggable(Level.FINE))
                _logger.fine("mail property: " + name + " = " + value);
        }
    }

    /**
     * Set a mail property, if the value isn't null or empty.
     */
    private void put(String name, String value) {
        if (value != null && value.length() > 0)
            mailProperties.put(name, value);
    }
 
    // XXX - none of the following mail-specific accessor methods
    // seem to be used

    /** 
     * Get the username for the mail session the server will provide.
     * @return the username.
     */
    public String getUsername() {
	return this.username;
    }
    
    /** 
     * Get the mail from address for the mail session the server will provide.
     * @return the from address.
     */
    public String getMailFrom() {
	return this.mailFrom;
    }
    
    /** 
     * Get the mail hostname for the mail session the server will provide.
     * @return the hostname of the mail server.
     */
    public String getMailHost() {
	return this.mailHost;
    }

    /** 
     * Get the default Message Access Protocol for the mail session the  server
     * will provide.
     * @return the store protocol of the mail server.
     */
    public String getMailStoreProtocol() {
        return this.storeProtocol;
    }

    /** 
     * Get the default Transport Protocol for the mail session the server will
     * provide.
     * @return the transport protocol of the mail server.
     */
    public String getMailTransportProtocol() {
        return this.transportProtocol;
    }

    /** 
     * Get the default Message Access Protocol class for the mail session the
     * server will provide.
     * @return the store protocol of the mail server.
     */
    public String getMailStoreProtocolClass() {
        return this.storeProtocolClass;
    }

    /** 
     * Get the default Transport Protocol class for the mail session the server
     * will provide.
     * @return the transport protocol of the mail server.
     */
    public String getMailTransportProtocolClass() {
        return this.transportProtocolClass;
    }

    /** 
     * Get the mail debug flag for the mail session the server will provide.
     * @return the debug flag of the mail server.
     */
    public boolean getMailDebug() {
        return this.debug;
    }

    /** 
     * Get the mail description for the mail session the server will provide.
     * @return the description of the mail server.
     */
    public String getDescription() {
        return this.description;
    }

    /** 
     * Get the mail JNDI name for the mail session the server will provide.
     * @return the JNDI name of the mail server.
     */
    public String getJndiName() {
        return this.jndiName;
    }

    /** 
     * Get the mail enable flag for the mail session the server will provide.
     * @return the enable flag of the mail server.
     */
    public boolean getEnabled() {
        return this.enabled;
    }

    // This is the only method that's actually used, to create the Session.

    /** 
     * Get the mail session properties as per JavaMail.
     * @return the mail session properties.
     */
    public Properties getMailProperties() {
        return mailProperties;
    }
 
    /** 
     * A formatted representation of my state.
     */
    public void print(StringBuffer toStringBuffer) {
        toStringBuffer.append("MailConfiguration: [");
        toStringBuffer.append("description=").append(description);
        toStringBuffer.append( ", jndiName=").append(jndiName);
        toStringBuffer.append( ", enabled=").append(enabled);

        toStringBuffer.append( ", storeProtocol=").append(storeProtocol);
        toStringBuffer.append( ", transportProtocol=").
                                                append(transportProtocol);
        toStringBuffer.append( ", storeProtocolClass=").
                                                append(storeProtocolClass);
        toStringBuffer.append( ", transportProtocolClass=").
                                                append(transportProtocolClass);
        toStringBuffer.append( ", mailHost=").append(mailHost);
        toStringBuffer.append( ", username=").append(username);
        toStringBuffer.append( ", mailFrom=").append(mailFrom);
        toStringBuffer.append( ", debug=").append(debug);
        toStringBuffer.append( ", mailProperties: [");

        Enumeration e = mailProperties.propertyNames();
        String name;
        String value;
        boolean isFirst = true;

        while (e.hasMoreElements()) {
            name = (String)e.nextElement();
            value = mailProperties.getProperty(name);
            if (!isFirst)
                toStringBuffer.append( ", ");
            toStringBuffer.append(name).append("=").append(value);
            isFirst = false;
        }
        toStringBuffer.append( "]]");
    }
}
