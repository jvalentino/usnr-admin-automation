package com.blogspot.jvalentino.cac.security

import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509ExtendedKeyManager;

import org.apache.log4j.Logger;


class CacKeyManager extends X509ExtendedKeyManager {

	static Logger log = Logger.getLogger(CacKeyManager.class)
	
	CacIdentity identity = null
	
	CacKeyManager(CacIdentity identity) {
		log.info("CacKeyManager(" + identity.friendlyName + ")")
		this.identity = identity
	}
	
	@Override
	public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
		log.info("String chooseClientAlias(${arg0}, ${arg1}, ${arg2})")
		log.info("\t" + identity.alias)
		return identity.alias
	}

	@Override
	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {	
		log.info("String chooseServerAlias(${arg0}, ${arg1}, ${arg2})")
		log.info("\tnull")
		return null;
	}

	@Override
	public X509Certificate[] getCertificateChain(String arg0) {
		log.info("X509Certificate[] getCertificateChain(${arg0})")
		return identity.chain
	}

	@Override
	public String[] getClientAliases(String arg0, Principal[] arg1) {
		log.info("String[] getClientAliases(${arg0}, ${arg1})")
		log.info("\tnull")
		return null
	}

	@Override
	public PrivateKey getPrivateKey(String arg0) {
		log.info("PrivateKey getPrivateKey(${arg0})")
		log.info("\t" + identity.key)
		return identity.key
	}

	@Override
	public String[] getServerAliases(String arg0, Principal[] arg1) {
		log.info("String[] getServerAliases(${arg0}, ${arg1})")
		log.info("\tnull")
		return null;
	}

}
