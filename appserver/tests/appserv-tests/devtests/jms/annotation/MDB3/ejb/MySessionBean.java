/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.test.jms.annotation.ejb;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSProducer;
import javax.jms.JMSSessionMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@JMSDestinationDefinition(
    description = "global-scope result queue defined by @JMSDestinationDefinition",
    name = "java:global/env/annotation_resultQueue",
    interfaceName = "javax.jms.Queue",
    resourceAdapter = "jmsra",
    destinationName = "myPhysicalResultQueue"
)

@Stateless(mappedName="MySessionBean/remote")
public class MySessionBean implements MySessionBeanRemote {
    @Resource(mappedName = "java:global/env/annotation_testQueue")
    private Queue testQueue;

    @Resource(mappedName = "java:global/env/annotation_resultQueue")
    private Queue resultQueue;

    @Resource(mappedName = "java:global/env/annotation_CF")
    private ConnectionFactory myConnectionFactory;

    @Inject
    @JMSConnectionFactory("java:global/env/annotation_CF")
    @JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext jmsContext;

    @Override
    public void sendMessage(String text) {
        try {
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage(text);
            producer.send(testQueue, message);
        } catch (Exception e) {
            throw new EJBException(e);
        }
    }

    @Override
    public boolean checkMessage(String text) {
        Connection conn = null;
        Session session = null;
        try {
            conn = myConnectionFactory.createConnection();
            conn.start();
            session = conn.createSession();

            MessageConsumer consumer = session.createConsumer(resultQueue);
            TextMessage msg = (TextMessage) consumer.receive(10000);
            if (msg == null) {
                Logger.getLogger("MySessionBean").severe("No result message received.");
                return false;
            } else {
                String result = msg.getText();
                if (result.equals("true:" + text)) {
                    return true;
                } else {
                    String errMsg = result.substring(result.indexOf(":") + 1);
                    Logger.getLogger("MySessionBean").severe(errMsg);
                    return false;
                }
            }
        } catch (JMSException e) {
            throw new EJBException(e);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    throw new EJBException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (JMSException e) {
                    throw new EJBException(e);
                }
            }
        }
    }
}
