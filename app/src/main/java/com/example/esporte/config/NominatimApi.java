package com.example.esporte.config;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NominatimApi {
    private static final String API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/";
    private static final OkHttpClient client = new OkHttpClient();

    public NominatimApi(OkHttpClient mockClient) {
    }

    public static String searchCity(String estado) throws IOException {
        String estadoEncoded = URLEncoder.encode(estado, "UTF-8");
        Request request = new Request.Builder()
                .url(API_URL + estadoEncoded +"/municipios")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            // Log or handle the exception as needed
            throw new IOException("Error while fetching city data: " + e.getMessage(), e);
        }
    }
}