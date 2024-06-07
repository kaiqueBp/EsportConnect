package com.example.esporte.model;

import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    private String nome;
    private String idade;
    private String sexo;
    private String email;
    private String senha;
    private List<Esporte> esportes;

    public Usuarios(String nome) {
        this.nome = nome;
    }

    public Usuarios() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Esporte> getEsportes() {
        return esportes;
    }

    public void setEsportes(List<Esporte> esportes) {
        this.esportes = esportes;
    }
    public void adicionarEsporte(Esporte esporte) {
        if (this.esportes == null) {
            this.esportes = new ArrayList<>();
        }
        this.esportes.add(esporte);
    }
    public void removerEsporte(Esporte esporte) {
        if (this.esportes != null) {
            this.esportes.remove(esporte);
        }
    }

}
