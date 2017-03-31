package com.zy.mocknet.server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by zy on 17-3-29.
 */
public class ServerSocketFactory {
    private volatile static ServerSocketFactory instance;

    private ServerSocketFactory() {

    }

    public static ServerSocketFactory getInstance() {
        if (instance == null) {
            synchronized (ServerSocketFactory.class) {
                if (instance == null) {
                    instance = new ServerSocketFactory();
                }
            }
        }
        return instance;
    }

    public ServerSocket createHttpServerSocket(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServerSocket createHttpsServerSocket(int port, String jksPath, String storePwd) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS"); // JKS: Java Key Store
            keyStore.load(new FileInputStream(jksPath), storePwd.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, storePwd.toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(km, tm, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            return sslServerSocket;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

}
