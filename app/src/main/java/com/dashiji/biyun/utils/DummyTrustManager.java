package com.dashiji.biyun.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by GA on 2018/1/2.
 */

public class DummyTrustManager implements X509TrustManager {
    public void checkServerTrusted(X509Certificate[] cert, String authType) {
        return;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
