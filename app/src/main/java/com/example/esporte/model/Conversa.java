package com.example.esporte.model;

import com.example.esporte.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Conversa implements Serializable {
    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuarios usuarioExibicao;
    private String isGroup;
    private Grupo grupo;
    private String visualiza;

    public Conversa() {

        this.setIsGroup("false");
        this.setVisualiza("false");
    }

    public void Salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();
        DatabaseReference databaseConversa = database.child("Conversas");
        databaseConversa.child(getIdRemetente()).child(getIdDestinatario()).setValue(this);
        //databaseConversa.child(getIdDestinatario()).child(getIdRemetente()).setValue(this);
    }

    public String getVisualiza() {
        return visualiza;
    }

    public void setVisualiza(String visualiza) {
        this.visualiza = visualiza;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuarios getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuarios usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}
