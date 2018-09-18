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

package org.glassfish.admin.rest.provider;

import com.sun.enterprise.admin.remote.ParamsWithPayload;
import com.sun.enterprise.admin.remote.writer.MultipartProprietaryWriter;
import com.sun.enterprise.v3.common.ActionReporter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.glassfish.api.ActionReport;

/**
 *
 * @author martinmares
 */
@Provider
@Produces("multipart/mixed")
public class ParamsWithPayloadMultipartWriter extends MultipartProprietaryWriter implements MessageBodyWriter<ParamsWithPayload> {
    
    private static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    
    private static final ActionReportJson2Provider arWriter = new ActionReportJson2Provider();
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ParamsWithPayload.class.isAssignableFrom(type) && mediaType.isCompatible(MULTIPART_MIXED);
    }

    @Override
    public void writeTo(ParamsWithPayload proxy, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            final MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Object value = httpHeaders.getFirst("MIME-Version");
        if (value == null) {
            httpHeaders.putSingle("MIME-Version", "1.0");
        }
        super.writeTo(proxy.getPayloadOutbound(), proxy.getParameters(), proxy.getActionReport(), entityStream, new MultipartProprietaryWriter.ContentTypeWriter() {
                        @Override
                        public void writeContentType(String firstPart, String secondPart, String boundary) {
                            StringBuilder ct = new StringBuilder();
                            ct.append(firstPart).append('/').append(secondPart);
                            if (boundary != null) {
                                ct.append("; boundary=").append(boundary);
                            }
                            httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, ct.toString());
                        }
                    });
    }
    
    @Override
    protected void writeActionReport(final Writer writer,
                                        final OutputStream underOS,
                                        final String boundary, 
                                        final ActionReport ar) throws IOException {
        //        //Inrtroducing boundery
//        if (isFirst) {
//            isFirst = false;
//        } else {
//            writer.write(EOL);
//        }
        multiWrite(writer, BOUNDERY_DELIMIT, boundary, EOL);
        //Headers
        multiWrite(writer, "Content-Disposition: file; name=\"ActionReport\"", EOL);
        multiWrite(writer, "Content-Type: ", MediaType.APPLICATION_JSON, EOL);
        //Data
        //Data
        writer.write(EOL);
        writer.flush();
        arWriter.writeTo((ActionReporter) ar, ar.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE, null, underOS);
        underOS.flush();
        writer.write(EOL);
    }

    @Override
    public long getSize(ParamsWithPayload t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
