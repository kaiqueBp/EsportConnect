package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ListarConversasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Conversa> Listaconversa = new ArrayList<>();
    private ListarConversaAdapter adapter;
    private DatabaseReference databaseRef;
    private DatabaseReference database;
    private ChildEventListener childEventListener;
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
        adapter = new ListarConversaAdapter(Listaconversa, ListarConversasActivity.this);
        recyclerView = findViewById(R.id.idListarConversa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        recyclerView.setAdapter(adapter);
        databaseRef = ConfiguracaoFirebase.getFirebase();
        String identificarUsuario = Base64Custom.codificar(ConfiguracaoFirebase.getAutenticacao().getCurrentUser().getEmail());
        database =  databaseRef.child("conversas").child(identificarUsuario);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
                Listaconversa.add(conversa);
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