package com.example.esporte.config;

import android.util.Base64;

public class Base64Custom {

    public static String codificar(String texto) {

        return Base64.encodeToString(texto.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)","");

    }
    public static String decodificar(String textoCodificado) {
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
