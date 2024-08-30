package com.example.esporte.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Endereco implements Serializable {
    private String localidade; // Nome da cidade
    private String uf; // Sigla do estado

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
    private static Map<String, String> estados = new HashMap<>();

    static {
        estados.put("AC", "Acre");
        estados.put("AL", "Alagoas");
        estados.put("AM", "Amazonas");
        estados.put("AP", "Amapá");
        estados.put("BA", "Bahia");
        estados.put("CE", "Ceará");
        estados.put("DF", "Distrito Federal");
        estados.put("ES", "Espírito Santo");
        estados.put("GO", "Goiás");
        estados.put("MA", "Maranhão");
        estados.put("MG", "Minas Gerais");
        estados.put("MS", "Mato Grosso do Sul");
        estados.put("MT", "Mato Grosso");
        estados.put("PA", "Pará");
        estados.put("PB", "Paraíba");
        estados.put("PE", "Pernambuco");
        estados.put("PI", "Piauí");
        estados.put("PR", "Paraná");
        estados.put("RJ", "Rio de Janeiro");
        estados.put("RN", "Rio Grande do Norte");
        estados.put("RO", "Rondônia");
        estados.put("RR", "Roraima");
        estados.put("RS", "Rio Grande do Sul");
        estados.put("SC", "Santa Catarina");
        estados.put("SE", "Sergipe");
        estados.put("SP", "São Paulo");
        estados.put("TO", "Tocantins");
    }

    public static String getEstado(String sigla) {
        return estados.get(sigla.toUpperCase());
    }
}
