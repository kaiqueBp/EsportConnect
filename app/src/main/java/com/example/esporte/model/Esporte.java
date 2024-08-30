package com.example.esporte.model;

import java.io.Serializable;
import java.util.List;

public class Esporte implements Serializable {
    private String id;
    private String nome;
    private String image;
    private List<String> esportes ;
    public Esporte() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String imagem) {
        this.image = imagem;
    }

    public Esporte(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getEsportes() {
        return esportes;
    }

    public void setEsportes(List<String> esportes) {
        this.esportes = esportes;
    }
}
