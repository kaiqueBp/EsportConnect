package com.example.esporte.view;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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


public class ConversasFragment extends Fragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        criarGrupo = view.findViewById(R.id.idCriarGrupo);
        toolbarConversa =  view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbarConversa);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setTitle("Conversas");
            toolbarConversa.setTitleTextColor(getResources().getColor(R.color.white));
        }

        adapter = new ListarConversaAdapter(Listaconversa, getActivity());
        recyclerView =  view.findViewById(R.id.idListarConversa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        databaseRef = ConfiguracaoFirebase.getFirebase();
        String identificarUsuario = Base64Custom.codificar(ConfiguracaoFirebase.getAutenticacao().getCurrentUser().getEmail());
        database =  databaseRef.child("Conversas").child(identificarUsuario);

        criarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CriarGrupoActivity.class);
                intent.putExtra("lista", apenasConversas);
                startActivity(intent);
                //Toast.makeText(ListarConversasActivity.this, "Criar Grupo", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Listaconversa.clear();
        adapter.notifyDataSetChanged();
        RecuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        database.removeEventListener(childEventListener);
    }

    public void RecuperarConversas(){
        apenasConversas.clear();
        childEventListener = database.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);

                if (conversa != null && !Listaconversa.contains(conversa)) {
                    if (!conversa.getIsGroup().equals("true")) {
                        apenasConversas.add(conversa);
                    }
                    Listaconversa.add(conversa);
                    adapter.notifyItemInserted(Listaconversa.size() - 1);  // Notifica apenas o item adicionado
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                if (conversa != null) {
                    int position = Listaconversa.indexOf(conversa);
                    if (position != -1) {
                        Listaconversa.set(position, conversa);
                        adapter.notifyItemChanged(position);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                if (conversa != null) {
                    int position = Listaconversa.indexOf(conversa);
                    if (position != -1) {
                        Listaconversa.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                }
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