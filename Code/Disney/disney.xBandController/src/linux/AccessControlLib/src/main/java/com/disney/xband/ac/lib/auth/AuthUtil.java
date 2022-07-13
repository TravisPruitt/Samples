package com.disney.xband.ac.lib.auth;

import org.apache.commons.codec.binary.Base64;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;

public class AuthUtil {
     /**
     * Provides the missing functionality of keytool that Apache needs for SSLCertificateKeyFile.
     *
     * @param args  <ul>
     *              <li> [0] Keystore filename.
     *              <li> [1] Keystore password.
     *              <li> [2] alias
     *              </ul>
     */
    static public void main(String[] args)
    throws Exception {
        final String keystoreName = "/tmp/keystore.jks";
        final String keystorePassword = AuthUtil.getKeyPassword(args, null); //"changeit";
        final String alias = "webvankey";
        final String keyPassword = AuthUtil.getKeyPassword(args, null); //"changeit";

        KeyStore ks = KeyStore.getInstance("jks");
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(keystoreName);
            ks.load(fis, keystorePassword.toCharArray());
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (Exception ignore) {
                }
            }
        }

        Key key = ks.getKey(alias, keyPassword.toCharArray());
        String b64 = new String(Base64.encodeBase64(key.getEncoded(), true));

        System.out.println("-----BEGIN PRIVATE KEY-----");
        System.out.println(b64);
        System.out.println("-----END PRIVATE KEY-----");
    }

    private static String getKeyPassword(final String[] args, final String keystorePassword)
    {
       String keyPassword = keystorePassword;

       if(args.length == 4) {
           keyPassword = args[3];
       }
       return keyPassword;
    }
}
