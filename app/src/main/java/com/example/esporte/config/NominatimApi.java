package com.example.esporte.config;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NominatimApi {
    private static final String API_URL = "http://educacao.dadosabertosbr.org/api/cidades/";

    public static String searchCity(String estado) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String estadoEncoded = URLEncoder.encode(estado, "UTF-8");
        Request request = new Request.Builder()
                .url(API_URL + estadoEncoded)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return response.body().string();
    }
}