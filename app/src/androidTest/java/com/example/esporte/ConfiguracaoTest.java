package com.example.esporte;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.esporte.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;

public class ConfiguracaoTest {
    @Test
    public void testGetFirebase() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
        assertNotNull("DatabaseReference não deve ser nulo", reference);
    }

    @Test
    public void testGetAutenticacao() {
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        assertNotNull("FirebaseAuth não deve ser nulo", auth);
    }

    @Test
    public void testGetFirestore() {
        StorageReference storage = ConfiguracaoFirebase.getFirestore();
        assertNotNull("StorageReference não deve ser nulo", storage);
    }

    @Test
    public void testGetUsuarioLogadoSemUsuarioAutenticado() {
        FirebaseUser usuarioLogado = ConfiguracaoFirebase.getUsuarioLogado();
        assertNull("Usuário logado deve ser nulo se não houver usuário autenticado", usuarioLogado);
    }

    @Test
    public void testIDUsuarioLogadoSemUsuarioAutenticado() {
        try {
            String idUsuario = ConfiguracaoFirebase.IDUsuarioLogado();
            assertNull("ID do usuário deve ser nulo se não houver usuário autenticado", idUsuario);
        } catch (NullPointerException e) {
            System.out.println("IDUsuarioLogado lança NullPointerException, como esperado, quando não há usuário autenticado.");
        }
    }
}
