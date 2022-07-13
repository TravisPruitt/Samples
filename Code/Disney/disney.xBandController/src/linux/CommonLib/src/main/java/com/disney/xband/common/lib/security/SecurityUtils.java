package com.disney.xband.common.lib.security;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/11/13
 * Time: 2:36 PM
 */
public class SecurityUtils {
    private static Logger logger = Logger.getLogger(SecurityUtils.class);
    private static final int SH_SALT_LENGTH = 4;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean isHashValid(final String str, final String hash) {
        if((str == null) || (str.length() == 0)) {
            return false;
        }

        if(hash.length() <= SH_SALT_LENGTH) {
            return false;
        }

        final String salt = hash.substring(0, SH_SALT_LENGTH);

        try {
            final String hash2 = getSecureHash(str, salt);

            return hash.equals(hash2);
        }
        catch (Exception e) {
            logger.error("Failed to get secure hash: " + e.getMessage());
        }

        return false;
    }

    public static String getSecureHash(final String str) {
        return getSecureHash(str, null);
    }

    public static String getSecureHash(final String str, String salt) {
        if(str == null) {
            return str;
        }

        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");

            if(salt == null) {
                salt = getShaSalt(SH_SALT_LENGTH);
            }

            md.update((salt + str).getBytes("UTF-8"));
            final byte[] digest = md.digest();
            return salt + new String(Base64.encode(digest));
        }
        catch (Exception e) {
            logger.error("Failed to get secure hash: " + e.getMessage());
        }

        return null;
    }

    private static String getShaSalt(final int nBytes) {
        return new String(Base64.encode((new SecureRandom()).generateSeed(nBytes))).substring(0, nBytes);
    }
}
