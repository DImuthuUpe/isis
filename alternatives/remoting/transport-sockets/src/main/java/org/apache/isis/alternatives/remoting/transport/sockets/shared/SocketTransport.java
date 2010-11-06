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


package org.apache.isis.alternatives.remoting.transport.sockets.shared;

import static org.apache.isis.alternatives.remoting.transport.sockets.shared.SocketTransportConstants.PORT_DEFAULT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.isis.metamodel.config.IsisConfiguration;
import org.apache.isis.remoting.transport.ConnectionException;
import org.apache.isis.remoting.transport.ProfilingInputStream;
import org.apache.isis.remoting.transport.ProfilingOutputStream;
import org.apache.isis.remoting.transport.TransportAbstract;

public class SocketTransport extends TransportAbstract {

    private static final Logger LOG = Logger.getLogger(SocketTransport.class);

    private Socket socket;
    private int port;
    private String host;
    private boolean profiling;
    
    private OutputStream outputStream;
    private InputStream inputStream;

    public SocketTransport(IsisConfiguration configuration) {
    	super(configuration);
    }

    //////////////////////////////////////////////////////////
    // init, shutdown
    //////////////////////////////////////////////////////////

    public void init() {
        port = getConfiguration().getInteger(SocketTransportConstants.PORT_KEY, PORT_DEFAULT);
        host = getConfiguration().getString(SocketTransportConstants.HOST_KEY, SocketTransportConstants.HOST_DEFAULT);
        profiling = getConfiguration().getBoolean(SocketTransportConstants.PROFILING_KEY, SocketTransportConstants.PROFILING_DEFAULT);
        LOG.info("connections will be made to " + host + " " + port);
    }

	public void shutdown() {
		// does nothing
	}

    //////////////////////////////////////////////////////////
    // Configuration
    //////////////////////////////////////////////////////////

    protected boolean isProfiling() {
        return profiling;
    }

    
    //////////////////////////////////////////////////////////
    // connect, disconnect
    //////////////////////////////////////////////////////////

	public void connect() {
        if (socket != null) {
        	return;
		}
        
        try {
		    socket = new Socket(host, port);
		    inputStream = socket.getInputStream();
		    outputStream = socket.getOutputStream();
		    if (isProfiling()) {
		        inputStream = new ProfilingInputStream(inputStream);
		        outputStream = new ProfilingOutputStream(outputStream);
		    }
		    if (LOG.isDebugEnabled()) {
		    	LOG.debug("connection established " + socket);
		    }
		} catch (final MalformedURLException e) {
		    throw new ConnectionException("Connection failure", e);
		} catch (final UnknownHostException e) {
		    throw new ConnectionException("Connection failure", e);
		} catch (final ConnectException e) {
		    throw new ConnectionException("Failed to connect to " + host + "/" + port, e);
		} catch (final IOException e) {
		    throw new ConnectionException("Connection failure", e);
		}
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (final IOException e) {
                LOG.error("failed to close connection", e);
            }
        }
	}


}