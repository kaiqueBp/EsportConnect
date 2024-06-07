package com.example.esporte.model;

public class Mensagem {
    private String idUsuario;
    private String nome;
    private String mensagem;
    private String imagem;


    public Mensagem(String idUsuario, String nome, String mensagem, String imagem) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.mensagem = mensagem;
        this.imagem = imagem;
    }
    public Mensagem() {
    }
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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
