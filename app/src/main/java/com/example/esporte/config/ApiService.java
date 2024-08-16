package com.example.esporte.config;

import com.example.esporte.model.Endereco;
//import com.example.testelogin.model.Municipio;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("{cep}/json")
    Call<Endereco> getEndereco(@Path("cep") String cep);
}
