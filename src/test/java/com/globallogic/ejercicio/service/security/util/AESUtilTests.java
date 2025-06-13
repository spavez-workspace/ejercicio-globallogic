package com.globallogic.ejercicio.service.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.globallogic.ejercicio.exception.EncryptionException;
import com.globallogic.ejercicio.security.util.AESUtil;

public class AESUtilTests {

    private static final String SECRET_KEY = "1234567890123456";
    private static final String PLAINTEXT = "Este es un texto secreto";

    @Test
    public void testEncryptDecrypt() throws Exception {

        String encrypted = AESUtil.encrypt(PLAINTEXT, SECRET_KEY);
        assertNotNull(encrypted);
        assertNotEquals(PLAINTEXT, encrypted, "El texto encriptado no debe ser igual al texto original");

        String decrypted = AESUtil.decrypt(encrypted, SECRET_KEY);
        assertNotNull(decrypted);
        assertEquals(PLAINTEXT, decrypted, "El texto desencriptado debe ser igual al texto original");
    }

    @Test
    public void testEncryptWithNullText() {
        assertThrows(EncryptionException.class, () -> {
            AESUtil.encrypt(null, SECRET_KEY);
        });
    }

    @Test
    public void testDecryptWithInvalidCiphertext() {
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt("texto-invalido", SECRET_KEY);
        });
    }
}