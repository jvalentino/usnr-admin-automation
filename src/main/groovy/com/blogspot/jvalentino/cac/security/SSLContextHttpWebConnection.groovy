package com.blogspot.jvalentino.cac.security

import com.gargoylesoftware.htmlunit.HttpWebConnection; 
import com.gargoylesoftware.htmlunit.WebClient; 

import org.apache.http.conn.scheme.Scheme; 
import org.apache.http.conn.scheme.SchemeRegistry; 
import org.apache.http.conn.ssl.SSLSocketFactory; 
import org.apache.log4j.Logger;

import java.security.KeyStore; 
import java.security.KeyManagementException; 
import java.security.KeyStoreException; 
import java.security.NoSuchAlgorithmException; 
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;

class SSLContextHttpWebConnection extends HttpWebConnection {

	static Logger log = Logger.getLogger(SSLContextHttpWebConnection.class)
	
	SSLContext context
	
	SSLContextHttpWebConnection(WebClient webClient, SSLContext context) {
		super(webClient);

		this.context = context
		
		log.info("SSLContextHttpWebConnection(${webClient}, ${context})")
		log.info("\tOverriding connection manager for https on port 443 with ${context}")
		
		SchemeRegistry schemeRegistry = getHttpClient().getConnectionManager().getSchemeRegistry();
		org.apache.http.conn.ssl.SSLSocketFactory sf = new org.apache.http.conn.ssl.SSLSocketFactory(context);
		schemeRegistry.register(new Scheme("https", 443, sf));
		
	}
	
}
