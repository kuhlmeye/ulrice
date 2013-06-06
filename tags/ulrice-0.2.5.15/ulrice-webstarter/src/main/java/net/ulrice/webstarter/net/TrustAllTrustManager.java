package net.ulrice.webstarter.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustAllTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		// Empty. Trust everyone.
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		// Empty. Trust everyone.
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		 return null;
	}

}
