package com.example.esporte.model;

import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {
    private String id;
    private String nome;
    private String foto;
    private List<Usuarios> membros;

    public Grupo() {
        DatabaseReference dataRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference grupoRef = dataRef.child("grupos");
        String id = grupoRef.push().getKey();
        this.setId(id);
    }
    public void Salvar() {
        DatabaseReference dataRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference grupoRef = dataRef.child("grupos");
        grupoRef.child(getId()).setValue(this);

        for ( Usuarios membros : getMembros()) {
            String idRemetente  = Base64Custom.codificar(membros.getEmail());

            Conversa convrersa = new Conversa();
            convrersa.setIdRemetente(idRemetente);
            convrersa.setIdDestinatario(getId());
            convrersa.setUltimaMensagem("");
            convrersa.setIsGroup("true");
            convrersa.setGrupo(this);
            convrersa.Salvar();
        }
    }

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
