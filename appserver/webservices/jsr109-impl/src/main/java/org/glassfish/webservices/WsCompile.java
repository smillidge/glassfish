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

package org.glassfish.webservices;


import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import java.util.Iterator;
import java.util.List;


import javax.xml.namespace.QName;

//JAX-RPC SPI
import com.sun.xml.rpc.spi.JaxRpcObjectFactory;
import com.sun.xml.rpc.spi.tools.CompileTool;
import com.sun.xml.rpc.spi.tools.CompileToolDelegate;
import com.sun.xml.rpc.spi.tools.HandlerChainInfo;
import com.sun.xml.rpc.spi.tools.HandlerInfo;
import com.sun.xml.rpc.spi.tools.Configuration;
import com.sun.xml.rpc.spi.tools.ModelInfo;
import com.sun.xml.rpc.spi.model.Model;
import com.sun.xml.rpc.spi.model.Service;
import com.sun.xml.rpc.spi.model.Port;
import com.sun.xml.rpc.spi.model.ModelProperties;

import com.sun.enterprise.deployment.WebService;
import com.sun.enterprise.deployment.WebServiceEndpoint;
import com.sun.enterprise.deployment.ServiceReferenceDescriptor;
import com.sun.enterprise.deployment.WebServiceHandler;
import com.sun.enterprise.deployment.NameValuePairDescriptor;

/**
 * This implementation of WsCompile should be used only internally 
 * at deployment time (or j2eec).  Not meant to be overridden.
 *
 * @author Kenneth Saks
 */
public final class WsCompile extends CompileToolDelegate {

    private Collection generatedFiles;
    private WebService webService;
    private ServiceReferenceDescriptor serviceRef;
    private WsUtil wsUtil = new WsUtil();
    private CompileTool wscompile;
    private JaxRpcObjectFactory rpcFactory;

    // if set, used to override standard configuration
    private ModelInfo modelInfo;

    // true if wscompile encountered an error.
    private boolean error = false;

    public WsCompile(CompileTool compileTool, WebService webServiceDesc) {
        wscompile = compileTool;
        webService = webServiceDesc;
        rpcFactory = JaxRpcObjectFactory.newInstance();
    }

    public WsCompile(CompileTool compileTool, 
                     ServiceReferenceDescriptor serviceRefDesc) {
        wscompile = compileTool;
        serviceRef = serviceRefDesc;
        rpcFactory = JaxRpcObjectFactory.newInstance();
    }

    public void setModelInfo(ModelInfo info) {
        modelInfo = info;
    }

    public CompileTool getCompileTool() {
        return wscompile;
    }

    /**
     * com.sun.xml.rpc.spi.tools.wscompile.CompileToolDelegate overrides.
     */

    public Configuration createConfiguration() {
        Configuration configuration = null;
        if( modelInfo != null ) {
            configuration = 
		rpcFactory.createConfiguration(wscompile.getEnvironment());
            configuration.setModelInfo(modelInfo);
        } 
        //else, leave it to the jaxrpc implementation to 
        //create Configuration

        return configuration;
    }

    public void preOnError() {
        error = true;
    }

    public void postRegisterProcessorActions() {

        if( !error) {

            if (webService != null) {
                setupServiceHandlerChain();
            } 

            // NOTE : Client handler chains are configured at runtime.
        }
    }

    public void postRun() {
        generatedFiles = new HashSet();

        if( !error ) {
            for(Iterator iter = wscompile.getEnvironment().getGeneratedFiles();
                iter.hasNext(); ) {
                generatedFiles.add( iter.next() );
            }
            if( webService != null ) {
                doServicePostProcessing();
            } else if( serviceRef != null ) {
                doClientPostProcessing();
            }
        }
    }

    //
    // WsCompile methods.
    //

    public Collection getGeneratedFiles() {
        return generatedFiles;
    }

    private void setupServiceHandlerChain() {

        Model model = wscompile.getProcessor().getModel();

        Collection endpoints = webService.getEndpoints();
        for(Iterator eIter = endpoints.iterator(); eIter.hasNext();) {
            WebServiceEndpoint nextEndpoint = (WebServiceEndpoint) eIter.next();

            if( !nextEndpoint.hasHandlers() ) {
                continue;
            }

            Port port = wsUtil.getPortFromModel(model, 
                                                nextEndpoint.getWsdlPort());
            if( port == null ) {
                throw new IllegalStateException("Model port for endpoint " + 
                                                nextEndpoint.getEndpointName() +
                                                " not found");
            }
            
            List handlerChain = nextEndpoint.getHandlers();
            HandlerChainInfo modelHandlerChain = 
                port.getServerHCI();
            List handlerInfoList = new ArrayList();

            // Insert an container handler as the first element.
            // This is needed to perform method authorization checks.
            HandlerInfo preHandler = rpcFactory.createHandlerInfo();
            String handlerClassName = nextEndpoint.implementedByEjbComponent() ?
                "org.glassfish.webservices.EjbContainerPreHandler" :
                "org.glassfish.webservices.ServletPreHandler";
            preHandler.setHandlerClassName(handlerClassName);
            handlerInfoList.add(preHandler);

            // Collect all roles defined on each handler and set them on
            // handler chain. NOTE : There is a bit of a mismatch here between 
            // 109 and JAXRPC.  JAXRPC only defines roles at the handler chain
            // level, whereas 109 descriptors put roles at the handler level.
            Collection soapRoles = new HashSet();            

            for(Iterator hIter = handlerChain.iterator(); hIter.hasNext();) {
                WebServiceHandler nextHandler = 
                    (WebServiceHandler) hIter.next();
                HandlerInfo handlerInfo = createHandlerInfo(nextHandler);
                handlerInfoList.add(handlerInfo);
                soapRoles.addAll(nextHandler.getSoapRoles());
            }

            // Insert a container handler as the last element in the chain.
            HandlerInfo postHandler = rpcFactory.createHandlerInfo();
            handlerClassName = nextEndpoint.implementedByEjbComponent() ?
                "org.glassfish.webservices.EjbContainerPostHandler" :
                "org.glassfish.webservices.ServletPostHandler";
            postHandler.setHandlerClassName(handlerClassName);
            handlerInfoList.add(postHandler);

            // @@@ should probably use addHandler api instead once
            // == bug is fixed.
            modelHandlerChain.setHandlersList(handlerInfoList);

            for(Iterator roleIter = soapRoles.iterator(); roleIter.hasNext();) {
                modelHandlerChain.addRole((String) roleIter.next());
            }
        }
    }

