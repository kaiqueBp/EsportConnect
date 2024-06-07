package com.example.esporte.model;

import java.util.List;

public class Grupo {
    private String id;
    private String nome;
    private String foto;
    private List<Usuarios> membros;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuarios> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuarios> membros) {
        this.membros = membros;
    }
}
