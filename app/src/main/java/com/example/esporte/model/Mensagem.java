package com.example.esporte.model;

public class Mensagem {
    private String idUsuario;
    private String nome;
    private String mensagem;
    private String imagem;
    private Double latitude;
    private Double longitude;


    public Mensagem(String idUsuario, String nome, String mensagem, String imagem) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.mensagem = mensagem;
        this.imagem = imagem;
    }
    public Mensagem() {
        this.nome = "";
    }
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

}
