package com.example.salvadore.rest;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
public class CustomX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                   String authType) throws CertificateException {

    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
