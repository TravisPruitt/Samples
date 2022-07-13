/**
 * $Id$
 * 
 * Copyright (c) 2011, Disney Enterprises, Inc.
 */
package com.disney.xband.common.lib;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

/**
 * Encryption and Decryption using Jasypt API
 * 
 * The XconnectJasyptManager is similar to the nge JasyptManager, but it allows for calling 
 * getEncryptedData and getDecryptedData multiple times without having to re-load the
 * entire JasyptManager.
 */
public class XconnectJasyptManager {

    private final StandardPBEStringEncryptor standardPBEString;

    /**
     * Constructor setting a password for jasypt StandardPBEStringEncryptor API
     */
    public XconnectJasyptManager(String password) {
        standardPBEString = new StandardPBEStringEncryptor();
        standardPBEString.setProvider(new BouncyCastleProvider());
        standardPBEString.setAlgorithm("PBEWithSHA256And256BitAES-CBC-BC");
        standardPBEString.setPassword(password);
    }

    /**
     * @param actualInput
     *            as String
     * @param passWord
     *            as String
     * @return encrypts and return encrypted text
     */
    public String getEncryptedData(final String actualInput) {
        return standardPBEString.encrypt(actualInput);
    }

    /**
     * @param encryptedData
     *            as String
     * @return decrypts and return normal text
     */
    @SuppressWarnings("PMD.AvoidUsingTryCatch")
    public String getDecryptedData(final String encryptedData) {
        String decryptedText = null;
        try {
            decryptedText = standardPBEString.decrypt(encryptedData);
        } catch (final EncryptionOperationNotPossibleException EONPE) {
            return encryptedData;
        }
        return decryptedText;
    }

}
