import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLClient {
    public static void main(String[] args) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] password = "password".toCharArray();
            keyStore.load(new FileInputStream("clientkeystore.jks"), password);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);

            // Load server's certificate into client's truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("clienttruststore.jks"), password);
            trustManagerFactory.init(trustStore);

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 9999);

            PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            writer.println("Hello from client");
            String response = reader.readLine();
            System.out.println("Response from server: " + response);

            sslSocket.close();

        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }
}