    private HandlerInfo createHandlerInfo(WebServiceHandler handler) {
        HandlerInfo handlerInfo = rpcFactory.createHandlerInfo();
        
        handlerInfo.setHandlerClassName(handler.getHandlerClass());
        for(Iterator iter = handler.getSoapHeaders().iterator(); 
            iter.hasNext();) {
            QName next = (QName) iter.next();
            handlerInfo.addHeaderName(next);
        }

        Map properties = handlerInfo.getProperties();
        for(Iterator iter = handler.getInitParams().iterator(); 
            iter.hasNext();) {
            NameValuePairDescriptor next = (NameValuePairDescriptor) 
                iter.next();
            properties.put(next.getName(), next.getValue());
        }

        return handlerInfo;
    }

    private void doServicePostProcessing() {

        Model model = wscompile.getProcessor().getModel();

        Collection endpoints = webService.getEndpoints();

        for(Iterator iter = endpoints.iterator(); iter.hasNext(); ) {
            WebServiceEndpoint next = (WebServiceEndpoint) iter.next();
            Service service = wsUtil.getServiceForPort(model, 
                                                       next.getWsdlPort());
            if( service == null ) {
                service = (Service) model.getServices().next();

                System.out.println("Warning : Can't find Service for Endpoint '"
                                   + next.getEndpointName() + "' Port '" +
                                   next.getWsdlPort() + "'");

                System.out.println("Defaulting to "+ service.getName());
            }

            QName serviceName = service.getName();

            next.setServiceNamespaceUri(serviceName.getNamespaceURI());
            next.setServiceLocalPart(serviceName.getLocalPart());

            Port port = wsUtil.getPortFromModel(model, next.getWsdlPort());
            if( port == null ) {
                String msg = "Can't find model port for endpoint " 
                    + next.getEndpointName() + " with wsdl-port " + 
                    next.getWsdlPort();
                throw new IllegalStateException(msg);
            }

            // If port has a tie class name property, use it.  Otherwise,
            // use naming convention to derive tie class name.  If there
            // are multiple ports per SEI(binding), then the property then
            // the TIE_CLASS_NAME property will be available.  In that case,
            // a separate tie and stub are generated per port.  
            String tieClassName = (String)
                port.getProperty(ModelProperties.PROPERTY_TIE_CLASS_NAME);
            if( tieClassName == null ) {
                tieClassName = next.getServiceEndpointInterface() + "_Tie";
            }
            next.setTieClassName(tieClassName);

            if( next.implementedByWebComponent() ) {
                wsUtil.updateServletEndpointRuntime(next);
            } else {
                wsUtil.validateEjbEndpoint(next);
            }

            String endpointAddressUri = next.getEndpointAddressUri();
            if( endpointAddressUri == null ) {
                String msg = "Endpoint address uri must be set for endpoint " +
                    next.getEndpointName();
                throw new IllegalStateException(msg);
            } else if( endpointAddressUri.indexOf("*") >= 0 ) {
                String msg = "Endpoint address uri " + endpointAddressUri + 
                    " for endpoint " + next.getEndpointName() + 
                    " is invalid. It must not contain the '*' character";
                throw new IllegalStateException(msg);
            } else if( endpointAddressUri.endsWith("/") ) {
                String msg = "Endpoint address uri " + endpointAddressUri + 
                    " for endpoint " + next.getEndpointName() + 
                    " is invalid. It must not end with '/'";
                throw new IllegalStateException(msg);
            }
        }
    }

    private void doClientPostProcessing() {
        Model model = wscompile.getProcessor().getModel();

        Iterator serviceIter = model.getServices();
        Service service = null;

        if( serviceRef.hasServiceName() ) {
            while( serviceIter.hasNext() ) {
                Service next = (Service) serviceIter.next();
                if( next.getName().equals(serviceRef.getServiceName()) ) {
                    service = next;
                    break;
                }
            }
            if( service == null ) {
                throw new IllegalStateException
                    ("Service " + serviceRef.getServiceName() + 
                     " for service-ref " + serviceRef.getName() + " not found");
            }
        } else {
            if( serviceIter.hasNext() ) {
                service = (Service) serviceIter.next();
                if( serviceIter.hasNext() ) {
                    throw new IllegalStateException
                        ("service ref " + serviceRef.getName() + " must specify"
                         + " service name since its wsdl declares multiple"
                         + " services");
                }
                QName sName = service.getName();
                serviceRef.setServiceNamespaceUri(sName.getNamespaceURI());
                serviceRef.setServiceLocalPart(sName.getLocalPart());
            } else {
                throw new IllegalStateException
                    ("service ref " + serviceRef.getName() + " WSDL must " +
                     "define at least one Service");
            }
        }

        // Use naming convention to derive Generated Service 
        // implementation class name.  
        String serviceImpl = service.getJavaIntf().getName() + "_Impl";
        serviceRef.setServiceImplClassName(serviceImpl);

    }

}
