package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.MenssagensAdapter;
import com.example.esporte.model.Mensagem;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Usuarios pessoa;
    private ImageView foto;
    private TextView nome, mensagem;
    private String idRemetente, idDestinatario;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private MenssagensAdapter adapter;
    private List<Mensagem> menssagens = new ArrayList<>();
    private DatabaseReference database;
    private DatabaseReference mensagemRef;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        foto = findViewById(R.id.idFt);
        nome = findViewById(R.id.idNm);
        mensagem = findViewById(R.id.editMenssagem);
        recyclerView = findViewById(R.id.recyclerMenssagem);
        auth = ConfiguracaoFirebase.getAutenticacao();
        idRemetente = Base64Custom.codificar(auth.getCurrentUser().getEmail());
        toolbar = findViewById(R.id.idToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new MenssagensAdapter(menssagens,getApplicationContext());

          RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);



        Intent intent = getIntent();
        pessoa = (Usuarios) intent.getSerializableExtra("usuarioCLicado");
        getSupportActionBar().setTitle("");
        nome.setText(pessoa.getNome());
        Glide.with(this).load(pessoa.getFoto()).into(foto);
        idDestinatario = pessoa.getIdUsuario();
        database = ConfiguracaoFirebase.getFirebase();
        mensagemRef = database.child("Mensagens")
                .child(idRemetente)
                .child(idDestinatario);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void enviarMensagem(View view){
        if(!mensagem.getText().toString().isEmpty()){
            Mensagem msg = new Mensagem();
            msg.setIdUsuario(idRemetente);
            msg.setMensagem(mensagem.getText().toString());

            Salvar(idRemetente, idDestinatario, msg);
            Salvar(idDestinatario, idRemetente, msg);
        }else {
            //mensagem.setError("Digite uma mensagem");
        }
    }
    private void Salvar(String idRemetente, String idDestinatario, Mensagem m){
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();;
        DatabaseReference mensagemRef = database.child("Mensagens");
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(m);
        mensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarMenssagem();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemRef.removeEventListener(childEventListener);
    }

    private void RecuperarMenssagem(){
        childEventListener = mensagemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem msg = snapshot.getValue(Mensagem.class);
                menssagens.add(msg);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}