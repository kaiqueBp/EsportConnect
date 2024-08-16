package com.example.esporte.config;

import com.example.esporte.model.Endereco;

public interface ValidateCityCallback {
    void onCityValidated(boolean isValid, Endereco endereco);
}
