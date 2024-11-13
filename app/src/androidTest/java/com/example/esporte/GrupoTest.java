package com.example.esporte;
import static com.example.esporte.view.EditarActivity.usuario;
import static org.junit.Assert.assertEquals;

import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
public class GrupoTest {
    @Test
    public void testSalvar() {
        Usuarios usuario = new Usuarios();
        usuario.setIdUsuario("12345");
        usuario.setNome("Teste Nome");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("senha123");
        usuario.setIdade("25");
        usuario.setSexo("Masculino");
        usuario.setFoto("linkFoto");
        List<Usuarios> list = new ArrayList<>();
        list.add(usuario);

        Grupo grupo = new Grupo();
        grupo.setNome("Grupo de Teste");
        grupo.setFoto("fotoUrl");
        grupo.setMembros(list);

        grupo.Salvar();

    }

    @Test
    public void testGettersAndSetters() {
        Grupo grupo = new Grupo();
        grupo.setNome("Grupo de Teste");
        grupo.setFoto("fotoUrl");

        assertEquals("Grupo de Teste", grupo.getNome());
        assertEquals("fotoUrl", grupo.getFoto());
    }
}
