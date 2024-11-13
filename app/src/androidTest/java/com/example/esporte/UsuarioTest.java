package com.example.esporte;

import static org.junit.Assert.assertEquals;
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
public class UsuarioTest {
    private Usuarios usuario;


    @Before
    public void setUp() {

        usuario = new Usuarios();
        usuario.setIdUsuario("12345");
        usuario.setNome("Teste Nome");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("senha123");
        usuario.setIdade("25");
        usuario.setSexo("Masculino");
        usuario.setFoto("linkFoto");
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("12345", usuario.getIdUsuario());
        assertEquals("Teste Nome", usuario.getNome());
        assertEquals("teste@teste.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals("25", usuario.getIdade());
        assertEquals("Masculino", usuario.getSexo());
        assertEquals("linkFoto", usuario.getFoto());
    }

    @Test
    public void testSalvar() {
        usuario.salvar();
    }

    @Test
    public void testEsportesList() {
        ArrayList<String> esportes = new ArrayList<>();
        esportes.add("Futebol");
        esportes.add("Vôlei");
        usuario.setEsportes(esportes);
        assertEquals(2, usuario.getEsportes().size());
        assertEquals("Futebol", usuario.getEsportes().get(0));
        assertEquals("Vôlei", usuario.getEsportes().get(1));
    }

    @Test
    public void testEndereco() {
        Endereco endereco = new Endereco();
        endereco.setLocalidade("São Paulo");
        usuario.setEndereco(endereco);
        assertEquals("São Paulo", usuario.getEndereco().getLocalidade());
    }
}
