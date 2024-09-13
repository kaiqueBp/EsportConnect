package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.PessoasAdapter;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PessoasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Usuarios> arrayUsuario = new ArrayList<>();
    private ArrayList<Usuarios> arrayAux = new ArrayList<>();
    private  String nome;
    private DrawerLayout drawerLayout;
    private Button filtro;
    private Spinner spinnerState, spinnerSex, spinnerCidade;
    private EditText pesquisa;
    private TextView titulo;
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
        drawerLayout = findViewById(R.id.drawer_layout);
        filtro = findViewById(R.id.btAplicaFiltro);
        spinnerCidade = findViewById(R.id.spinner_cidade);
        spinnerState = findViewById(R.id.spinner_state);
        spinnerSex = findViewById(R.id.spinner_sex);
        recyclerView = findViewById(R.id.recyclePessoas);
        pesquisa = findViewById(R.id.idPesquisa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        nome = intent.getStringExtra("nome");
        titulo = findViewById(R.id.nome_Esporte);
        titulo.setText(nome.toUpperCase());

        pesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    DatabaseReference database = ConfiguracaoFirebase.getFirebase().child("Usuarios");
                    ArrayList<Usuarios> arrayList = new ArrayList<>();
                    String query = s.toString().trim();

                    database.orderByChild("nome").startAt(query).endAt(query + "z")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    arrayList.clear(); // Limpa a lista antes de adicionar novos resultados
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot sportSnapshot : dataSnapshot.getChildren()) {
                                            Usuarios usuario = sportSnapshot.getValue(Usuarios.class);
                                            if (usuario != null) {
                                                for (String esporte : usuario.getEsportes()) {
                                                    if (esporte.equals(nome)) {
                                                        arrayList.add(usuario);
                                                        PessoasAdapter adapter = new PessoasAdapter(arrayList, PessoasActivity.this);
                                                        recyclerView.setAdapter(adapter);
                                                    }
                                                }
                                                Log.d("Pesquisa", "Esporte encontrado: " + usuario.getNome());
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
                    PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, PessoasActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estadoSelecionado = (String) parent.getItemAtPosition(position);
                ArrayList<String> cidades = getCidades(estadoSelecionado);
                HashSet<String> uniqueCidades = new HashSet<>(cidades);
                cidades.clear();
                cidades.addAll(uniqueCidades);
                ArrayAdapter<String> adapterCidade = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, cidades);
                spinnerCidade.setAdapter(adapterCidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerCidade.setEnabled(false);
            }
        });


        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadUsuarios(new PessoasActivity.Callback() {
//                    @Override
//                    public void onDataLoaded() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//                        // Tratar erro
//                    }
//                });
                arrayAux = new ArrayList<>(arrayUsuario);
                String sexo = "";
                if(spinnerSex.getSelectedItem().toString().equals("Masculino")){
                    sexo = "M";
                }
                if(spinnerSex.getSelectedItem().toString().equals("Feminino")){
                    sexo = "F";
                }
                if(spinnerSex.getSelectedItem().toString().equals("Outro")){
                    sexo = "O";
                }


                if(!spinnerState.getSelectedItem().toString().equals("Selecione um Estado") && sexo != ""){
                    arrayUsuario.clear();
                    for(Usuarios u : arrayAux){
                        if(u.getSexo().equals(sexo) && u.getEndereco().getLocalidade().equals(spinnerCidade.getSelectedItem().toString())
                                &&  u.getEndereco().getLocalidade().equals(spinnerCidade.getSelectedItem().toString())){
                            arrayUsuario.add(u);
                        }
                    }
                }


                if(!spinnerState.getSelectedItem().toString().equals("Selecione um Estado") && sexo ==""){
                    arrayUsuario.clear();
                    for(Usuarios u : arrayAux){
                        if(u.getEndereco().getUf().equals(spinnerState.getSelectedItem().toString()) &&
                                u.getEndereco().getLocalidade().equals(spinnerCidade.getSelectedItem().toString())){
                            arrayUsuario.add(u);
                        }
                    }
                }

                if(sexo != "" && spinnerState.getSelectedItem().toString().equals("Selecione um Estado")){
                    arrayUsuario.clear();
                    for(Usuarios u : arrayAux){
                        if(u.getSexo().equals(sexo)){
                            arrayUsuario.add(u);
                        }
                    }
                }
                PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, PessoasActivity.this);
                recyclerView.setAdapter(adapter);

                arrayUsuario = new ArrayList<>(arrayAux);
            }
        });


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
    public void Limpar(View view){
        PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, PessoasActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<String> getCidades(String estadoSelecionado) {
        ArrayList<String> cidades = new ArrayList<>();
        for (Usuarios u : arrayUsuario) {
            if (u.getEndereco().getUf().equals(estadoSelecionado)) {
                cidades.add(u.getEndereco().getLocalidade());
            }
        }
        return cidades;
    }
    public void AbrirFiltragem(View view){
        drawerLayout.openDrawer(GravityCompat.END);
//        ArrayList<String> arraySexo = new ArrayList<>();;
//        for(Usuarios u : arrayUsuario){
//            arraySexo.add(u.getEndereco().getLocalidade());
//        }
//        LinkedHashSet<String> set = new LinkedHashSet<>(arraySexo);
//        ArrayList<String> listaSemRepetidos = new ArrayList<>(set);
//
//       ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaSemRepetidos);
////        spinnerState.setAdapter(adapter);
    }

    private void loadUsuarios(final Callback callback) {
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase().child("Usuarios");
        //Query query = usuarioRef.orderByChild("esportes/01").equalTo("Basquete");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String em = auth.getCurrentUser().getEmail();
                    if(!Objects.equals(userSnapshot.child("email").getValue(String.class), em)){
                        DataSnapshot esportesSnapshot = userSnapshot.child("esportes");

                        for (DataSnapshot esporteSnapshot : esportesSnapshot.getChildren()) {
                            String esporte = esporteSnapshot.getValue(String.class);

                            if (esporte != null && esporte.equals(nome)) {
                                Usuarios usuarios = userSnapshot.getValue(Usuarios.class);
                                arrayUsuario.add(usuarios);
                            }
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

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}