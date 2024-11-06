package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.EsporteAdapter;
import com.example.esporte.model.Esporte;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PrincipalFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Esporte> arrayEsporte = new ArrayList<>();
    private TextInputEditText pesquisa;
    private TextView perfil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificaLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teste, container, false);

        pesquisa = view.findViewById(R.id.idPes);
        perfil = view.findViewById(R.id.idPerfil);
        verificaLogin();

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PerfilActivity.class);
                startActivity(intent);
            }
        });
        //loadEsportes();
        recyclerView = view.findViewById(R.id.idRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        loadEsportes(new PrincipalActivity.Callback() {
            @Override
            public void onDataLoaded() {
                EsporteAdapter adapter = new EsporteAdapter(arrayEsporte, getActivity());
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
                                                EsporteAdapter adapter = new EsporteAdapter(arrayList, getActivity());
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
                    EsporteAdapter adapter = new EsporteAdapter(arrayEsporte, getActivity());
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                // Método vazio - pode ser implementado se necessário
            }
        });


        return view;
    }
    public void verificaLogin(){
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            //finish();
        }
    }
    private void loadEsportes(final PrincipalActivity.Callback callback) {
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