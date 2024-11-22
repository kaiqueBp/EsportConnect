package com.example.esporte.view;

import static com.example.esporte.view.Teste.navView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.PessoasAdapter;
import com.example.esporte.model.Usuarios;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


public class PessoasFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Usuarios> arrayUsuario = new ArrayList<>();
    private ArrayList<Usuarios> arrayAux = new ArrayList<>();
    private  String nome;
    private DrawerLayout drawerLayout;
    private Button filtro, filtragem, limparFiltro;
    private Spinner spinnerState, spinnerSex, spinnerCidade;
    private EditText pesquisa;
    private TextView titulo, vazio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pessoas, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        filtro = view.findViewById(R.id.btAplicaFiltro);
        filtragem = view.findViewById(R.id.btFiltrar);
        limparFiltro = view.findViewById(R.id.btLimpar);
        spinnerCidade = view.findViewById(R.id.spinner_cidade);
        spinnerState = view.findViewById(R.id.spinner_state);
        spinnerSex = view.findViewById(R.id.spinner_sex);
        recyclerView = view.findViewById(R.id.recyclePessoas);
        pesquisa = view.findViewById(R.id.idPesquisa);
        vazio = view.findViewById(R.id.idNenhum);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getArguments() != null) {
            nome = getArguments().getString("nome");
        }

        titulo = view.findViewById(R.id.nome_Esporte);
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
                    String query = s.toString().trim().toUpperCase();

                    database.orderByChild("nome").startAt(query).endAt(query + "z")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    arrayList.clear();
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot sportSnapshot : dataSnapshot.getChildren()) {
                                            Usuarios usuario = sportSnapshot.getValue(Usuarios.class);
                                            if (usuario != null) {
                                                for (String esporte : usuario.getEsportes()) {
                                                    if (esporte.equals(nome)) {
                                                        arrayList.add(usuario);
                                                        PessoasAdapter adapter = new PessoasAdapter(arrayList, getActivity());
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
                    PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, getActivity());
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
                ArrayAdapter<String> adapterCidade = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cidades);
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
                PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, getActivity());
                recyclerView.setAdapter(adapter);

                arrayUsuario = new ArrayList<>(arrayAux);
            }
        });


        loadUsuarios(new Callback() {
            @Override
            public void onDataLoaded() {
                if(arrayUsuario.isEmpty()){
                    vazio.setVisibility(View.VISIBLE);
                }
                PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, getActivity());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });

        filtragem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        limparFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PessoasAdapter adapter = new PessoasAdapter(arrayUsuario, getActivity());
                recyclerView.setAdapter(adapter);
                spinnerSex.setSelection(0);
                spinnerState.setSelection(0);
            }
        });


        return view;
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

    private void loadUsuarios(Callback callback) {
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
    public void onStart() {
        super.onStart();

    }

}