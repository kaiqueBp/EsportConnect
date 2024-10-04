package com.example.esporte.model;

import com.example.esporte.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuarios usuarioExibicao;
    private String isGroup;
    private Grupo grupo;

    public void Salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();
        DatabaseReference databaseConversa = database.child("conversas");
        databaseConversa.child(getIdRemetente()).child(getIdDestinatario()).setValue(this);
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
