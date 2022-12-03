package com.blogspot.jvalentino.cac.security

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.SocketFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.log4j.Logger;

class CacProtocolSocketFactory implements SecureProtocolSocketFactory {

	static Logger log = Logger.getLogger(CacProtocolSocketFactory.class)
	
	private SSLContext sslcontext = null;
	
	CacProtocolSocketFactory(SSLContext sslcontext) {
		super()
		this.sslcontext = sslcontext
		log.info("CacProtocolSocketFactory(${sslcontext})")
	}
	
	private SSLContext getSSLContext() {
		return sslcontext
	}
	
	public Socket createSocket(String host, int port, InetAddress localAddress,
			int localPort) throws IOException, UnknownHostException {
		log.info("Socket createSocket(${host}, ${port}, ${localAddress}, ${localPort})")
		return getSSLContext().getSocketFactory().createSocket(
		 	host,
		 	port,
		 	localAddress,
		 	localPort
		 );
	}

	public Socket createSocket(String host, int port, InetAddress localAddress,
			int localPort, HttpConnectionParams params) throws IOException,
	UnknownHostException, ConnectTimeoutException {
		
		log.info("Socket createSocket(${host}, ${port}, ${localAddress}, ${localPort}, ${params})")
		
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null");
		}
		int timeout = params.getConnectionTimeout();
		SocketFactory socketfactory = getSSLContext().getSocketFactory();
		if (timeout == 0) {
			return socketfactory.createSocket(host, port, localAddress, localPort);
		} else {
			Socket socket = socketfactory.createSocket();
			SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
			SocketAddress remoteaddr = new InetSocketAddress(host, port);
			socket.bind(localaddr);
			socket.connect(remoteaddr, timeout);
			return socket;
		}
	}

	public Socket createSocket(String host, int port) throws IOException,
	UnknownHostException {
		
		log.info("Socket createSocket(${host}, ${port})")
		
		return getSSLContext().getSocketFactory().createSocket(
		host,
		port
		);
	}

	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		
		log.info("createSocket(${socket}, ${host}, ${port}, ${autoClose})")
			
		return getSSLContext().getSocketFactory().createSocket(
		socket,
		host,
		port,
		autoClose
		);
	}

}
