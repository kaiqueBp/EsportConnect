package com.example.esporte;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.example.esporte.model.Esporte;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    private  FirebaseFirestore db;
    private Grupo grupo;
    private List<Usuarios> usuarios;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private Usuarios usuario;

    @Before
    public void setUp() {
        usuario = new Usuarios();
    }

    @Test
    public void testSetNome() {
        usuario.setNome("Kaique");
        assertEquals("Kaique", usuario.getNome());
    }

    @Test
    public void testSetIdade() {
        usuario.setIdade("25");
        assertEquals("25", usuario.getIdade());
    }

    @Test
    public void testSetSexo() {
        usuario.setSexo("Masculino");
        assertEquals("Masculino", usuario.getSexo());
    }

    @Test
    public void testSetEmail() {
        usuario.setEmail("kaique@example.com");
        assertEquals("kaique@example.com", usuario.getEmail());
    }

    @Test
    public void testSetSenha() {
        usuario.setSenha("senha123");
        assertEquals("senha123", usuario.getSenha());
    }
    @Test
    public void testAdicionarEsportes() {
        Esporte futebol = new Esporte("Futebol");
        Esporte basquete = new Esporte("Basquete");

        usuario.adicionarEsporte(futebol);
        usuario.adicionarEsporte(basquete);

        assertEquals(2, usuario.getEsportes().size());

        assertEquals("Futebol", usuario.getEsportes().get(0).getNome());
        assertEquals("Basquete", usuario.getEsportes().get(1).getNome());
    }
    @Test
    public void testRemoverEsporte() {
        Esporte futebol = new Esporte("Futebol");
        Esporte basquete = new Esporte("Basquete");

        usuario.adicionarEsporte(futebol);
        usuario.adicionarEsporte(basquete);

        usuario.removerEsporte(futebol);

        assertEquals(1, usuario.getEsportes().size());

        assertEquals("Basquete", usuario.getEsportes().get(0).getNome());
    }
    @Before
    public void setUser() {
        grupo = new Grupo();
        Usuarios user1 = new Usuarios("kaique");
        Usuarios user2 = new Usuarios("Pereira");
        usuarios = new ArrayList<>();
        usuarios.add(user1);
        usuarios.add(user2);
    }

    @Test
    public void testSetMembros() {
        grupo.setMembros(usuarios);
        assertEquals(usuarios, grupo.getMembros());
    }

    @Test
    public void testGetMembros() {
        grupo.setMembros(usuarios);
        List<Usuarios> membros = grupo.getMembros();
        assertNotNull(membros);
        assertEquals(2, membros.size());
        assertEquals("kaique", membros.get(0).getNome());
        assertEquals("Pereira", membros.get(1).getNome());
    }

    }