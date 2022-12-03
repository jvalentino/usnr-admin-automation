package com.blogspot.jvalentino.cac.security

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

class CacIdentity implements X509TrustManager, Serializable {
	String alias
	String authType;
	X509Certificate[] chain
	PrivateKey key
	CacIdentityType type
	String friendlyName

	CacIdentity(String alias, X509Certificate[] chain, PrivateKey key, CacIdentityType type, String friendlyName) {
		this.alias = alias
		this.chain = chain
		this.key = key
		this.type = type
		this.friendlyName = friendlyName
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) {
		this.chain = chain;
		this.authType = authType;
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
		this.chain = chain;
		this.authType = authType;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public X509Certificate[] getChain() {
		return chain;
	}

	public String getAuthType() {
		return authType
	}
}
