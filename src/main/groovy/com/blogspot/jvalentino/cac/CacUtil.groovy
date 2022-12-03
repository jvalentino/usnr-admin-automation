package com.blogspot.jvalentino.cac

import java.security.KeyStore;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;







import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

import com.blogspot.jvalentino.cac.security.CacIdentity;
import com.blogspot.jvalentino.cac.security.CacIdentityType;
import com.blogspot.jvalentino.cac.security.CacKeyManager;
import com.blogspot.jvalentino.cac.security.OpenTrustManager;
import com.blogspot.jvalentino.cac.security.SSLContextHttpWebConnection;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

class CacUtil {


	static Logger log = Logger.getLogger(CacUtil.class)
	
	/**
	 * Creates a key store based on a plugged in CAC reader containing
	 * a CAC using PKCS11. This requires that a card.config file be present
	 * in the root that specifies the location of CACKey,
	 * such as library = /Library/CACKey/libcackey.dylib
	 *
	 * @param pin
	 * @return
	 * @throws Exception
	 */
	static KeyStore getKeyStore(char[] pin, File configFile=null) throws Exception {
		
		log.info("KeyStore getKeyStore(****, ${configFile}) - Creates a Key store from a CAC")
		
		Provider p = null
		
		// Handle loading from a specific location
		if (configFile != null) {
			InputStream is = new FileInputStream(configFile)
			
			if (is == null) {
				throw new Exception(configFile.getAbsolutePath() + " is not a valid file")
			}
			
			p = new sun.security.pkcs11.SunPKCS11(is)
			
		} else {
			// default location
			String configName = "card.config"
			p = new sun.security.pkcs11.SunPKCS11(configName)
		}
		
		Security.addProvider(p)
		KeyStore cac = KeyStore.getInstance("PKCS11")
		cac.load(null, pin)
		return cac
	}
	
	/**
	 * Returns a list of the identities (certificates) on the CAC, taking care
	 * to label which ones are email and which one is the identify certificate
	 * @param pin
	 * @param ks
	 * @return
	 */
	static List<CacIdentity> getCacIdentities(char[] pin, KeyStore ks) {
		
		log.info("List<CacIdentity> getCacIdentities(****, ${ks})")
		
		ArrayList<CacIdentity> results = new ArrayList<CacIdentity>()
		
		Enumeration<String> aliases = ks.aliases();
		
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			X509Certificate[] cchain = (X509Certificate[]) ks.getCertificateChain(alias);
			
			// Only the actual supported certs on the CAC will have a chain
			if (cchain != null) {
				
				// we need to determine whether is is an email cert or not
				CacIdentityType type = CacIdentityType.IDENTITY
				
				String friendlyName = ""
				
				for (int i = 0; i < cchain.length; i ++) {
					if (i == 0) {
						friendlyName = cchain[i].getSubjectDN()
					}
					if (cchain[i].getIssuerDN().toString().contains("EMAIL")) {
						type = CacIdentityType.EMAIL
					}
				}
				
				// we need to obtain the private key from the cert (identity)
				Key key = ks.getKey(alias, pin);

				// The Identity is the alias, the chain, and the private key
				CacIdentity entity = new CacIdentity(alias, cchain, key, type, 
					type.toString() + ": " + friendlyName)
				results.add(entity)		
				
				log.info("\t" + entity.friendlyName)

			}
		}
		
		return results
	}
	
	/**
	 * Selects the first identity on the CAC that is used for email
	 * @param list
	 * @return
	 */
	static CacIdentity selectFirstEmailIdentity(List<CacIdentity> list) {
		return selectFirstIdentity(CacIdentityType.EMAIL, list)
	}
	
	/**
	 * Selects the first CAC identify of the specified type (EMAIL or IDENTITY)
	 * @param type
	 * @param list
	 * @return
	 */
	static CacIdentity selectFirstIdentity(CacIdentityType type, List<CacIdentity> list) {
		log.info("CacIdentity selectFirstIdentity(${type}, ${list})")
		for (CacIdentity current : list) {
			if (current.type == type) {
				log.info("\t" + current.friendlyName)
				return current
			}
		}
		return null
	}
	
	/**
	 * Creates an SSL Context using the keystore generated from the CAC, and then 
	 * one of the identities (Certificates) on that CAC
	 * @param keystore
	 * @param identity
	 * @return
	 */
	static SSLContext createSSLContext(CacIdentity identity) {
		log.info("SSLContext createSSLContext(" + identity.friendlyName + ")")
		KeyManager[] keyManagers = [ new CacKeyManager(identity) ]
		TrustManager[] trustManagers = [ new OpenTrustManager() ]
		
		SSLContext context = SSLContext.getInstance("TLSv1");
		context.init(keyManagers, trustManagers, new SecureRandom())
		
		return context
	}
	
	static SSLContext createSSLContext(char[] pin, CacIdentityType type) {
		return createSSLContext(pin, null, type)
	}
	
	/**
	 * Creates an SSL Context by using the pin to retrieve all identities, and
	 * then picking an identity that matches the given type (EMAIL or IDENTITY)
	 * 
	 * @param pin
	 * @param type
	 * @return
	 */
	static SSLContext createSSLContext(char[] pin, File configFile, CacIdentityType type) {
		log.info("SSLContext createSSLContext(****, ${configFile}, ${type})")
		KeyStore keystore = CacUtil.getKeyStore(pin, configFile)
		List<CacIdentity> identities = CacUtil.getCacIdentities(pin, keystore)
		CacIdentity identity = CacUtil.selectFirstIdentity(type, identities)
		SSLContext context = CacUtil.createSSLContext(identity)
		return context
	}
	
	static WebClient createWebClient(char[] pin, CacIdentityType type, BrowserVersion version=null) {
		return createWebClient(pin, null, type, version)
	}
	
	/**
	 * This took a bit of work to figure out, but establishes an SSL Context by using the pin to retrieve
	 * all identities on the plugged in CAC, picks an identity matching the given type (EMAIL or 
	 * IDENTITY), and creates an HTML-UNIT WebClient. In order for the web client to work 
	 * a custom connection manager is used which overrides the https protocol on port 443 to use
	 * the established SSL Context instead of using its own.
	 * 
	 * @param pin
	 * @param type
	 * @return
	 */
	static WebClient createWebClient(char[] pin, File configFile, CacIdentityType type, BrowserVersion version=null) {
		log.info("WebClient createWebClient(****, ${configFile}, ${type}, ${version}")
		SSLContext context = CacUtil.createSSLContext(pin, configFile, type)
		WebClient webClient = null
		if (version != null)
			webClient = new WebClient(version)
		else
			webClient = new WebClient()
		SSLContextHttpWebConnection conn = new SSLContextHttpWebConnection(webClient, context)
		webClient.setWebConnection(conn)
		return webClient
	}
	
	/*static void writeIdentityToFile(CacIdentity ks, File file) {
		FileOutputStream fileOut = new FileOutputStream(file)
		ObjectOutputStream out = new ObjectOutputStream(fileOut)
		out.writeObject(ks)
		out.close()
		fileOut.close()
	}*/
	
}
