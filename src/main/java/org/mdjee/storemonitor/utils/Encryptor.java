package org.mdjee.storemonitor.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Encryptor {

    // Encryption password
    private final String password = "2RlJ935Iblo*qFvV#JPm9?q";

    public String encrypt(String plainText){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);

        // Default Algorithm
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        String encryptedText = encryptor.encrypt(plainText);

        return encryptedText;
    }

    public String decrypt(String encryptedText){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);

        // Default Algorithm
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        String plainText = encryptor.decrypt(encryptedText);

        return plainText;
    }

    public StandardPBEStringEncryptor getEncryptor(){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(password);

        return encryptor;
    }
}
