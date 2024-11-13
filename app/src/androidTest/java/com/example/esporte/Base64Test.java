package com.example.esporte;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Base64;

import com.example.esporte.config.Base64Custom;

import org.junit.Test;

public class Base64Test {
    private static final int NO_WRAP = 1;

    @Test
    public void testCodificar() {
        String originalText = "Hello, World!";
        String expectedEncoded = Base64.encodeToString(originalText.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)","");
        String encodedText = Base64Custom.codificar(originalText);
        assertNotNull("O texto codificado não deve ser nulo", encodedText);
        assertEquals("A codificação Base64 não retornou o valor esperado", expectedEncoded, encodedText);
    }

    @Test
    public void testDecodificar() {
        String originalText = "Hello, World!";
        String encodedText = Base64Custom.codificar(originalText);

        String decodedText = Base64Custom.decodificar(encodedText);

        assertNotNull("O texto decodificado não deve ser nulo", decodedText);
        assertEquals("A decodificação não retornou o valor original", originalText, decodedText);
    }
}
