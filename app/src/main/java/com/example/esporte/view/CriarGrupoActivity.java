package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.GrupoAdapter;
import com.example.esporte.config.GrupoSelecionadoAdapter;
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CriarGrupoActivity extends AppCompatActivity {
    private RecyclerView membrosRecyclerView, membrosSelecionadosRecyclerView;
    private GrupoAdapter grupoAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private ArrayList<Conversa> usuarios = new ArrayList<>();
    private Conversa conversaSelecionada;
    private ArrayList<Usuarios> usuarioSelecionada = new ArrayList<>();
    private Toolbar toolbar;
    private TextView quantidadeMembros;
    private int tamanho;
    private FloatingActionButton avancar;
    private Grupo grupo;
    private List<Usuarios> auxiliar = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_grupo);
        membrosRecyclerView = findViewById(R.id.idMembros);
        membrosSelecionadosRecyclerView = findViewById(R.id.idMembrosSelecionados);
        quantidadeMembros = findViewById(R.id.idGrupQtd);
        toolbar = findViewById(R.id.idToolbarGrupo);
        avancar = findViewById(R.id.idFloatAvancar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();

        membrosSelecionadosRecyclerView.setVisibility(View.GONE);

        if(extras.containsKey("adicionar")){
            grupo = (Grupo) extras.getSerializable("adicionar");

            //setTitle(grupo.getNome());
            PegarUsuariosNaoSelecionados(grupo.getMembros(), grupo.getId());
        }else{
            usuarios = (ArrayList<Conversa>) extras.getSerializable("lista");
            tamanho = usuarios.size();
            setarQuantidade();
        }
        //Intent intent = getIntent();



        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(usuarioSelecionada, this, new GrupoSelecionadoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Usuarios usuario = usuarioSelecionada.get(position);
                conversaSelecionada = new Conversa();
                conversaSelecionada.setUsuarioExibicao(usuario);
                usuarioSelecionada.remove(position);
                grupoSelecionadoAdapter.notifyItemRemoved(position);
                usuarios.add(conversaSelecionada);
                grupoAdapter.notifyItemInserted(usuarios.size());
                setarQuantidade();
                if(usuarioSelecionada.size() == 0){
                    membrosSelecionadosRecyclerView.setVisibility(View.GONE);
                }
            }
        });
        grupoAdapter = new GrupoAdapter(usuarios, this, new GrupoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                conversaSelecionada = usuarios.get(position);
                usuarios.remove(position);
                grupoAdapter.notifyItemRemoved(position);


                usuarioSelecionada.add(conversaSelecionada.getUsuarioExibicao());
                grupoSelecionadoAdapter.notifyItemInserted(usuarioSelecionada.size());
                membrosSelecionadosRecyclerView.setVisibility(View.VISIBLE);
                setarQuantidade();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        membrosRecyclerView.setLayoutManager(layoutManager);
        membrosRecyclerView.setHasFixedSize(true);
        membrosRecyclerView.setAdapter(grupoAdapter);

        membrosSelecionadosRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        membrosSelecionadosRecyclerView.setHasFixedSize(true);
        membrosSelecionadosRecyclerView.setAdapter(grupoSelecionadoAdapter);

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extras.containsKey("adicionar")){
                    for(Usuarios membros : grupo.getMembros()){
                        auxiliar.add(membros);
                        grupo.setMembros(auxiliar);
                    }
                    grupo.Salvar();
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), CadastroGrupoActivity.class);
                    intent.putExtra("usuariosSelecionados", usuarioSelecionada);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setarQuantidade(){
        String titulo = usuarioSelecionada.size() + " de " + tamanho + " selecionados";
        quantidadeMembros.setText(titulo);
    }

    public void PegarUsuariosNaoSelecionados(List<Usuarios> u, String id){
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebase();
        ConfiguracaoFirebase.IDUsuarioLogado();
        String identificarUsuario = ConfiguracaoFirebase.IDUsuarioLogado();
        DatabaseReference database =  databaseRef.child("Conversas").child(identificarUsuario);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conversa conversa = new Conversa();
                String idRem = ConfiguracaoFirebase.IDUsuarioLogado();
                List<String> membros = new ArrayList<>();
                membros.add(id);
                for(Usuarios usuario : u){
                    membros.add(usuario.getIdUsuario());
                }
                for(DataSnapshot dados : snapshot.getChildren()){
                        idRem = dados.getKey();
                        if(!membros.contains(idRem)){
                            Conversa conversaSelecionada = new Conversa();
                            conversaSelecionada = dados.getValue(Conversa.class);
                           usuarios.add(dados.getValue(Conversa.class));
                           auxiliar.add(conversaSelecionada.getUsuarioExibicao());
                        }
                }
                grupoAdapter.notifyDataSetChanged();
                tamanho = usuarios.size();
                //grupoSelecionadoAdapter.notifyDataSetChanged();
                setarQuantidade();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}