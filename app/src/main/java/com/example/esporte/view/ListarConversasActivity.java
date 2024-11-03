package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


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

import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.ListarConversaAdapter;
import com.example.esporte.model.Conversa;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ListarConversasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Conversa> Listaconversa = new ArrayList<>();
    private ArrayList<Conversa> apenasConversas = new ArrayList<>();
    private ListarConversaAdapter adapter;
    private DatabaseReference databaseRef;
    private DatabaseReference database;
    private ChildEventListener childEventListener;
    private Toolbar toolbarConversa;
    private View criarGrupo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar_conversas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        criarGrupo = findViewById(R.id.idCriarGrupo);
        toolbarConversa = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarConversa);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Conversas");
            toolbarConversa.setTitleTextColor(getResources().getColor(R.color.white));
        }
        adapter = new ListarConversaAdapter(Listaconversa, ListarConversasActivity.this);
        recyclerView = findViewById(R.id.idListarConversa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        recyclerView.setAdapter(adapter);
        databaseRef = ConfiguracaoFirebase.getFirebase();
        String identificarUsuario = Base64Custom.codificar(ConfiguracaoFirebase.getAutenticacao().getCurrentUser().getEmail());
        database =  databaseRef.child("Conversas").child(identificarUsuario);

        criarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ListarConversasActivity.this, CriarGrupoActivity.class);
                intent.putExtra("lista", apenasConversas);
                startActivity(intent);
                //Toast.makeText(ListarConversasActivity.this, "Criar Grupo", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onStart() {
        super.onStart();
        Listaconversa.clear();
        adapter.notifyDataSetChanged();
        RecuperarConversas();
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.removeEventListener(childEventListener);
    }

    public void RecuperarConversas(){

        childEventListener = database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                if (!Listaconversa.contains(conversa)) {
                    if(!conversa.getIsGroup().equals("true")){
                        apenasConversas.add(conversa);
                    }
                    Listaconversa.add(conversa);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                Listaconversa.remove(conversa);
                adapter.notifyDataSetChanged();
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