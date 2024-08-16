package com.example.esporte.config;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NominatimApi {
    private static final String API_URL = "https://nominatim.openstreetmap.org/search";
    private static final String FORMAT = "json";

    public static String searchCity(String city, String estado) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String cityEncoded = URLEncoder.encode(city, "UTF-8");
        String estadoEncoded = URLEncoder.encode(estado, "UTF-8");

        Request request = new Request.Builder()
                .url(API_URL + "?q=" + cityEncoded +"&"+estadoEncoded+ "&format=" + FORMAT)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}