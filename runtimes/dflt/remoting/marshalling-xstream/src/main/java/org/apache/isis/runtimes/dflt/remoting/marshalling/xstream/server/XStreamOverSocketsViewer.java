/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.runtimes.dflt.remoting.marshalling.xstream.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.isis.runtimes.dflt.remoting.common.facade.ServerFacade;
import org.apache.isis.runtimes.dflt.remoting.common.protocol.ObjectEncoderDecoder;
import org.apache.isis.runtimes.dflt.remoting.marshalling.xstream.shared.XStreamMarshaller;
import org.apache.isis.runtimes.dflt.remoting.server.ServerConnection;
import org.apache.isis.runtimes.dflt.remoting.server.ServerConnectionDefault;
import org.apache.isis.runtimes.dflt.remoting.transport.ConnectionException;
import org.apache.isis.runtimes.dflt.remoting.transport.simple.SimpleTransport;
import org.apache.isis.runtimes.dflt.remoting.transport.sockets.server.SocketsViewerAbstract;

public class XStreamOverSocketsViewer extends SocketsViewerAbstract {

    public XStreamOverSocketsViewer(ObjectEncoderDecoder objectEncoderDecoder) {
        super(objectEncoderDecoder);
    }

    @Override
    protected ServerConnection createServerConnection(final InputStream input, final OutputStream output,
        final ServerFacade distribution) {
        SimpleTransport transport = new SimpleTransport(getConfiguration(), input, output);
        XStreamMarshaller serverMarshaller = new XStreamMarshaller(getConfiguration(), transport);
        try {
            serverMarshaller.connect();
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
        return new ServerConnectionDefault(distribution, serverMarshaller);
    }
}