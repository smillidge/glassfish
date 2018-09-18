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

import org.glassfish.api.deployment.archive.ArchiveType;
import org.glassfish.deployment.common.RootDeploymentDescriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Information about the web services defined in a single module.
 *
 * @author Kenneth Saks
 * @author Jerome Dochez
 */

public class WebServicesDescriptor extends RootDeploymentDescriptor {

    // Module in which these web services are defined.
    private BundleDescriptor bundleDesc;

    private Collection<WebService> webServices;
    /** 
     * Default constructor. 
     */
    public WebServicesDescriptor() {
        webServices = new HashSet<WebService>();
    }

    /**
     * @return the default version of the deployment descriptor
     * loaded by this descriptor
     */
    public String getDefaultSpecVersion() {
        return "1.3";//TODO fix this WebServicesDescriptorNode.SPEC_VERSION;
    }

    public void setBundleDescriptor(BundleDescriptor module) {
        bundleDesc = module;
    }

    public BundleDescriptor getBundleDescriptor() {
        return bundleDesc;
    }

    public boolean hasWebServices() {
        return !(webServices.isEmpty());
    }

    public boolean isEmpty() {
        return webServices.isEmpty();
    }

    public WebService getWebServiceByName(String webServiceName) {
        for (WebService webService : webServices) {
            if( webService.getName().equals(webServiceName) ) {
                return webService;
            }
        }
        return null;
    }

    public void addWebService(WebService descriptor) {
        descriptor.setWebServicesDescriptor(this);
        webServices.add(descriptor);

    }

    public void removeWebService(WebService descriptor) {
        descriptor.setWebServicesDescriptor(null);
        webServices.remove(descriptor);

    }

    public Collection<WebService> getWebServices() {
        return new HashSet<WebService>(webServices);
    }

    /**
     * Endpoint has a unique name within all the endpoints in the module.
     * @return WebServiceEndpoint or null if not found
     */
    public WebServiceEndpoint getEndpointByName(String endpointName) {
        for(Iterator iter = getEndpoints().iterator(); iter.hasNext();) {
            WebServiceEndpoint next = (WebServiceEndpoint) iter.next();
            if( next.getEndpointName().equals(endpointName) ) {
                return next;
            }
        }
        return null;
    }

    public boolean hasEndpointsImplementedBy(EjbDescriptor ejb) {
        return !(getEndpointsImplementedBy(ejb).isEmpty());
    }

    public Collection<WebServiceEndpoint> getEndpointsImplementedBy(EjbDescriptor ejb) {
        Collection<WebServiceEndpoint> endpoints = new HashSet();
        if( ejb instanceof EjbSessionDescriptor ) {
            for(WebServiceEndpoint next : getEndpoints()) {
                if( next.implementedByEjbComponent(ejb) ) {
                    endpoints.add(next);
                }
            }
        }
        return endpoints;
    }

    public boolean hasEndpointsImplementedBy(WebComponentDescriptor desc) {
        return !(getEndpointsImplementedBy(desc).isEmpty());
    }

    public Collection<WebServiceEndpoint> getEndpointsImplementedBy(WebComponentDescriptor desc) {
        Collection<WebServiceEndpoint> endpoints = new HashSet();
        for(WebServiceEndpoint next : getEndpoints()) {
            if( next.implementedByWebComponent(desc) ) {
                endpoints.add(next);
            }
        }
        return endpoints;
    }

    public Collection<WebServiceEndpoint> getEndpoints() {
        Collection allEndpoints = new HashSet();
        for(WebService webService : webServices) {
            allEndpoints.addAll( webService.getEndpoints() );
        }
        return allEndpoints;
    }
    
    public ArchiveType getModuleType() {
        if (bundleDesc!=null) {
          return bundleDesc.getModuleType();  
        } 
        return null; 
    }

    
    //
    // Dummy RootDeploymentDescriptor implementations for methods that
    // do not apply to WebServicesDescriptor.
    //
    public String getModuleID() { return ""; } 
    public ClassLoader getClassLoader() { return null; }
    public boolean isApplication() {return false; }

    /**
     * Returns a formatted String of the attributes of this object.
     */
    public void print(StringBuffer toStringBuffer) {
	super.print(toStringBuffer);
        if (hasWebServices()) {
            for (Iterator itr = getWebServices().iterator();itr.hasNext();) {
                WebService aWebService = (WebService) itr.next();
                toStringBuffer.append("\n Web Service : ");
                aWebService.print(toStringBuffer);
            }
        }
    }
    
}    
