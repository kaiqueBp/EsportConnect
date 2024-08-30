package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.PessoasAdapter;
import com.example.esporte.model.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PessoasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Usuarios> arrayUsuario = new ArrayList<>();
    private  String nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pessoas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclePessoas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        nome = intent.getStringExtra("nome");
        loadUsuarios(new PessoasActivity.Callback() {
            @Override
            public void onDataLoaded() {
                PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, PessoasActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }

    private void loadUsuarios(final Callback callback) {
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase().child("Usuarios");
        //Query query = usuarioRef.orderByChild("esportes/01").equalTo("Basquete");

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot esportesSnapshot = userSnapshot.child("esportes");

                    for (DataSnapshot esporteSnapshot : esportesSnapshot.getChildren()) {
                        String esporte = esporteSnapshot.getValue(String.class);

                        if (esporte != null && esporte.equals(nome)) {
                            Usuarios usuarios = userSnapshot.getValue(Usuarios.class);
                            arrayUsuario.add(usuarios);
                        }
                    }
                }
                callback.onDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("erro", "Erro ao ler dados", error.toException());
                callback.onError();
            }
        });
    }
    public interface Callback {
        void onDataLoaded();
        void onError();
    }
}