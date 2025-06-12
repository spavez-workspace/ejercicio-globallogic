package com.globallogic.ejercicio.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.globallogic.ejercicio.exception.EncryptionException;

import java.util.Base64;

public class AESUtil {
    
    private static final String ALGORITHM = "AES";

    // Encriptar
    public static String encrypt(String plainText, String secretKey){
    	try {
	        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
	        Cipher cipher = Cipher.getInstance(ALGORITHM);
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	
	        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
	        return Base64.getEncoder().encodeToString(encryptedBytes);
    	} catch (Exception e) {
            throw new EncryptionException("Error al encriptar", e);
        }
    }

    // Desencriptar
    public static String decrypt(String encryptedText, String secretKey){
    	try {
	        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
	        Cipher cipher = Cipher.getInstance(ALGORITHM);
	        cipher.init(Cipher.DECRYPT_MODE, key);
	
	        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
	        return new String(decryptedBytes);
    	} catch (Exception e) {
            throw new EncryptionException("Error al encriptar", e);
        }
    }

}