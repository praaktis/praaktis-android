package com.praaktis.exerciseengine;

import android.widget.Button;

public class Dump {

    Button button;

    String HOSTNAME = "localhost";
    int PORT = 22;

    void func() {

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView tv = (TextView) findViewById(R.id.textView1);
//                try {
//                    InputStream fis = getAssets().open("cert.pem");
//                    BufferedInputStream bis = new BufferedInputStream(fis);
//                    Certificate certificate;
//                    try {
//                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//                        certificate = cf.generateCertificate(bis);
//                    } catch (CertificateException cex) {
//                        tv.setText("ERROR: " + cex.getMessage());
//                        return;
//                    } finally {
//                        bis.close();
//                    }
//                    KeyStore keyStore;
//                    SSLSocket sslSocket;
//                    try {
//                        keyStore = KeyStore.getInstance("BKS");
//                        keyStore.load(null, null);
//                        keyStore.setCertificateEntry("certAlias", certificate);
//                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//                        trustManagerFactory.init(keyStore);
//                        SSLContext sslctx = SSLContext.getInstance("TLS");
//                        sslctx.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//                        SSLSocketFactory factory = sslctx.getSocketFactory();
//                        sslSocket = (SSLSocket) factory.createSocket(HOSTNAME, PORT);
//                    } catch (KeyStoreException ksex) {
//                        tv.setText("ERROR: " + ksex.getMessage());
//                        return;
//                    } catch (GeneralSecurityException gsex) {
//                        tv.setText("ERROR: " + gsex.getMessage());
//                        return;
//                    }
//                    //SSLSocket sslSocket = sslConnect(HOSTNAME, PORT);
//                    BufferedReader br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
////                    String s = br.readLine();
////                    tv.setText("RECEIVED: " + s);
////                    br.close();
//                } catch (IOException ex) {
//                    tv.setText("ERROR: " + ex.getMessage());
//                }
//            }
//        });
    }
}
