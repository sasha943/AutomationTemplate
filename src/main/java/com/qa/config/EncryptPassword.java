package com.qa.config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class EncryptPassword {
    private static final String ALGO = "AES";

    private static final byte[] keyValue = new byte[]{'m', 'Y', 'p', 'U',
            'b', 'l', 'I', 'c', 'k', 'E', 'y', 'n', 'A', 'e', 'E', 'M'};

    public static String decrypt(String encryptedData) {
        Key key;
        String decryptedValue = "";
        try {
            key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            decryptedValue = new String(decValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    public static void main(String... args) throws Exception {
        String password = "";
        String passwordEnc = EncryptPassword.encrypt(password);
        System.out.println("Plain Text : " + password
                + " and it's Encryption is ::    " + passwordEnc);
        String passwordDec = EncryptPassword.decrypt(passwordEnc);
        System.out.println("Decrypted Text : " + passwordDec);
    }
}