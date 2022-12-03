import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.util.List;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class SmartCardManual {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SmartCardManual instance = new SmartCardManual();
	}
	
    
      private X509Certificate generateCertificate(byte[] certValue) {  
          try {  
            InputStream is = new ByteArrayInputStream(certValue);  
            CertificateFactory cf = CertificateFactory.getInstance("X.509");  
            return (X509Certificate) cf.generateCertificate(is);  
          } catch (CertificateException ce) {  
            return null;  
          }  
        }  

}
