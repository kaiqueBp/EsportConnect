package com.example.esporte;
import static org.junit.Assert.assertNotNull;

import com.example.esporte.config.NominatimApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class APITest {
    private MockWebServer mockWebServer;
    private OkHttpClient mockClient;
    private NominatimApi nominatimApi;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        mockClient = new OkHttpClient.Builder()
                .build();

        nominatimApi = new NominatimApi(mockClient);
    }

    @Test
    public void testSearchCity_Success() throws IOException, InterruptedException {
        String estado = "MS";
        String result = NominatimApi.searchCity(estado);
        assertNotNull(result);
    }

    @Test(expected = IOException.class)
    public void testSearchCity_Failure() throws IOException {
        // Simula uma falha no servidor
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // Tenta realizar a requisição, que deverá falhar
        NominatimApi.searchCity("Sao Paulo");
    }

    @After
    public void tearDown() throws IOException {
        // Fecha o MockWebServer
        mockWebServer.shutdown();
    }
}
