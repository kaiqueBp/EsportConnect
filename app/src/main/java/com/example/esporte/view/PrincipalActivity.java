package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.EsporteAdapter;
import com.example.esporte.model.Esporte;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Esporte> arrayEsporte = new ArrayList<>();
    private TextInputEditText pesquisa;
    private TextView perfil;
    private BottomNavigationView navBotton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        verificaLogin();
        pesquisa = findViewById(R.id.idPes);
        perfil = findViewById(R.id.idPerfil);
        navBotton = findViewById(R.id.idBotton);
        navBotton.setSelectedItemId(R.id.navHome);

        navBotton.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome){
                    return true;
                }
                if(item.getItemId() == R.id.navConversa){
                    Intent intent = new Intent(PrincipalActivity.this, ListarConversasActivity.class);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.navPerfil){
                    Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
        //loadEsportes();
        recyclerView = findViewById(R.id.idRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        loadEsportes(new Callback() {
            @Override
            public void onDataLoaded() {
                EsporteAdapter adapter = new EsporteAdapter(arrayEsporte, PrincipalActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });

        pesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Método vazio - pode ser implementado se necessário
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Se a pesquisa estiver vazia, não faça a consulta
                if (s.length() != 0) {
                    DatabaseReference database = ConfiguracaoFirebase.getFirebase().child("Esportes");
                    ArrayList <Esporte> arrayList = new ArrayList<>();
                    String query = s.toString().trim();

                    database.orderByChild("nome").startAt(query).endAt(query + "z")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    arrayList.clear(); // Limpa a lista antes de adicionar novos resultados
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot sportSnapshot : dataSnapshot.getChildren()) {
                                            Esporte esporte = sportSnapshot.getValue(Esporte.class);
                                            if (esporte != null) {

                                                arrayList.add(esporte);
                                                EsporteAdapter adapter = new EsporteAdapter(arrayList, PrincipalActivity.this);
                                                recyclerView.setAdapter(adapter);
                                                Log.d("Pesquisa", "Esporte encontrado: " + esporte.getNome());
                                            }
                                        }
                                    } else {
                                        Log.d("Pesquisa", "Nenhum esporte encontrado.");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("Pesquisa", "Erro ao buscar: ", databaseError.toException());
                                }
                            });
                }else{
                    EsporteAdapter adapter = new EsporteAdapter(arrayEsporte, PrincipalActivity.this);
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                // Método vazio - pode ser implementado se necessário
            }
        });


    }
    public void verificaLogin(){
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void loadEsportes(final Callback callback) {
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();
        database = database.child("Esportes");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot esporteSnapshot : dataSnapshot.getChildren()) {
                    Esporte esporte = esporteSnapshot.getValue(Esporte.class);
                    arrayEsporte.add(esporte);
                }
                callback.onDataLoaded();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Erro", "Erro ao carregar esportes", error.toException());
                callback.onError();
            }
        });
    }

    public interface Callback {
        void onDataLoaded();
        void onError();
    }
}