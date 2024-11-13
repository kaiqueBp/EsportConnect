package com.example.esporte;

import static org.junit.Assert.assertEquals;

import com.example.esporte.model.Conversa;

import org.junit.Test;

public class ConversaTest {
    @Test
    public void testSalvar() {
        Conversa conversa = new Conversa();
        conversa.setIdRemetente("idRemetente");
        conversa.setIdDestinatario("idDestinatario");
        conversa.setUltimaMensagem("Última mensagem");
        conversa.setIsGroup("false");
        conversa.Salvar();
    }

    @Test
    public void testGettersAndSetters() {
        Conversa conversa = new Conversa();
        conversa.setIdRemetente("idRemetente");
        conversa.setIdDestinatario("idDestinatario");
        conversa.setUltimaMensagem("Última mensagem");
        conversa.setIsGroup("false");

        assertEquals("idRemetente", conversa.getIdRemetente());
        assertEquals("idDestinatario", conversa.getIdDestinatario());
        assertEquals("Última mensagem", conversa.getUltimaMensagem());
        assertEquals("false", conversa.getIsGroup());
    }
}
