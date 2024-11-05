package com.example.esporte.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.GrupoAdapter;
import com.example.esporte.config.ListarConversaAdapter;
import com.example.esporte.config.ListarPessoasGrupoAdapter;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PerfilGrupoActivity extends AppCompatActivity {
    private Grupo grupo;
    private TextView nomeGrupo, adicionar;
    private ImageView img;
    private ListarPessoasGrupoAdapter adapter;
    private RecyclerView recyclerView;
    //private ArrayList<Usuarios> listaUsuarios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_grupo);
        nomeGrupo = findViewById(R.id.idPNomeGrop);
        adicionar = findViewById(R.id.idAdd);
        img = findViewById(R.id.idImgPGrupo);
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) extras.getSerializable("perfilGrupo");

        adapter = new ListarPessoasGrupoAdapter(grupo.getMembros(), PerfilGrupoActivity.this, new GrupoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Usuarios usuario = grupo.getMembros().get(position);
                String id = usuario.getIdUsuario();
                if(!usuario.getIdUsuario().equals(ConfiguracaoFirebase.IDUsuarioLogado())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PerfilGrupoActivity.this);
                    builder.setMessage("Excluir conversa?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            deletarUsuario(id,grupo.getId(), new Callback() {
//                                @Override
//                                public void onDataLoaded() {
//                                    finish();
//                                    startActivity(getIntent());
//                                }
//
//                                @Override
//                                public void onError() {
//
//                                }
//                            });
                            deletarConversa(id, new Callback() {
                                @Override
                                public void onDataLoaded() {
                                    deletarUsuario(id, grupo.getId(), new Callback() {
                                        @Override
                                        public void onDataLoaded() {
                                            finish();
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });

                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Fechar opção de excluir conversa
                        }
                    });
                    builder.show();
                }
            }
        });
        recyclerView = findViewById(R.id.idPesGrupo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        recyclerView.setAdapter(adapter);

        nomeGrupo.setText("Membros: "+grupo.getMembros().size());

        if(grupo.getFoto() != null || grupo.getFoto() == ""){
            Glide.with(this).load(grupo.getFoto()).into(img);
        }
        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PerfilGrupoActivity.this, CriarGrupoActivity.class);
                intent.putExtra("adicionar", grupo);
                startActivity(intent);
                finish();
            }
        });
    }
    private void deletarUsuario(String idPessoa, String idGrupo, final Callback callback) {
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("grupos");
        mensagensRef.child(idGrupo)
                .child("membros")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //DataSnapshot membrosSnapshot = dataSnapshot.child("membros");
                            List<Usuarios> membros = new ArrayList<>();
                            for (DataSnapshot membro : dataSnapshot.getChildren()) {
                                String idMembro = membro.child("idUsuario").getValue(String.class);
                                // Comparar o ID do membro com o ID que você deseja remover
                                if (!idMembro.equals(idPessoa)) {
                                    // Remover o membro
                                    membros.add(membro.getValue(Usuarios.class));
                                }
                            }
                            dataSnapshot.getRef().setValue(membros);
                            callback.onDataLoaded();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ExcluirMensagem", "Erro ao excluir mensagem", databaseError.toException());
                        callback.onError();
                    }
                });
    }
    private void deletarConversa(String idPessoa, final Callback callback){
        DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebase().child("Conversas");
        conversaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot membro : snapshot.getChildren()){
                    if(membro.getKey().equals(idPessoa)){
                        DataSnapshot snap = membro.child(grupo.getId());
                        snap.getRef().removeValue();
                    }
                    if(membro.getKey().equals(ConfiguracaoFirebase.IDUsuarioLogado())){
                        DataSnapshot membrosSnapshot = membro.child(grupo.getId()).child("grupo").child("membros");
                        List<Usuarios> membros = new ArrayList<>();
                        for (DataSnapshot m : membrosSnapshot.getChildren()){
                            String idMembro = m.child("idUsuario").getValue(String.class);
                            if (!idMembro.equals(idPessoa)) {
                                // Remover o membro
                                membros.add(m.getValue(Usuarios.class));
                            }
                        }
                        membrosSnapshot.getRef().setValue(membros);
                    }
                }
                callback.onDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public interface Callback {
        void onDataLoaded();
        void onError();
    }
}