package com.example.esporte.model;

import com.example.esporte.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    private String idUsuario;
    private String nome;
    private String idade;
    private String sexo;
    private String email;
    private String senha;
    private String foto;
    private List<String> esportes;
    private Endereco endereco;

    public Usuarios(String nome) {
        this.nome = nome;
    }

    public Usuarios() {

    }
    public void salvar(){
        DatabaseReference referencia = ConfiguracaoFirebase.getFirebase();
        referencia.child("Usuarios").child(this.idUsuario).setValue(this);
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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

    public List<String> getEsportes() {
        return esportes;
    }

    public void setEsportes(List<String> esportes) {
        this.esportes = esportes;
    }
    public void adicionarEsporte(String esporte) {
        if (this.esportes == null) {
            this.esportes = new ArrayList<>();
        }
        this.esportes.add(esporte.toString());
    }
    public void removerEsporte(String esporte) {
        if (this.esportes != null) {
            this.esportes.remove(esporte);
        }
    }

}
